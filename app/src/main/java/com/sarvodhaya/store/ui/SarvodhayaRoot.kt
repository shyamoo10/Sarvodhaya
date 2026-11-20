package com.sarvodhaya.store.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sarvodhaya.store.core.ui.theme.BrandAccent
import com.sarvodhaya.store.di.AppContainer
import com.sarvodhaya.store.di.containerViewModelFactory
import com.sarvodhaya.store.ui.navigation.Destinations
import com.sarvodhaya.store.ui.screens.auth.AuthScreen
import com.sarvodhaya.store.ui.screens.cart.CartScreen
import com.sarvodhaya.store.ui.screens.catalog.CategoryProductsScreen
import com.sarvodhaya.store.ui.screens.checkout.CheckoutScreen
import com.sarvodhaya.store.ui.screens.home.HomeScreen
import com.sarvodhaya.store.ui.screens.orders.OrderDetailScreen
import com.sarvodhaya.store.ui.screens.orders.OrdersScreen
import com.sarvodhaya.store.ui.screens.product.ProductDetailScreen
import com.sarvodhaya.store.ui.screens.profile.ProfileScreen
import com.sarvodhaya.store.ui.screens.search.SearchScreen
import com.sarvodhaya.store.ui.screens.wishlist.WishlistScreen
import com.sarvodhaya.store.ui.viewmodel.AuthViewModel
import com.sarvodhaya.store.ui.viewmodel.CartViewModel
import com.sarvodhaya.store.ui.viewmodel.CatalogViewModel
import com.sarvodhaya.store.ui.viewmodel.CheckoutViewModel
import com.sarvodhaya.store.ui.viewmodel.OrdersViewModel
import com.sarvodhaya.store.ui.viewmodel.WishlistViewModel

@Composable
fun SarvodhayaRoot(container: AppContainer) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedTab = selectedTabFor(currentRoute)

    val rootCartViewModel: CartViewModel = viewModel(
        factory = containerViewModelFactory { CartViewModel(container) }
    )
    val cartCount by rootCartViewModel.itemCount.collectAsStateWithLifecycle()

    CompositionLocalProvider(
        LocalAppContainer provides container,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            /* The bar stays mounted on every destination so moving into a
             * product, category, order or checkout screen never strands the
             * shopper. Scaffold reserves its height as padding for the
             * NavHost, so no screen content runs underneath it. */
            bottomBar = {
                SarvodhayaBottomBar(
                    selectedTab = selectedTab,
                    cartCount = cartCount,
                    onSelect = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Destinations.HOME,
                modifier = Modifier.padding(padding)
            ) {
                composable(Destinations.HOME) {
                    val catalogViewModel: CatalogViewModel = viewModel(
                        factory = containerViewModelFactory { CatalogViewModel(container) }
                    )
                    val cartViewModel: CartViewModel = viewModel(
                        factory = containerViewModelFactory { CartViewModel(container) }
                    )
                    HomeScreen(
                        catalogViewModel = catalogViewModel,
                        cartViewModel = cartViewModel,
                        onProductClick = { navController.navigate(Destinations.product(it)) },
                        onCategoryClick = { navController.navigate(Destinations.category(it)) },
                        onBrowseAll = {
                            navController.navigate(Destinations.SEARCH) { launchSingleTop = true }
                        },
                        onOpenCart = { navController.navigate(Destinations.CART) { launchSingleTop = true } },
                        onOpenSearch = { navController.navigate(Destinations.SEARCH) { launchSingleTop = true } }
                    )
                }

                composable(Destinations.SEARCH) {
                    val catalogViewModel: CatalogViewModel = viewModel(
                        factory = containerViewModelFactory { CatalogViewModel(container) }
                    )
                    val cartViewModel: CartViewModel = viewModel(
                        factory = containerViewModelFactory { CartViewModel(container) }
                    )
                    SearchScreen(
                        catalogViewModel = catalogViewModel,
                        cartViewModel = cartViewModel,
                        onProductClick = { navController.navigate(Destinations.product(it)) },
                        onCategoryClick = { navController.navigate(Destinations.category(it)) }
                    )
                }

                composable(Destinations.WISHLIST) {
                    val wishlistViewModel: WishlistViewModel = viewModel(
                        factory = containerViewModelFactory { WishlistViewModel(container) }
                    )
                    val cartViewModel: CartViewModel = viewModel(
                        factory = containerViewModelFactory { CartViewModel(container) }
                    )
                    WishlistScreen(
                        wishlistViewModel = wishlistViewModel,
                        cartViewModel = cartViewModel,
                        onProductClick = { navController.navigate(Destinations.product(it)) },
                        onContinueShopping = { navController.navigate(Destinations.HOME) }
                    )
                }

                composable(Destinations.CART) {
                    val cartViewModel: CartViewModel = viewModel(
                        factory = containerViewModelFactory { CartViewModel(container) }
                    )
                    CartScreen(
                        cartViewModel = cartViewModel,
                        onProductClick = { navController.navigate(Destinations.product(it)) },
                        onCheckout = { navController.navigate(Destinations.CHECKOUT) },
                        onContinueShopping = { navController.navigate(Destinations.HOME) }
                    )
                }

                composable(Destinations.PROFILE) {
                    val authViewModel: AuthViewModel = viewModel(
                        factory = containerViewModelFactory { AuthViewModel(container) }
                    )
                    val ordersViewModel: OrdersViewModel = viewModel(
                        factory = containerViewModelFactory { OrdersViewModel(container) }
                    )
                    val wishlistViewModel: WishlistViewModel = viewModel(
                        factory = containerViewModelFactory { WishlistViewModel(container) }
                    )
                    val cartViewModel: CartViewModel = viewModel(
                        factory = containerViewModelFactory { CartViewModel(container) }
                    )
                    ProfileScreen(
                        authViewModel = authViewModel,
                        ordersViewModel = ordersViewModel,
                        wishlistViewModel = wishlistViewModel,
                        cartViewModel = cartViewModel,
                        onSignIn = { navController.navigate(Destinations.AUTH) },
                        onOpenOrders = { navController.navigate(Destinations.ORDERS) },
                        onOpenWishlist = { navController.navigate(Destinations.WISHLIST) { launchSingleTop = true } },
                        onOpenCart = { navController.navigate(Destinations.CART) { launchSingleTop = true } }
                    )
                }

                composable(Destinations.CHECKOUT) {
                    val checkoutViewModel: CheckoutViewModel = viewModel(
                        factory = containerViewModelFactory { CheckoutViewModel(container) }
                    )
                    CheckoutScreen(
                        checkoutViewModel = checkoutViewModel,
                        onBack = { navController.popBackStack() },
                        onOrderPlaced = { orderId ->
                            navController.navigate(Destinations.order(orderId)) {
                                popUpTo(Destinations.CART) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Destinations.ORDERS) {
                    val ordersViewModel: OrdersViewModel = viewModel(
                        factory = containerViewModelFactory { OrdersViewModel(container) }
                    )
                    val authViewModel: AuthViewModel = viewModel(
                        factory = containerViewModelFactory { AuthViewModel(container) }
                    )
                    val user by authViewModel.currentUser.collectAsStateWithLifecycle()
                    OrdersScreen(
                        ordersViewModel = ordersViewModel,
                        isSignedIn = user != null,
                        onOrderClick = { navController.navigate(Destinations.order(it)) },
                        onSignIn = { navController.navigate(Destinations.AUTH) }
                    )
                }

                composable(
                    route = Destinations.ORDER_PATTERN,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { entry ->
                    val ordersViewModel: OrdersViewModel = viewModel(
                        factory = containerViewModelFactory { OrdersViewModel(container) }
                    )
                    OrderDetailScreen(
                        orderId = entry.arguments?.getString("orderId").orEmpty(),
                        ordersViewModel = ordersViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Destinations.PRODUCT_PATTERN,
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { entry ->
                    val catalogViewModel: CatalogViewModel = viewModel(
                        factory = containerViewModelFactory { CatalogViewModel(container) }
                    )
                    val cartViewModel: CartViewModel = viewModel(
                        factory = containerViewModelFactory { CartViewModel(container) }
                    )
                    val wishlistViewModel: WishlistViewModel = viewModel(
                        factory = containerViewModelFactory { WishlistViewModel(container) }
                    )
                    ProductDetailScreen(
                        productId = entry.arguments?.getString("productId").orEmpty(),
                        catalogViewModel = catalogViewModel,
                        cartViewModel = cartViewModel,
                        wishlistViewModel = wishlistViewModel,
                        onBack = { navController.popBackStack() },
                        onOpenCart = { navController.navigate(Destinations.CART) { launchSingleTop = true } }
                    )
                }

                composable(
                    route = Destinations.CATEGORY_PATTERN,
                    arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                ) { entry ->
                    val catalogViewModel: CatalogViewModel = viewModel(
                        factory = containerViewModelFactory { CatalogViewModel(container) }
                    )
                    val cartViewModel: CartViewModel = viewModel(
                        factory = containerViewModelFactory { CartViewModel(container) }
                    )
                    CategoryProductsScreen(
                        categoryId = entry.arguments?.getString("categoryId").orEmpty(),
                        catalogViewModel = catalogViewModel,
                        cartViewModel = cartViewModel,
                        onBack = { navController.popBackStack() },
                        onProductClick = { navController.navigate(Destinations.product(it)) }
                    )
                }

                composable(Destinations.AUTH) {
                    val authViewModel: AuthViewModel = viewModel(
                        factory = containerViewModelFactory { AuthViewModel(container) }
                    )
                    AuthScreen(
                        authViewModel = authViewModel,
                        onAuthenticated = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

/**
 * Maps a destination to the tab that should stay highlighted for it.
 * Product, category, checkout, order and auth screens belong to the
 * branch they were opened from, so the bar keeps its place.
 */
private fun selectedTabFor(route: String?): String? = when (route) {
    Destinations.HOME -> Destinations.HOME
    Destinations.SEARCH,
    Destinations.CATEGORY_PATTERN,
    Destinations.PRODUCT_PATTERN -> Destinations.SEARCH
    Destinations.WISHLIST -> Destinations.WISHLIST
    Destinations.CART,
    Destinations.CHECKOUT -> Destinations.CART
    Destinations.PROFILE,
    Destinations.ORDERS,
    Destinations.ORDER_PATTERN,
    Destinations.AUTH -> Destinations.PROFILE
    else -> null
}

private data class BottomItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomItems = listOf(
    BottomItem(Destinations.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomItem(Destinations.SEARCH, "Products", Icons.Filled.GridView, Icons.Outlined.GridView),
    BottomItem(Destinations.WISHLIST, "Wishlist", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    BottomItem(Destinations.CART, "Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    BottomItem(Destinations.PROFILE, "Profile", Icons.Filled.Person, Icons.Filled.Person)
)

@Composable
private fun SarvodhayaBottomBar(
    selectedTab: String?,
    cartCount: Int,
    onSelect: (String) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            bottomItems.forEach { item ->
                val selected = selectedTab == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    icon = {
                        if (item.route == Destinations.CART && cartCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = BrandAccent) { Text(cartCount.toString()) }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}
