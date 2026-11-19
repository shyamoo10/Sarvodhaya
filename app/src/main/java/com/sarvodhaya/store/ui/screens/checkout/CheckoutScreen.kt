package com.sarvodhaya.store.ui.screens.checkout

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.RadioButtonDefaults
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

    Scaffold(topBar = { SarvodhayaTopBar(title = "Checkout", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CheckoutCard {
                    Text(
                        text = "Delivery address",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    AddressForm(
                        address = state.address,
                        onChange = { transform -> checkoutViewModel.updateAddress(transform) }
                    )
                }
            }
            item {
                CheckoutCard {
                    Text(
                        text = "Payment method",
                        style = MaterialTheme.typography.titleSmall,
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
            item {
                Text(
                    text = "Order summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            items(lines, key = { it.product.id }) { line ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${line.quantity} \u00D7 ${line.product.name}",
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
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = state.error.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { checkoutViewModel.placeOrder(paymentMethod) },
                    enabled = !state.isPlacing && state.address.isComplete && lines.isNotEmpty(),
                    shape = RoundedCornerShape(10.dp),
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
                        Text("Place order \u2022 ${total.formatPrice()}")
                    }
                }
                if (!state.address.isComplete) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Complete the address form to place your order.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
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
            isError = address.fullName.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address.phone,
            onValueChange = { value -> onChange { it.copy(phone = value.filter { ch -> ch.isDigit() }) } },
            label = { Text("Phone number") },
            singleLine = true,
            isError = address.phone.isNotBlank() && address.phone.length < 10,
            supportingText = if (address.phone.isNotBlank() && address.phone.length < 10) {
                { Text("Enter a 10 digit mobile number") }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address.line1,
            onValueChange = { value -> onChange { it.copy(line1 = value) } },
            label = { Text("House, street, area") },
            isError = address.line1.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = address.city,
                onValueChange = { value -> onChange { it.copy(city = value) } },
                label = { Text("City") },
                singleLine = true,
                isError = address.city.isBlank(),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = address.state,
                onValueChange = { value -> onChange { it.copy(state = value) } },
                label = { Text("State") },
                singleLine = true,
                isError = address.state.isBlank(),
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = address.pincode,
            onValueChange = { value -> onChange { it.copy(pincode = value.filter { ch -> ch.isDigit() }) } },
            label = { Text("PIN code") },
            singleLine = true,
            isError = address.pincode.isNotBlank() && address.pincode.length < 6,
            supportingText = if (address.pincode.isNotBlank() && address.pincode.length < 6) {
                { Text("PIN code must be 6 digits") }
            } else null,
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
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
