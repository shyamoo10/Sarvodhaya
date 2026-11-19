package com.sarvodhaya.store.ui.navigation

object Destinations {
    const val HOME = "home"
    const val SEARCH = "search"
    const val WISHLIST = "wishlist"
    const val CART = "cart"
    const val PROFILE = "profile"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"
    const val AUTH = "auth"

    const val PRODUCT_PATTERN = "product/{productId}"
    const val CATEGORY_PATTERN = "category/{categoryId}"
    const val ORDER_PATTERN = "order/{orderId}"

    fun product(productId: String) = "product/$productId"
    fun category(categoryId: String) = "category/$categoryId"
    fun order(orderId: String) = "order/$orderId"
}
