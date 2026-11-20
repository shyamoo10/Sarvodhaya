package com.sarvodhaya.store.core.ui

import com.sarvodhaya.store.core.ui.theme.BrandAccent
import com.sarvodhaya.store.domain.model.Category
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.formatPrice(): String {
    val rounded = if (this % 1.0 == 0.0) toLong().toString() else String.format(Locale.US, "%.2f", this)
    return "\u20B9$rounded"
}

fun Long.formatDateTime(): String =
    SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date(this))

fun Long.formatDate(): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))

/** Banner colours are stored as ARGB longs. */
fun Long.colorFromArgb() = androidx.compose.ui.graphics.Color(this)

val Category.accentColor: androidx.compose.ui.graphics.Color
    get() = BrandAccent
