package com.sarvodhaya.store.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.SarvodhayaTopBar
import com.sarvodhaya.store.core.ui.formatPrice
import com.sarvodhaya.store.domain.model.Address
import com.sarvodhaya.store.ui.screens.cart.OrderSummary
import com.sarvodhaya.store.ui.viewmodel.CheckoutViewModel

@Composable
fun CheckoutScreen(
    checkoutViewModel: CheckoutViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val state by checkoutViewModel.state.collectAsStateWithLifecycle()
    val lines by checkoutViewModel.lines.collectAsStateWithLifecycle()
    val subtotal by checkoutViewModel.subtotal.collectAsStateWithLifecycle()
    val deliveryFee by checkoutViewModel.deliveryFee.collectAsStateWithLifecycle()
    val total by checkoutViewModel.total.collectAsStateWithLifecycle()

    var paymentMethod by remember { mutableStateOf("Cash on delivery") }

    LaunchedEffect(state.placedOrderId) {
        state.placedOrderId?.let(onOrderPlaced)
    }

    Scaffold(
        topBar = { SarvodhayaTopBar(title = "Checkout", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Delivery address",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        AddressForm(
                            address = state.address,
                            onChange = { transform -> checkoutViewModel.updateAddress(transform) }
                        )
                    }
                }
            }
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        PaymentOption(
                            label = "Cash on delivery",
                            hint = "Pay when your order arrives",
                            selected = paymentMethod == "Cash on delivery",
                            onClick = { paymentMethod = "Cash on delivery" }
                        )
                        PaymentOption(
                            label = "UPI on delivery",
                            hint = "Scan and pay at your doorstep",
                            selected = paymentMethod == "UPI on delivery",
                            onClick = { paymentMethod = "UPI on delivery" }
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Order summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(lines, key = { it.product.id }) { line ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${line.quantity} x ${line.product.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = line.lineTotal.formatPrice(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            item { OrderSummary(subtotal = subtotal, deliveryFee = deliveryFee, total = total) }
            if (state.error != null) {
                item {
                    Text(
                        text = state.error.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            item {
                Button(
                    onClick = { checkoutViewModel.placeOrder(paymentMethod) },
                    enabled = !state.isPlacing && state.address.isComplete && lines.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (state.isPlacing) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Place order - ${total.formatPrice()}")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressForm(address: Address, onChange: ((Address) -> Address) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = address.fullName,
            onValueChange = { value -> onChange { it.copy(fullName = value) } },
            label = { Text("Full name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address.phone,
            onValueChange = { value -> onChange { it.copy(phone = value.filter { ch -> ch.isDigit() }) } },
            label = { Text("Phone number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address.line1,
            onValueChange = { value -> onChange { it.copy(line1 = value) } },
            label = { Text("House, street, area") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = address.city,
                onValueChange = { value -> onChange { it.copy(city = value) } },
                label = { Text("City") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = address.state,
                onValueChange = { value -> onChange { it.copy(state = value) } },
                label = { Text("State") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = address.pincode,
            onValueChange = { value -> onChange { it.copy(pincode = value.filter { ch -> ch.isDigit() }) } },
            label = { Text("PIN code") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PaymentOption(label: String, hint: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
