package com.sarvodhaya.store.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.EmptyState
import com.sarvodhaya.store.core.ui.components.PriceRow
import com.sarvodhaya.store.core.ui.components.ProductImage
import com.sarvodhaya.store.core.ui.components.QuantityStepper
import com.sarvodhaya.store.core.ui.components.RatingBadge
import com.sarvodhaya.store.core.ui.components.SarvodhayaTopBar
import com.sarvodhaya.store.core.ui.components.StockPill
import com.sarvodhaya.store.ui.LocalSnackbarHostState
import com.sarvodhaya.store.ui.viewmodel.CartViewModel
import com.sarvodhaya.store.ui.viewmodel.CatalogViewModel
import com.sarvodhaya.store.ui.viewmodel.WishlistViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(
    productId: String,
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    onBack: () -> Unit,
    onOpenCart: () -> Unit
) {
    val products by catalogViewModel.products.collectAsStateWithLifecycle()
    val wishlistIds by wishlistViewModel.wishlistIds.collectAsStateWithLifecycle()
    val product = products.firstOrNull { it.id == productId }
    var quantity by remember(productId) { mutableIntStateOf(1) }
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SarvodhayaTopBar(
                title = product?.brand ?: "Product",
                onBack = onBack,
                actions = {
                    if (product != null) {
                        IconButton(onClick = { wishlistViewModel.toggle(product.id) }) {
                            Icon(
                                imageVector = if (wishlistIds.contains(product.id)) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = "Toggle wishlist",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onOpenCart) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Open cart")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            Column(modifier = Modifier.padding(padding)) {
                EmptyState(
                    icon = Icons.Outlined.Inventory2,
                    title = "Product not found",
                    message = "This product may have been removed from the store."
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProductImage(
                    product = product,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(horizontal = 16.dp),
                    corner = 22
                )
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = product.brand.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBadge(product.rating)
                        Spacer(Modifier.width(10.dp))
                        StockPill(product.stock)
                    }
                    Spacer(Modifier.height(10.dp))
                    PriceRow(
                        price = product.price,
                        mrp = product.mrp,
                        discountPercent = product.discountPercent
                    )
                    Text(
                        text = "Inclusive of all taxes - ${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    QuantityStepper(
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        maxQuantity = product.stock.coerceAtLeast(1)
                    )
                }
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                wishlistViewModel.toggle(product.id)
                                scope.launch { snackbar.showSnackbar("Wishlist updated") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (wishlistIds.contains(product.id)) "Wishlisted" else "Wishlist")
                        }
                        Button(
                            onClick = {
                                cartViewModel.add(product.id, quantity)
                                scope.launch { snackbar.showSnackbar("Added $quantity to cart") }
                            },
                            enabled = product.inStock,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (product.inStock) "Add to cart" else "Out of stock")
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            cartViewModel.add(product.id, quantity)
                            onOpenCart()
                        },
                        enabled = product.inStock,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Buy now")
                    }
                }
            }
            item {
                ProductSection(title = "Description") {
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                ProductSection(title = "Specifications") {
                    Column {
                        product.specifications.entries.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = entry.key,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = entry.value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (index < product.specifications.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            }
                        }
                    }
                }
            }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Delivery & returns",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Free delivery above Rs.999 - 7 day easy returns on unused items in original packaging.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSection(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}
