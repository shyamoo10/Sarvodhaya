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
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.EmptyState
import com.sarvodhaya.store.core.ui.components.ProductCard
import com.sarvodhaya.store.core.ui.components.SarvodhayaTopBar
import com.sarvodhaya.store.core.ui.theme.BrandNavy
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
    val categories by catalogViewModel.categories.collectAsStateWithLifecycle()
    var inStockOnly by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOption.POPULAR) }
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    // Derived from the collected list so the title updates when remote categories arrive.
    val categoryName = categories.firstOrNull { it.id == categoryId }?.name ?: "Products"
    val inCategory = remember(products, categoryId) {
        products.filter { it.categoryId == categoryId }
    }
    val visibleProducts: List<Product> = remember(inCategory, inStockOnly, sortOption) {
        val filtered = inCategory.filter { !inStockOnly || it.inStock }
        when (sortOption) {
            SortOption.POPULAR -> filtered.sortedByDescending { it.rating }
            SortOption.PRICE_LOW -> filtered.sortedBy { it.price }
            SortOption.PRICE_HIGH -> filtered.sortedByDescending { it.price }
            SortOption.DISCOUNT -> filtered.sortedByDescending { it.discountPercent }
        }
    }

    Scaffold(topBar = { SarvodhayaTopBar(title = categoryName, onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (inCategory.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = inStockOnly,
                            onClick = { inStockOnly = !inStockOnly },
                            label = { Text("In stock") },
                            colors = industrialChipColors()
                        )
                    }
                    items(SortOption.values().toList()) { option ->
                        FilterChip(
                            selected = sortOption == option,
                            onClick = { sortOption = option },
                            label = { Text(option.label) },
                            colors = industrialChipColors()
                        )
                    }
                }
            }

            if (visibleProducts.isEmpty()) {
                EmptyState(
                    icon = if (inCategory.isEmpty()) {
                        Icons.Outlined.Inventory2
                    } else {
                        Icons.Outlined.FilterAltOff
                    },
                    title = if (inCategory.isEmpty()) "Nothing here yet" else "No products match",
                    message = if (inCategory.isEmpty()) {
                        "Products for this category will appear as soon as they are added."
                    } else {
                        "This category has ${inCategory.size} products, but none match the current filter."
                    },
                    actionLabel = if (inCategory.isNotEmpty()) "Clear filters" else null,
                    onAction = {
                        inStockOnly = false
                        sortOption = SortOption.POPULAR
                    }
                )
            } else {
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

@Composable
private fun industrialChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = BrandNavy,
    selectedLabelColor = Color.White,
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
)
