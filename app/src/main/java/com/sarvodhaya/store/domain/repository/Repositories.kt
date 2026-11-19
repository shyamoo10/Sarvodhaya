package com.sarvodhaya.store.domain.repository

import com.sarvodhaya.store.domain.model.Address
import com.sarvodhaya.store.domain.model.Banner
import com.sarvodhaya.store.domain.model.CartItem
import com.sarvodhaya.store.domain.model.Category
import com.sarvodhaya.store.domain.model.Order
import com.sarvodhaya.store.domain.model.Product
import com.sarvodhaya.store.domain.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface CatalogRepository {
    val categories: StateFlow<List<Category>>
    val banners: StateFlow<List<Banner>>
    val products: StateFlow<List<Product>>

    /** Uploads the bundled demo catalog into Firestore. */
    suspend fun publishSeedCatalog(): Result<Unit>
}

interface AuthRepository {
    val currentUser: StateFlow<UserProfile?>
    val isConfigured: Boolean

    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun signOut()
    suspend fun saveProfile(name: String, phone: String, address: Address): Result<Unit>
}

interface ShopRepository {
    val cart: StateFlow<List<CartItem>>
    val wishlist: StateFlow<List<String>>

    fun addToCart(productId: String, quantity: Int = 1)
    fun setQuantity(productId: String, quantity: Int)
    fun removeFromCart(productId: String)
    fun clearCart()
    fun toggleWishlist(productId: String)
}

interface OrderRepository {
    val orders: StateFlow<List<Order>>
    val isLoading: StateFlow<Boolean>

    suspend fun placeOrder(address: Address, paymentMethod: String): Result<Order>
}
