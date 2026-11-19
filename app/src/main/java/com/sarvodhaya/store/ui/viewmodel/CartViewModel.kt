package com.sarvodhaya.store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarvodhaya.store.di.AppContainer
import com.sarvodhaya.store.domain.model.CartLine
import com.sarvodhaya.store.domain.model.Pricing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CartViewModel(private val container: AppContainer) : ViewModel() {

    private val shop = container.shopRepository
    private val catalog = container.catalogRepository

    val lines: StateFlow<List<CartLine>> = combine(shop.cart, catalog.products) { cart, products ->
        cart.mapNotNull { item ->
            products.firstOrNull { it.id == item.productId }?.let { CartLine(it, item.quantity) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val itemCount: StateFlow<Int> = lines
        .map { list -> list.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val subtotal: StateFlow<Double> = lines
        .map { list -> list.sumOf { it.lineTotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val deliveryFee: StateFlow<Double> = subtotal
        .map { Pricing.deliveryFeeFor(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val total: StateFlow<Double> = combine(subtotal, deliveryFee) { subtotalValue, fee ->
        subtotalValue + fee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    fun add(productId: String, quantity: Int = 1) = shop.addToCart(productId, quantity)

    fun setQuantity(productId: String, quantity: Int) = shop.setQuantity(productId, quantity)

    fun remove(productId: String) = shop.removeFromCart(productId)

    fun clear() = shop.clearCart()
}
