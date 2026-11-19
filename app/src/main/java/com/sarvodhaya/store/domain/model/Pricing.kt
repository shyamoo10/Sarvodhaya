package com.sarvodhaya.store.domain.model

object Pricing {
    const val FREE_DELIVERY_MIN = 999.0
    const val DELIVERY_FEE = 49.0

    fun deliveryFeeFor(subtotal: Double): Double =
        if (subtotal >= FREE_DELIVERY_MIN || subtotal <= 0.0) 0.0 else DELIVERY_FEE
}
