package com.sarvodhaya.store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarvodhaya.store.di.AppContainer
import com.sarvodhaya.store.domain.model.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class WishlistViewModel(private val container: AppContainer) : ViewModel() {

    private val shop = container.shopRepository
    private val catalog = container.catalogRepository

    val wishlistIds: StateFlow<List<String>> = shop.wishlist

    val products: StateFlow<List<Product>> = combine(shop.wishlist, catalog.products) { ids, products ->
        products.filter { ids.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggle(productId: String) = shop.toggleWishlist(productId)

    fun addToCart(productId: String) = shop.addToCart(productId)
}
