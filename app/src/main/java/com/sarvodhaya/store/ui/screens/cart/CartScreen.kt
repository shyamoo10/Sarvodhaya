package com.sarvodhaya.store.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.EmptyState
import com.sarvodhaya.store.core.ui.components.ProductImage
import com.sarvodhaya.store.core.ui.components.QuantityStepper
import com.sarvodhaya.store.core.ui.components.SarvodhayaTopBar
import com.sarvodhaya.store.core.ui.formatPrice
import com.sarvodhaya.store.core.ui.theme.BrandGreen
import com.sarvodhaya.store.core.ui.theme.BrandMuted
import com.sarvodhaya.store.core.ui.theme.CardShadow
import com.sarvodhaya.store.core.ui.theme.CardShape
import com.sarvodhaya.store.core.ui.theme.PillShape
import com.sarvodhaya.store.domain.model.CartLine
import com.sarvodhaya.store.domain.model.Pricing
import com.sarvodhaya.store.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onProductClick: (String) -> Unit,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    val lines by cartViewModel.lines.collectAsStateWithLifecycle()
    val subtotal by cartViewModel.subtotal.collectAsStateWithLifecycle()
    val deliveryFee by cartViewModel.deliveryFee.collectAsStateWithLifecycle()
    val total by cartViewModel.total.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { SarvodhayaTopBar(title = "Your cart") },
        bottomBar = {
            if (lines.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 16.dp
                ) {
                    Button(
                        onClick = onCheckout,
                        shape = PillShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(54.dp)
                    ) {
                        Text(
                            text = "Proceed to checkout \u2022 ${total.formatPrice()}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        },
        // The root Scaffold already reserves the bottom bar's height, so this
        // screen must not add the system bottom inset on top of it.
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        if (lines.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyState(
                    icon = Icons.Outlined.ShoppingCart,
                    title = "Your cart is empty",
                    message = "Add paints, locks, cement or tools to get started.",
                    actionLabel = "Start shopping",
                    onAction = onContinueShopping
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(lines, key = { it.product.id }) { line ->
                    CartLineCard(
                        line = line,
                        onProductClick = { onProductClick(line.product.id) },
                        onQuantityChange = { quantity ->
                            cartViewModel.setQuantity(line.product.id, quantity)
                        },
                        onRemove = { cartViewModel.remove(line.product.id) }
                    )
                }
                item {
                    OrderSummary(
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        total = total
                    )
                }
            }
        }
    }
}

@Composable
private fun CartLineCard(
    line: CartLine,
    onProductClick: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = CardShadow,
        onClick = onProductClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProductImage(
                    product = line.product,
                    modifier = Modifier.size(76.dp),
                    corner = 8
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = line.product.brand.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandMuted
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = line.product.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = line.product.unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = line.lineTotal.formatPrice(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Remove from cart",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${line.product.price.formatPrice()} each",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                QuantityStepper(
                    quantity = line.quantity,
                    onQuantityChange = onQuantityChange,
                    maxQuantity = line.product.stock.coerceAtLeast(line.quantity)
                )
            }
        }
    }
}

@Composable
fun OrderSummary(subtotal: Double, deliveryFee: Double, total: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = CardShadow
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Price details",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            SummaryRow("Subtotal", subtotal.formatPrice())
            SummaryRow(
                label = "Delivery",
                value = if (deliveryFee == 0.0) "FREE" else deliveryFee.formatPrice(),
                valueColor = if (deliveryFee == 0.0) BrandGreen else MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            SummaryRow("Total", total.formatPrice(), bold = true)
            if (subtotal in 0.001..(Pricing.FREE_DELIVERY_MIN - 0.001)) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Add ${(Pricing.FREE_DELIVERY_MIN - subtotal).formatPrice()} more for free delivery",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandGreen
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    bold: Boolean = false,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}
