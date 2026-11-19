package com.sarvodhaya.store.data.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.sarvodhaya.store.domain.model.Address
import com.sarvodhaya.store.domain.model.CartLine
import com.sarvodhaya.store.domain.model.Order
import com.sarvodhaya.store.domain.model.OrderItem
import com.sarvodhaya.store.domain.model.OrderStatus
import com.sarvodhaya.store.domain.model.Pricing
import com.sarvodhaya.store.domain.repository.AuthRepository
import com.sarvodhaya.store.domain.repository.CatalogRepository
import com.sarvodhaya.store.domain.repository.OrderRepository
import com.sarvodhaya.store.domain.repository.ShopRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class OrderRepositoryImpl(
    private val auth: AuthRepository,
    private val shop: ShopRepository,
    private val catalog: CatalogRepository,
    private val db: FirebaseFirestore?
) : OrderRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    override val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var userId: String? = null
    private var listener: ListenerRegistration? = null

    init {
        scope.launch {
            auth.currentUser.collect { profile -> bind(profile?.uid) }
        }
    }

    private fun bind(newUserId: String?) {
        if (userId == newUserId) return
        userId = newUserId
        listener?.remove()
        listener = null

        if (newUserId == null || db == null) {
            _orders.value = emptyList()
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        listener = db.collection(FirestorePaths.USERS).document(newUserId)
            .collection(FirestorePaths.ORDERS)
            .orderBy("placedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false
                if (error == null && snapshot != null) {
                    _orders.value = snapshot.documents.map { it.toOrder() }
                }
            }
    }

    override suspend fun placeOrder(address: Address, paymentMethod: String): Result<Order> =
        withContext(Dispatchers.IO) {
            val lines = shop.cart.value.mapNotNull { item ->
                catalog.products.value.firstOrNull { it.id == item.productId }
                    ?.let { CartLine(it, item.quantity) }
            }
            if (lines.isEmpty()) return@withContext failure("Your cart is empty")
            if (!address.isComplete) {
                return@withContext failure("Add a complete delivery address")
            }

            val subtotal = lines.sumOf { it.lineTotal }
            val deliveryFee = Pricing.deliveryFeeFor(subtotal)
            val order = Order(
                id = db?.collection(FirestorePaths.ORDERS)?.document()?.id
                    ?: UUID.randomUUID().toString(),
                items = lines.map { line ->
                    OrderItem(
                        productId = line.product.id,
                        name = line.product.name,
                        price = line.product.price,
                        quantity = line.quantity,
                        unit = line.product.unit,
                        imageUrl = line.product.imageUrl
                    )
                },
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                total = subtotal + deliveryFee,
                address = address,
                paymentMethod = paymentMethod,
                status = OrderStatus.PLACED,
                placedAt = System.currentTimeMillis()
            )

            try {
                val currentUserId = userId
                if (currentUserId != null && db != null) {
                    db.collection(FirestorePaths.USERS).document(currentUserId)
                        .collection(FirestorePaths.ORDERS).document(order.id)
                        .set(order.toMap()).await()
                }
                _orders.value = listOf(order) + _orders.value
                shop.clearCart()
                Result.success(order)
            } catch (e: Exception) {
                Result.failure(IllegalStateException(e.message ?: "Could not place your order"))
            }
        }

    private fun failure(message: String): Result<Order> = Result.failure(IllegalStateException(message))
}

private fun Order.toMap(): Map<String, Any> = mapOf(
    "items" to items.map { item ->
        mapOf(
            "productId" to item.productId,
            "name" to item.name,
            "price" to item.price,
            "quantity" to item.quantity,
            "unit" to item.unit,
            "imageUrl" to item.imageUrl
        )
    },
    "subtotal" to subtotal,
    "deliveryFee" to deliveryFee,
    "total" to total,
    "address" to mapOf(
        "fullName" to address.fullName,
        "phone" to address.phone,
        "line1" to address.line1,
        "city" to address.city,
        "state" to address.state,
        "pincode" to address.pincode
    ),
    "paymentMethod" to paymentMethod,
    "status" to status.name,
    "placedAt" to placedAt
)

private fun DocumentSnapshot.toOrder(): Order {
    @Suppress("UNCHECKED_CAST")
    val itemMaps = get("items") as? List<Map<String, Any?>>
    val items = itemMaps.orEmpty().map { item ->
        OrderItem(
            productId = item["productId"]?.toString().orEmpty(),
            name = item["name"]?.toString().orEmpty(),
            price = (item["price"] as? Number)?.toDouble() ?: 0.0,
            quantity = (item["quantity"] as? Number)?.toInt() ?: 0,
            unit = item["unit"]?.toString().orEmpty(),
            imageUrl = item["imageUrl"]?.toString().orEmpty()
        )
    }
    @Suppress("UNCHECKED_CAST")
    val addressMap = get("address") as? Map<String, Any?>
    val address = Address(
        fullName = addressMap?.get("fullName")?.toString().orEmpty(),
        phone = addressMap?.get("phone")?.toString().orEmpty(),
        line1 = addressMap?.get("line1")?.toString().orEmpty(),
        city = addressMap?.get("city")?.toString().orEmpty(),
        state = addressMap?.get("state")?.toString().orEmpty(),
        pincode = addressMap?.get("pincode")?.toString().orEmpty()
    )
    return Order(
        id = id,
        items = items,
        subtotal = getDouble("subtotal") ?: 0.0,
        deliveryFee = getDouble("deliveryFee") ?: 0.0,
        total = getDouble("total") ?: 0.0,
        address = address,
        paymentMethod = getString("paymentMethod") ?: "Cash on delivery",
        status = OrderStatus.from(getString("status")),
        placedAt = getLong("placedAt") ?: System.currentTimeMillis()
    )
}
