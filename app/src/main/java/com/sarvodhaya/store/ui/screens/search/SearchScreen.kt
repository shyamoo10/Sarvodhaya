package com.sarvodhaya.store.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.EmptyState
import com.sarvodhaya.store.core.ui.components.ProductCard
import com.sarvodhaya.store.core.ui.components.SectionHeader
import com.sarvodhaya.store.core.ui.theme.PillShape
import com.sarvodhaya.store.ui.LocalSnackbarHostState
import com.sarvodhaya.store.ui.viewmodel.CartViewModel
import com.sarvodhaya.store.ui.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val query by catalogViewModel.query.collectAsStateWithLifecycle()
    val results by catalogViewModel.searchResults.collectAsStateWithLifecycle()
    val categories by catalogViewModel.categories.collectAsStateWithLifecycle()
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = catalogViewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Search products, brands") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { catalogViewModel.onQueryChange("") }) {
                        Icon(Icons.Outlined.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = PillShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = ImeAction.Search
            )
        )

        if (query.isBlank()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    AssistChip(
                        onClick = { onCategoryClick(category.id) },
                        label = {
                            Text(
                                category.name,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        shape = PillShape,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (results.isEmpty()) {
            val catalogEmpty = query.isBlank()
            EmptyState(
                icon = if (catalogEmpty) Icons.Outlined.Inventory2 else Icons.Outlined.SearchOff,
                title = if (catalogEmpty) "No products yet" else "No matches found",
                message = if (catalogEmpty) {
                    "The catalog is empty right now. New stock is added regularly."
                } else {
                    "Try a different product name or browse the categories above."
                }
            )
        } else {
            SectionHeader(
                title = if (query.isBlank()) "All products" else "${results.size} results",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(results, key = { it.id }) { product ->
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
