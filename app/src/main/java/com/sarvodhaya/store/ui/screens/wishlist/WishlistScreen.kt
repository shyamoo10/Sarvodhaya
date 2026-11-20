package com.sarvodhaya.store.ui.screens.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.EmptyState
import com.sarvodhaya.store.core.ui.components.ProductCard
import com.sarvodhaya.store.core.ui.components.SarvodhayaTopBar
import com.sarvodhaya.store.ui.LocalSnackbarHostState
import com.sarvodhaya.store.ui.viewmodel.CartViewModel
import com.sarvodhaya.store.ui.viewmodel.WishlistViewModel
import kotlinx.coroutines.launch

@Composable
fun WishlistScreen(
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (String) -> Unit,
    onContinueShopping: () -> Unit
) {
    val products by wishlistViewModel.products.collectAsStateWithLifecycle()
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { SarvodhayaTopBar(title = "Wishlist") },
        // The root Scaffold already reserves the bottom bar's height, so this
        // screen must not add the system bottom inset on top of it.
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        if (products.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyState(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "No saved products",
                    message = "Tap the heart on any product to keep it here for later.",
                    actionLabel = "Browse products",
                    onAction = onContinueShopping
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onAddToCart = {
                            cartViewModel.add(product.id)
                            scope.launch { snackbar.showSnackbar("${product.name} added to cart") }
                        }
                    )
                }
            }
        }
    }
}
