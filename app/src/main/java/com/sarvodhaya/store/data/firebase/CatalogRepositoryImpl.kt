package com.sarvodhaya.store.data.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sarvodhaya.store.data.seed.DemoCatalog
import com.sarvodhaya.store.domain.model.Banner
import com.sarvodhaya.store.domain.model.Category
import com.sarvodhaya.store.domain.model.Product
import com.sarvodhaya.store.domain.repository.CatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CatalogRepositoryImpl(private val db: FirebaseFirestore?) : CatalogRepository {

    private val _categories = MutableStateFlow(DemoCatalog.categories)
    override val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _banners = MutableStateFlow(DemoCatalog.banners)
    override val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _products = MutableStateFlow(DemoCatalog.products)
    override val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        val store = db
        if (store != null) {
            store.collection(FirestorePaths.CATEGORIES).addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    val items = snapshot.documents.mapNotNull { it.toCategory() }
                    if (items.isNotEmpty()) _categories.value = items
                }
            }
            store.collection(FirestorePaths.BANNERS).addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    val items = snapshot.documents.mapNotNull { it.toBanner() }
                    if (items.isNotEmpty()) _banners.value = items
                }
            }
            store.collection(FirestorePaths.PRODUCTS).addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    val items = snapshot.documents.mapNotNull { it.toProduct() }
                    if (items.isNotEmpty()) _products.value = items
                }
            }
        }
    }

    override suspend fun publishSeedCatalog(): Result<Unit> = withContext(Dispatchers.IO) {
        val store = db ?: return@withContext Result.failure(
            IllegalStateException("Firebase is not configured. Add app/google-services.json first.")
        )
        try {
            val batch = store.batch()
            DemoCatalog.categories.forEach { category ->
                batch.set(store.collection(FirestorePaths.CATEGORIES).document(category.id), category.toMap())
            }
            DemoCatalog.banners.forEach { banner ->
                batch.set(store.collection(FirestorePaths.BANNERS).document(banner.id), banner.toMap())
            }
            DemoCatalog.products.forEach { product ->
                batch.set(store.collection(FirestorePaths.PRODUCTS).document(product.id), product.toMap())
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalStateException(e.message ?: "Could not publish the catalog"))
        }
    }
}

private fun Category.toMap(): Map<String, Any> = mapOf(
    "name" to name,
    "tagline" to tagline,
    "iconKey" to iconKey
)

private fun Banner.toMap(): Map<String, Any> = mapOf(
    "title" to title,
    "subtitle" to subtitle,
    "ctaLabel" to ctaLabel,
    "categoryId" to categoryId,
    "startColor" to startColor,
    "endColor" to endColor
)

private fun Product.toMap(): Map<String, Any> = mapOf(
    "name" to name,
    "brand" to brand,
    "categoryId" to categoryId,
    "description" to description,
    "specifications" to specifications,
    "price" to price,
    "mrp" to mrp,
    "unit" to unit,
    "imageUrl" to imageUrl,
    "stock" to stock,
    "rating" to rating,
    "featured" to featured
)

private fun DocumentSnapshot.toCategory(): Category? {
    val name = getString("name") ?: return null
    return Category(
        id = id,
        name = name,
        tagline = getString("tagline").orEmpty(),
        iconKey = getString("iconKey").orEmpty()
    )
}

private fun DocumentSnapshot.toBanner(): Banner? {
    val title = getString("title") ?: return null
    return Banner(
        id = id,
        title = title,
        subtitle = getString("subtitle").orEmpty(),
        ctaLabel = getString("ctaLabel") ?: "Shop now",
        categoryId = getString("categoryId").orEmpty(),
        startColor = getLong("startColor") ?: 0xFF16324B,
        endColor = getLong("endColor") ?: 0xFF0E2233
    )
}

private fun DocumentSnapshot.toProduct(): Product? {
    val name = getString("name") ?: return null
    val price = getDouble("price") ?: return null
    @Suppress("UNCHECKED_CAST")
    val specs = (get("specifications") as? Map<String, Any?>)
        ?.mapValues { entry -> entry.value?.toString().orEmpty() }
        .orEmpty()
    return Product(
        id = id,
        name = name,
        brand = getString("brand").orEmpty(),
        categoryId = getString("categoryId").orEmpty(),
        description = getString("description").orEmpty(),
        specifications = specs,
        price = price,
        mrp = getDouble("mrp") ?: price,
        unit = getString("unit").orEmpty(),
        imageUrl = getString("imageUrl").orEmpty(),
        stock = getLong("stock")?.toInt() ?: 0,
        rating = getDouble("rating") ?: 4.5,
        featured = getBoolean("featured") ?: false
    )
}
