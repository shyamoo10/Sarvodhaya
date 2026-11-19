package com.sarvodhaya.store.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sarvodhaya.store.domain.model.CartItem
import com.sarvodhaya.store.domain.repository.AuthRepository
import com.sarvodhaya.store.domain.repository.ShopRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Cart and wishlist live in memory for guests and mirror to Firestore once a
 * user signs in.
 */
class ShopRepositoryImpl(
    auth: AuthRepository,
    private val db: FirebaseFirestore?
) : ShopRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    override val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _wishlist = MutableStateFlow<List<String>>(emptyList())
    override val wishlist: StateFlow<List<String>> = _wishlist.asStateFlow()

    private var userId: String? = null
    private var cartListener: ListenerRegistration? = null
    private var wishlistListener: ListenerRegistration? = null

    init {
        scope.launch {
            auth.currentUser.collect { profile -> bind(profile?.uid) }
        }
    }

    private fun bind(newUserId: String?) {
        if (userId == newUserId) return
        userId = newUserId
        cartListener?.remove()
        wishlistListener?.remove()
        cartListener = null
        wishlistListener = null

        if (newUserId == null || db == null) {
            _cart.value = emptyList()
            _wishlist.value = emptyList()
            return
        }

        val userDoc = db.collection(FirestorePaths.USERS).document(newUserId)
        cartListener = userDoc.collection(FirestorePaths.CART).addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                _cart.value = snapshot.documents.mapNotNull { document ->
                    val quantity = document.getLong("quantity")?.toInt() ?: 0
                    if (quantity > 0) CartItem(document.id, quantity) else null
                }
            }
        }
        wishlistListener = userDoc.collection(FirestorePaths.WISHLIST).addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                _wishlist.value = snapshot.documents.map { it.id }
            }
        }
    }

    override fun addToCart(productId: String, quantity: Int) {
        val existing = _cart.value.firstOrNull { it.productId == productId }?.quantity ?: 0
        setQuantity(productId, existing + quantity)
    }

    override fun setQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        val updated = _cart.value.toMutableList()
        val index = updated.indexOfFirst { it.productId == productId }
        if (index >= 0) updated[index] = updated[index].copy(quantity = quantity)
        else updated.add(CartItem(productId, quantity))
        _cart.value = updated
        persistCartItem(productId, quantity)
    }

    override fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filterNot { it.productId == productId }
        val currentUserId = userId ?: return
        db?.collection(FirestorePaths.USERS)?.document(currentUserId)
            ?.collection(FirestorePaths.CART)?.document(productId)?.delete()
    }

    override fun clearCart() {
        val snapshot = _cart.value
        _cart.value = emptyList()
        val currentUserId = userId ?: return
        val collection = db?.collection(FirestorePaths.USERS)?.document(currentUserId)
            ?.collection(FirestorePaths.CART) ?: return
        snapshot.forEach { item -> collection.document(item.productId).delete() }
    }

    override fun toggleWishlist(productId: String) {
        val wasWishlisted = _wishlist.value.contains(productId)
        _wishlist.value = if (wasWishlisted) {
            _wishlist.value - productId
        } else {
            _wishlist.value + productId
        }
        val currentUserId = userId ?: return
        val document = db?.collection(FirestorePaths.USERS)?.document(currentUserId)
            ?.collection(FirestorePaths.WISHLIST)?.document(productId) ?: return
        if (wasWishlisted) {
            document.delete()
        } else {
            document.set(mapOf("addedAt" to System.currentTimeMillis()))
        }
    }

    private fun persistCartItem(productId: String, quantity: Int) {
        val currentUserId = userId ?: return
        db?.collection(FirestorePaths.USERS)?.document(currentUserId)
            ?.collection(FirestorePaths.CART)?.document(productId)
            ?.set(mapOf("productId" to productId, "quantity" to quantity, "updatedAt" to System.currentTimeMillis()))
    }
}
