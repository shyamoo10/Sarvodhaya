package com.sarvodhaya.store.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.EmptyState
import com.sarvodhaya.store.core.ui.components.SarvodhayaTopBar
import com.sarvodhaya.store.core.ui.formatDate
import com.sarvodhaya.store.core.ui.formatPrice
import com.sarvodhaya.store.core.ui.theme.BrandGreen
import com.sarvodhaya.store.core.ui.theme.CardShadow
import com.sarvodhaya.store.core.ui.theme.CardShape
import com.sarvodhaya.store.core.ui.theme.PillShape
import com.sarvodhaya.store.domain.model.Order
import com.sarvodhaya.store.domain.model.OrderStatus
import com.sarvodhaya.store.ui.viewmodel.OrdersViewModel

@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel,
    isSignedIn: Boolean,
    onOrderClick: (String) -> Unit,
    onSignIn: () -> Unit
) {
    val orders by ordersViewModel.orders.collectAsStateWithLifecycle()
    val isLoading by ordersViewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { SarvodhayaTopBar(title = "My orders") },
        // The root Scaffold already reserves the bottom bar's height, so this
        // screen must not add the system bottom inset on top of it.
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        when {
            !isSignedIn -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyState(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = "Sign in to see orders",
                    message = "Your order history and live tracking appear here after you sign in.",
                    actionLabel = "Sign in",
                    onAction = onSignIn
                )
            }

            isLoading && orders.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            orders.isEmpty() -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyState(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = "No orders yet",
                    message = "Once you place an order it will show up here with live tracking."
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderCard(order = order, onClick = { onOrderClick(order.id) })
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = CardShadow,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.id.take(8).uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.placedAt.formatDate() + " - " + order.itemCount + " items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPill(order.status)
                Spacer(Modifier.weight(1f))
                Text(
                    text = order.total.formatPrice(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderDetailScreen(
    orderId: String,
    ordersViewModel: OrdersViewModel,
    onBack: () -> Unit
) {
    val orders by ordersViewModel.orders.collectAsStateWithLifecycle()
    val order = orders.firstOrNull { it.id == orderId }

    Scaffold(
        topBar = { SarvodhayaTopBar(title = "Order details", onBack = onBack) },
        // The root Scaffold already reserves the bottom bar's height, so this
        // screen must not add the system bottom inset on top of it.
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        if (order == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyState(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = "Order not found",
                    message = "This order is no longer available on this device."
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = CardShadow
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order #${order.id.take(8).uppercase()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Placed on ${order.placedAt.formatDate()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        StatusTimeline(order.status)
                    }
                }
            }
            item {
                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = CardShadow
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        order.items.forEachIndexed { index, item ->
                            Row(modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(
                                    text = "${item.quantity} x ${item.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = (item.price * item.quantity).formatPrice(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (index < order.items.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            }
                        }
                    }
                }
            }
            item {
                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = CardShadow
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Delivery address",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = order.address.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = order.address.singleLine,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Phone: ${order.address.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Payment: ${order.paymentMethod}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Total: ${order.total.formatPrice()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: OrderStatus) {
    Surface(
        shape = PillShape,
        color = BrandGreen.copy(alpha = 0.12f)
    ) {
        Text(
            text = status.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = BrandGreen,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StatusTimeline(current: OrderStatus) {
    Column {
        OrderStatus.values().forEachIndexed { index, status ->
            val reached = index <= current.step
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (reached) BrandGreen else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (reached) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    if (index < OrderStatus.values().lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(30.dp)
                                .background(
                                    if (index < current.step) BrandGreen
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = status.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (reached) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (reached) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = status.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
