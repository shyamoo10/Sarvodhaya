package com.sarvodhaya.store.domain.model

data class Category(
    val id: String,
    val name: String,
    val tagline: String = "",
    val iconKey: String = ""
)

data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val ctaLabel: String,
    val categoryId: String,
    val startColor: Long,
    val endColor: Long
)

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val categoryId: String,
    val description: String,
    val specifications: Map<String, String>,
    val price: Double,
    val mrp: Double,
    val unit: String,
    val imageUrl: String = "",
    val stock: Int = 0,
    val rating: Double = 4.5,
    val featured: Boolean = false
) {
    val discountPercent: Int
        get() = if (mrp > price && mrp > 0.0) (((mrp - price) / mrp) * 100).toInt() else 0

    val inStock: Boolean
        get() = stock > 0
}

data class CartItem(val productId: String, val quantity: Int)

data class CartLine(val product: Product, val quantity: Int) {
    val lineTotal: Double get() = product.price * quantity
}

data class Address(
    val fullName: String = "",
    val phone: String = "",
    val line1: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = ""
) {
    val isComplete: Boolean
        get() = fullName.isNotBlank() && phone.length >= 10 && line1.isNotBlank() &&
            city.isNotBlank() && state.isNotBlank() && pincode.length >= 6

    val singleLine: String
        get() = listOf(line1, city, state, pincode)
            .filter { it.isNotBlank() }
            .joinToString(", ")
}

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val address: Address = Address()
)

enum class OrderStatus(val label: String, val description: String) {
    PLACED("Order placed", "We have received your order"),
    PACKED("Packed", "Your items are packed and ready"),
    SHIPPED("Shipped", "Handed over to the delivery partner"),
    OUT_FOR_DELIVERY("Out for delivery", "Arriving today"),
    DELIVERED("Delivered", "Delivered successfully");

    val step: Int get() = ordinal

    companion object {
        fun from(value: String?) = values().firstOrNull { it.name == value } ?: PLACED
    }
}

data class OrderItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val unit: String = "",
    val imageUrl: String = ""
)

data class Order(
    val id: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val address: Address,
    val paymentMethod: String,
    val status: OrderStatus,
    val placedAt: Long
) {
    val itemCount: Int get() = items.sumOf { it.quantity }
}
