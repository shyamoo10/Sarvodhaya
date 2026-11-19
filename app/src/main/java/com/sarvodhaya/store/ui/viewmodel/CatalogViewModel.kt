package com.sarvodhaya.store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarvodhaya.store.di.AppContainer
import com.sarvodhaya.store.domain.model.Banner
import com.sarvodhaya.store.domain.model.Category
import com.sarvodhaya.store.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CatalogViewModel(private val container: AppContainer) : ViewModel() {

    private val catalog = container.catalogRepository

    val categories: StateFlow<List<Category>> = catalog.categories
    val banners: StateFlow<List<Banner>> = catalog.banners
    val products: StateFlow<List<Product>> = catalog.products

    val featured: StateFlow<List<Product>> = catalog.products
        .map { list -> list.filter { it.featured } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            catalog.products.value.filter { it.featured }
        )

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val searchResults: StateFlow<List<Product>> = combine(catalog.products, _query) { list, term ->
        val cleaned = term.trim()
        if (cleaned.isBlank()) list
        else list.filter {
            it.name.contains(cleaned, ignoreCase = true) ||
                it.brand.contains(cleaned, ignoreCase = true) ||
                it.description.contains(cleaned, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), catalog.products.value)

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun productsInCategory(categoryId: String): List<Product> =
        catalog.products.value.filter { it.categoryId == categoryId }

    fun categoryName(categoryId: String): String =
        catalog.categories.value.firstOrNull { it.id == categoryId }?.name ?: "Products"

    fun product(productId: String): Product? =
        catalog.products.value.firstOrNull { it.id == productId }
}
