package com.example.meenabazar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.meenabazar.ui.theme.MeenaBazarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(this)[BazarViewModel::class.java]
        splash.setKeepOnScreenCondition { viewModel.ui.value.loading }
        setContent {
            MeenaBazarTheme { MeenaBazarApp(viewModel) }
        }
    }
}

@Composable
fun MeenaBazarApp(viewModel: BazarViewModel) {
    val ui by viewModel.ui.collectAsState()
    val authentication = ui.screen in listOf(ScreenState.AUTHORIZATION, ScreenState.LOGIN, ScreenState.SIGN_UP)
    val tabs = listOf(ScreenState.SHOP, ScreenState.CART, ScreenState.ORDERS, ScreenState.ACCOUNT)
    BackHandler(enabled = ui.busy || ui.screen !in listOf(ScreenState.AUTHORIZATION, ScreenState.SHOP)) {
        if (!ui.busy) viewModel.back()
    }
    Scaffold(
        topBar = {
            if (!authentication && !ui.loading) {
                Surface(shadowElevation = 1.dp) {
                    Row(
                        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (ui.screen !in tabs) TextButton(onClick = viewModel::back, enabled = !ui.busy) { Text("← Back") }
                        Text("meena bazar", style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Serif, modifier = Modifier.weight(1f))
                        Text("EVERYDAY STYLE", letterSpacing = 1.sp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        bottomBar = {
            if (ui.screen in tabs && !ui.loading) {
                NavigationBar {
                    tabs.forEach { destination ->
                        val label = when (destination) {
                            ScreenState.SHOP -> "Explore"
                            ScreenState.CART -> "Bag"
                            ScreenState.ORDERS -> "Orders"
                            else -> "Account"
                        }
                        NavigationBarItem(
                            selected = ui.screen == destination,
                            enabled = !ui.busy,
                            onClick = { viewModel.navigate(destination) },
                            icon = {
                                Text(when (destination) {
                                    ScreenState.SHOP -> "⌂"
                                    ScreenState.CART -> if (ui.cart.isEmpty()) "◇" else "${ui.cart.sumOf { it.quantity }}"
                                    ScreenState.ORDERS -> "≡"
                                    else -> "○"
                                }, fontSize = 24.sp)
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
            if (ui.busy && !ui.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (ui.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (ui.error != null && ui.customerEmail == null && ui.screen == ScreenState.AUTHORIZATION) {
                EmptyState("Let's try that again.", ui.error.orEmpty(), "Retry", viewModel::load)
            } else {
                if (!authentication) ui.error?.let { Surface(color = MaterialTheme.colorScheme.errorContainer) { Text(it, Modifier.fillMaxWidth().padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer) } }
                if (!authentication) ui.notice?.let { notice ->
                    Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(notice, Modifier.weight(1f).padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = viewModel::clearNotice) { Text("Dismiss") }
                        }
                    }
                }
                Box(Modifier.weight(1f)) {
                    when (ui.screen) {
                        ScreenState.AUTHORIZATION -> LoginScreen(
                            onSignUpClick = { viewModel.navigate(ScreenState.SIGN_UP) },
                            onLoginClick = { viewModel.navigate(ScreenState.LOGIN) },
                            onGuestClick = { viewModel.navigate(ScreenState.SHOP) }
                        )
                        ScreenState.LOGIN -> LoginFormScreen(ui, viewModel::back, viewModel::login) { viewModel.navigate(ScreenState.SIGN_UP) }
                        ScreenState.SIGN_UP -> SignUpScreen(ui, viewModel::back, viewModel::register) { viewModel.navigate(ScreenState.LOGIN) }
                        ScreenState.SHOP -> ShopScreen(viewModel::openProduct)
                        ScreenState.PRODUCT -> ProductScreen(ui, viewModel::addToCart) { viewModel.navigate(ScreenState.CART) }
                        ScreenState.CART -> CartScreen(ui, viewModel::changeQuantity, { viewModel.navigate(ScreenState.CHECKOUT) }) { viewModel.navigate(ScreenState.SHOP) }
                        ScreenState.CHECKOUT -> CheckoutScreen(ui, viewModel::placeOrder)
                        ScreenState.ORDERS -> OrdersScreen(ui, { viewModel.navigate(ScreenState.SHOP) }) { viewModel.navigate(ScreenState.LOGIN) }
                        ScreenState.ORDER_SUCCESS -> OrderSuccessScreen(ui.lastOrder, { viewModel.navigate(ScreenState.ORDERS) }) { viewModel.navigate(ScreenState.SHOP) }
                        ScreenState.ACCOUNT -> AccountScreen(ui, { viewModel.navigate(ScreenState.LOGIN) }, { viewModel.navigate(ScreenState.SIGN_UP) }, { viewModel.navigate(ScreenState.ORDERS) }, viewModel::logout)
                    }
                }
            }
        }
    }
}
