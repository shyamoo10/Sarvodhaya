package com.sarvodhaya.store.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.sarvodhaya.store.di.AppContainer
import com.sarvodhaya.store.domain.model.Order
import kotlinx.coroutines.flow.StateFlow

class OrdersViewModel(private val container: AppContainer) : ViewModel() {

    val orders: StateFlow<List<Order>> = container.orderRepository.orders
    val isLoading: StateFlow<Boolean> = container.orderRepository.isLoading

    fun order(orderId: String): Order? = orders.value.firstOrNull { it.id == orderId }
}
