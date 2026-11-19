package com.sarvodhaya.store.ui.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.EmptyState
import com.sarvodhaya.store.core.ui.components.ProductCard
import com.sarvodhaya.store.core.ui.components.SarvodhayaTopBar
import com.sarvodhaya.store.domain.model.Product
import com.sarvodhaya.store.ui.LocalSnackbarHostState
import com.sarvodhaya.store.ui.viewmodel.CartViewModel
import com.sarvodhaya.store.ui.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch

private enum class SortOption(val label: String) {
    POPULAR("Popular"),
    PRICE_LOW("Price: low to high"),
    PRICE_HIGH("Price: high to low"),
    DISCOUNT("Biggest discount")
}

@Composable
fun CategoryProductsScreen(
    categoryId: String,
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val products by catalogViewModel.products.collectAsStateWithLifecycle()
    var inStockOnly by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOption.POPULAR) }
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    val categoryName = catalogViewModel.categoryName(categoryId)
    val visibleProducts: List<Product> = remember(products, categoryId, inStockOnly, sortOption) {
        val filtered = products
            .filter { it.categoryId == categoryId }
            .filter { !inStockOnly || it.inStock }
        when (sortOption) {
            SortOption.POPULAR -> filtered.sortedByDescending { it.rating }
            SortOption.PRICE_LOW -> filtered.sortedBy { it.price }
            SortOption.PRICE_HIGH -> filtered.sortedByDescending { it.price }
            SortOption.DISCOUNT -> filtered.sortedByDescending { it.discountPercent }
        }
    }

    Scaffold(
        topBar = { SarvodhayaTopBar(title = categoryName, onBack = onBack) }
    ) { padding ->
        if (visibleProducts.isEmpty()) {
            Column(modifier = Modifier.padding(padding)) {
                EmptyState(
                    icon = Icons.Outlined.Inventory2,
                    title = "Nothing here yet",
                    message = "Products for this category will appear as soon as they are added."
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = inStockOnly,
                            onClick = { inStockOnly = !inStockOnly },
                            label = { Text("In stock") }
                        )
                    }
                    items(SortOption.values().toList()) { option ->
                        FilterChip(
                            selected = sortOption == option,
                            onClick = { sortOption = option },
                            label = { Text(option.label) }
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = {
                                cartViewModel.add(product.id)
                                scope.launch { snackbar.showSnackbar("${product.name} added to cart") }
                            }
                        )
                    }
                }
            }
        }
    }
}
