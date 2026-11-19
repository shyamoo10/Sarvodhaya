package com.sarvodhaya.store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarvodhaya.store.di.AppContainer
import com.sarvodhaya.store.domain.model.Address
import com.sarvodhaya.store.domain.model.CartLine
import com.sarvodhaya.store.domain.model.Pricing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(private val container: AppContainer) : ViewModel() {

    private val shop = container.shopRepository
    private val catalog = container.catalogRepository
    private val orders = container.orderRepository

    val lines: StateFlow<List<CartLine>> = combine(shop.cart, catalog.products) { cart, products ->
        cart.mapNotNull { item ->
            products.firstOrNull { it.id == item.productId }?.let { CartLine(it, item.quantity) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val subtotal: StateFlow<Double> = lines
        .map { list -> list.sumOf { it.lineTotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val deliveryFee: StateFlow<Double> = subtotal
        .map { Pricing.deliveryFeeFor(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val total: StateFlow<Double> = combine(subtotal, deliveryFee) { subtotalValue, fee ->
        subtotalValue + fee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val isSignedIn: StateFlow<Boolean> = container.authRepository.currentUser
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    data class UiState(
        val address: Address = Address(),
        val isPlacing: Boolean = false,
        val error: String? = null,
        val placedOrderId: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var prefillAttempted = false

    init {
        viewModelScope.launch {
            container.authRepository.currentUser.collect { profile ->
                if (profile == null || prefillAttempted) return@collect
                prefillAttempted = true
                _state.update { current ->
                    if (current.address.isComplete) current
                    else current.copy(
                        address = profile.address.copy(
                            fullName = profile.address.fullName.ifBlank { profile.name },
                            phone = profile.address.phone.ifBlank { profile.phone }
                        )
                    )
                }
            }
        }
    }

    fun updateAddress(transform: (Address) -> Address) {
        _state.update { it.copy(address = transform(it.address), error = null) }
    }

    fun placeOrder(paymentMethod: String) {
        if (_state.value.isPlacing) return
        viewModelScope.launch {
            _state.update { it.copy(isPlacing = true, error = null) }
            val result = orders.placeOrder(_state.value.address, paymentMethod)
            _state.update { current ->
                result.fold(
                    onSuccess = { order -> current.copy(isPlacing = false, placedOrderId = order.id) },
                    onFailure = { error -> current.copy(isPlacing = false, error = error.message) }
                )
            }
        }
    }
}
