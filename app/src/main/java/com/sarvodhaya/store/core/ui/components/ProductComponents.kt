package com.sarvodhaya.store.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Plumbing
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sarvodhaya.store.core.ui.formatPrice
import com.sarvodhaya.store.core.ui.theme.BrandAccent
import com.sarvodhaya.store.core.ui.theme.BrandAmber
import com.sarvodhaya.store.core.ui.theme.BrandAmberDeep
import com.sarvodhaya.store.core.ui.theme.BrandAmberSoft
import com.sarvodhaya.store.core.ui.theme.BrandMuted
import com.sarvodhaya.store.core.ui.theme.CardShadow
import com.sarvodhaya.store.core.ui.theme.CardShape
import com.sarvodhaya.store.core.ui.theme.PillShape
import com.sarvodhaya.store.domain.model.Product

/** Product visual with a branded placeholder when no photo is available. */
@Composable
fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier,
    corner: Int = 14
) {
    val shape = RoundedCornerShape(corner.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (product.imageUrl.isNotBlank()) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = categoryIcon(product.categoryId),
                contentDescription = null,
                tint = BrandMuted.copy(alpha = 0.45f),
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

fun categoryIcon(categoryId: String): ImageVector = when (categoryId) {
    "paints" -> Icons.Outlined.FormatPaint
    "locks" -> Icons.Outlined.Lock
    "cement" -> Icons.Outlined.Inventory2
    "steel" -> Icons.Outlined.Construction
    "plumbing" -> Icons.Outlined.Plumbing
    "electrical" -> Icons.Outlined.ElectricalServices
    "tools" -> Icons.Outlined.Handyman
    "adhesives" -> Icons.Outlined.Science
    else -> Icons.Outlined.FormatPaint
}

@Composable
fun PriceRow(
    price: Double,
    mrp: Double,
    discountPercent: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = price.formatPrice(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (mrp > price) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = mrp.formatPrice(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough
            )
        }
        if (discountPercent > 0) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = "$discountPercent% OFF",
                style = MaterialTheme.typography.labelSmall,
                color = BrandAccent
            )
        }
    }
}

@Composable
fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = PillShape,
        color = BrandAmberSoft
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = BrandAmber,
                modifier = Modifier.size(11.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = String.format(java.util.Locale.US, "%.1f", rating),
                style = MaterialTheme.typography.labelSmall,
                color = BrandAmberDeep
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = CardShadow
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box {
                ProductImage(
                    product = product,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp),
                    corner = 14
                )
                if (product.discountPercent > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = PillShape,
                        color = BrandAccent
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                if (!product.inStock) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = "SOLD OUT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = product.brand.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )
            Spacer(Modifier.height(6.dp))
            PriceRow(
                price = product.price,
                mrp = product.mrp,
                discountPercent = 0
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingBadge(product.rating)
                Spacer(Modifier.weight(1f))
                Surface(
                    onClick = onAddToCart,
                    enabled = product.inStock,
                    shape = CircleShape,
                    color = if (product.inStock) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add ${product.name} to cart",
                        tint = if (product.inStock) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier
                            .padding(9.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Surface(
        modifier = modifier,
        shape = PillShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (quantity > minQuantity) onQuantityChange(quantity - 1) },
                enabled = quantity > minQuantity,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    Icons.Outlined.Remove,
                    contentDescription = "Decrease quantity",
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(
                onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) },
                enabled = quantity < maxQuantity,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Increase quantity",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
