package com.sarvodhaya.store.ui.screens.product

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
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
import androidx.compose.ui.graphics.Color
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
import com.sarvodhaya.store.core.ui.components.categoryIcon
import com.sarvodhaya.store.core.ui.theme.BrandAccent
import com.sarvodhaya.store.core.ui.theme.BrandAmber
import com.sarvodhaya.store.core.ui.theme.BrandForest
import com.sarvodhaya.store.core.ui.theme.BrandMuted
import com.sarvodhaya.store.core.ui.theme.CardShadow
import com.sarvodhaya.store.core.ui.theme.CardShape
import com.sarvodhaya.store.core.ui.theme.PanelShape
import com.sarvodhaya.store.core.ui.theme.PillShape
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
                                tint = if (wishlistIds.contains(product.id)) BrandAccent else Color.White
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ProductImage(
                    product = product,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    corner = 0
                )
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = product.categoryId.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandMuted
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBadge(product.rating)
                        Spacer(Modifier.width(10.dp))
                        StockPill(product.stock)
                    }
                    Spacer(Modifier.height(12.dp))
                    PriceRow(
                        price = product.price,
                        mrp = product.mrp,
                        discountPercent = product.discountPercent
                    )
                    Text(
                        text = "Inclusive of all taxes \u2022 ${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = CardShadow
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantity",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        QuantityStepper(
                            quantity = quantity,
                            onQuantityChange = { quantity = it },
                            maxQuantity = product.stock.coerceAtLeast(1)
                        )
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                wishlistViewModel.toggle(product.id)
                                scope.launch { snackbar.showSnackbar("Wishlist updated") }
                            },
                            shape = PillShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text(if (wishlistIds.contains(product.id)) "Wishlisted" else "Wishlist")
                        }
                        Button(
                            onClick = {
                                cartViewModel.add(product.id, quantity)
                                scope.launch { snackbar.showSnackbar("Added $quantity to cart") }
                            },
                            enabled = product.inStock,
                            shape = PillShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
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
                        shape = PillShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Buy now")
                        }
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
                                    .padding(vertical = 9.dp)
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
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
                    shape = PanelShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(BrandForest, androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Delivery & returns",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Free delivery above \u20B9999 \u2022 7 day easy returns on unused items in original packaging.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = CardShadow
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(14.dp)
                        .background(BrandAmber, RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
