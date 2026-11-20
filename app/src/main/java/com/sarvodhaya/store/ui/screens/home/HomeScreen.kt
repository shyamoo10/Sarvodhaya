package com.sarvodhaya.store.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarvodhaya.store.core.ui.components.CompactProductRow
import com.sarvodhaya.store.core.ui.components.ProductCard
import com.sarvodhaya.store.core.ui.components.SectionHeader
import com.sarvodhaya.store.core.ui.components.categoryIcon
import com.sarvodhaya.store.core.ui.theme.BrandAccent
import com.sarvodhaya.store.core.ui.theme.BrandAmber
import com.sarvodhaya.store.core.ui.theme.BrandForest
import com.sarvodhaya.store.core.ui.theme.BrandForestDeep
import com.sarvodhaya.store.core.ui.theme.BrandMuted
import com.sarvodhaya.store.core.ui.theme.PanelShape
import com.sarvodhaya.store.core.ui.theme.PillShape
import com.sarvodhaya.store.domain.model.Banner
import com.sarvodhaya.store.domain.model.Category
import com.sarvodhaya.store.domain.model.Product
import com.sarvodhaya.store.ui.LocalSnackbarHostState
import com.sarvodhaya.store.ui.viewmodel.CartViewModel
import com.sarvodhaya.store.ui.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onBrowseAll: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenSearch: () -> Unit
) {
    val categories by catalogViewModel.categories.collectAsStateWithLifecycle()
    val banners by catalogViewModel.banners.collectAsStateWithLifecycle()
    val featured by catalogViewModel.featured.collectAsStateWithLifecycle()
    val products by catalogViewModel.products.collectAsStateWithLifecycle()
    val cartCount by cartViewModel.itemCount.collectAsStateWithLifecycle()

    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val addToCart: (Product) -> Unit = { product ->
        cartViewModel.add(product.id)
        scope.launch { snackbar.showSnackbar("${product.name} added to cart") }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            HomeHero(cartCount = cartCount, onOpenCart = onOpenCart, onOpenSearch = onOpenSearch)
        }
        item {
            BannerCarousel(banners = banners, onBannerClick = { onCategoryClick(it.categoryId) })
        }
        item {
            CategoryGrid(
                categories = categories,
                onCategoryClick = onCategoryClick,
                onSeeAll = onBrowseAll
            )
        }
        item {
            SectionHeader(
                title = "Best sellers",
                actionLabel = "Browse all",
                onAction = onBrowseAll,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featured, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onAddToCart = { addToCart(product) },
                        modifier = Modifier.width(176.dp)
                    )
                }
            }
        }
        item { ServiceStrip() }
        item {
            SectionHeader(
                title = "Popular in store",
                actionLabel = "See all",
                onAction = onBrowseAll,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        items(products.take(6), key = { it.id }) { product ->
            CompactProductRow(
                product = product,
                onClick = { onProductClick(product.id) },
                onAddToCart = { addToCart(product) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun HomeHero(cartCount: Int, onOpenCart: () -> Unit, onOpenSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                Brush.verticalGradient(listOf(BrandForestDeep, BrandForest))
            )
            .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(BrandAmber, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandForestDeep
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SARVODHAYA",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Hardware & building materials",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.68f)
                )
            }
            IconButton(onClick = onOpenCart) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = BrandAccent) { Text(cartCount.toString()) }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Open cart",
                        tint = Color.White
                    )
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PillShape,
            color = MaterialTheme.colorScheme.surface,
            onClick = onOpenSearch
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = BrandMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Search paints, locks, cement, tools",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandMuted
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeroChip("Genuine brands")
            HeroChip("Bulk supply")
            HeroChip("Same-day dispatch")
        }
    }
}

@Composable
private fun HeroChip(label: String) {
    Surface(
        shape = PillShape,
        color = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.90f),
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun BannerCarousel(banners: List<Banner>, onBannerClick: (Banner) -> Unit) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })
    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(PanelShape)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(banner.startColor), Color(banner.endColor))
                        )
                    )
                    .clickable { onBannerClick(banner) }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(20.dp)
                ) {
                    Surface(
                        shape = PillShape,
                        color = BrandAmber
                    ) {
                        Text(
                            text = "OFFER",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandForestDeep,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = banner.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = banner.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = PillShape,
                        color = Color.White
                    ) {
                        Text(
                            text = banner.ctaLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = BrandForestDeep,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            banners.indices.forEach { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = if (selected) 18.dp else 6.dp, height = 4.dp)
                        .background(
                            color = if (selected) BrandAmber else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit,
    onSeeAll: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "Shop by category", actionLabel = "See all", onAction = onSeeAll)
        Spacer(Modifier.height(12.dp))
        categories.chunked(4).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowCategories.forEach { category ->
                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .clickable { onCategoryClick(category.id) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = categoryIcon(category.id),
                                    contentDescription = null,
                                    tint = BrandForest,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ServiceStrip() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = PanelShape,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BrandForest, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Free delivery above \u20B9999",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Same-day dispatch on orders placed before 4 PM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
