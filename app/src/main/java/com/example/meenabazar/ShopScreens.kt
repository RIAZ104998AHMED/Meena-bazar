package com.example.meenabazar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meenabazar.ui.theme.Forest
import java.text.DateFormat
import java.util.Date

@Composable
fun ShopScreen(onProduct: (Product) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    val products = remember(query, category) { Catalog.search(query, category) }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Surface(color = Forest, shape = RoundedCornerShape(24.dp)) {
                Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("THE EVERYDAY EDIT", color = Color(0xFFDDE7CE), letterSpacing = 2.sp, style = MaterialTheme.typography.labelSmall)
                        Text("Wear your\nown story.", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Serif)
                        Text("Easy pieces. Endless possibilities.", color = Color(0xFFDDE7CE), style = MaterialTheme.typography.bodyMedium)
                    }
                    ProductArtwork(Catalog.products.first(), Modifier.width(96.dp).height(150.dp), background = false)
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text("Search clothing") }, singleLine = true, shape = RoundedCornerShape(16.dp), trailingIcon = if (query.isNotEmpty()) ({ TextButton(onClick = { query = "" }) { Text("Clear") } }) else null)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Catalog.categories) { choice -> FilterChip(selected = category == choice, onClick = { category = choice }, label = { Text(choice) }) }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (query.isBlank() && category == "All") "Made for your everyday" else "$category styles", style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Serif, modifier = Modifier.weight(1f).semantics { heading() })
                Text("${products.size} pieces", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (products.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState("No styles found", "Try a different search or explore another category.", "Reset filters") { query = ""; category = "All" }
            }
        }
        items(products, key = { it.id }) { product ->
            Card(onClick = { onProduct(product) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                ProductArtwork(product, Modifier.fillMaxWidth().aspectRatio(.82f))
                Column(Modifier.padding(top = 12.dp, bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(product.category.uppercase(), style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(product.name, style = MaterialTheme.typography.titleSmall, minLines = 2)
                    Text(money(product.price), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text("Thoughtfully chosen, comfortably worn.\nFree delivery on demo orders from ${money(Pricing.freeDeliveryThreshold)}.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** Original code-drawn product illustrations, available without a network connection. */
@Composable
fun ProductArtwork(product: Product, modifier: Modifier = Modifier, background: Boolean = true) {
    val cloth = Color(product.color)
    val backdrop = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier.semantics { contentDescription = "${product.name} illustration" }.then(if (background) Modifier.background(backdrop, RoundedCornerShape(16.dp)) else Modifier)) {
        withTransform({ scale(size.width / 300f, size.height / 340f, pivot = Offset.Zero) }) {
            drawOval(Color.Black.copy(alpha = .07f), Offset(64f, 296f), Size(172f, 16f))
            val silhouette = Path().apply {
                when (product.style) {
                    "scarf" -> {
                        moveTo(96f, 50f); cubicTo(150f, 30f, 198f, 55f, 198f, 105f)
                        lineTo(174f, 290f); lineTo(133f, 284f); lineTo(146f, 124f)
                        lineTo(121f, 280f); lineTo(79f, 270f); lineTo(105f, 108f)
                        cubicTo(82f, 92f, 78f, 65f, 96f, 50f)
                    }
                    else -> {
                        val long = product.style == "kurta" || product.style == "dress"
                        val hem = if (long) 292f else 242f
                        val left = if (product.style == "dress") 66f else 98f
                        val right = if (product.style == "dress") 234f else 202f
                        moveTo(119f, 60f); lineTo(87f, 72f); lineTo(44f, 125f)
                        lineTo(76f, 150f); lineTo(101f, 121f); lineTo(left, hem)
                        quadraticTo(150f, hem + 12f, right, hem)
                        lineTo(199f, 121f); lineTo(224f, 150f); lineTo(256f, 125f)
                        lineTo(213f, 72f); lineTo(181f, 60f)
                        quadraticTo(150f, 91f, 119f, 60f)
                    }
                }
                close()
            }
            drawPath(silhouette, cloth)
            drawPath(silhouette, cloth.copy(red = (cloth.red * .8f), green = (cloth.green * .8f), blue = (cloth.blue * .8f)), style = Stroke(2f))
            if (product.style != "scarf") {
                drawLine(Color.White.copy(alpha = .25f), Offset(111f, 138f), Offset(109f, 226f), 2f)
                drawLine(Color.Black.copy(alpha = .09f), Offset(190f, 140f), Offset(194f, 231f), 2f)
                if (product.style == "shirt" || product.style == "kurta") {
                    drawLine(Color.Black.copy(alpha = .16f), Offset(150f, 83f), Offset(150f, if (product.style == "kurta") 168f else 243f), 2f)
                    repeat(if (product.style == "kurta") 3 else 6) { drawCircle(Color.White.copy(alpha = .8f), 2.5f, Offset(155f, 100f + it * 22f)) }
                }
                if (product.style == "dress") drawLine(Color.Black.copy(alpha = .13f), Offset(99f, 160f), Offset(201f, 160f), 3f)
            } else {
                repeat(6) { index -> drawLine(Color.White.copy(alpha = .3f), Offset(86f + index * 6f, 255f), Offset(86f + index * 6f, 281f), 2f) }
            }
        }
    }
}

@Composable
fun ProductScreen(ui: BazarUiState, onAdd: (Product, String) -> Unit, onBag: () -> Unit) {
    val product = ui.selectedProduct
    var size by rememberSaveable(product.id) { mutableStateOf(if (product.sizes.size == 1) product.sizes.first() else "") }
    var showError by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        ProductArtwork(product, Modifier.fillMaxWidth().height(310.dp))
        Text(product.category.uppercase(), style = MaterialTheme.typography.labelMedium, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(product.name, style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif)
        Text(money(product.price), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(product.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Choose your size", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(product.sizes) { choice -> FilterChip(selected = size == choice, onClick = { size = choice; showError = false }, label = { Text(choice) }, enabled = !ui.busy) }
        }
        if (showError) ValidationError("Please choose a size before adding to your bag")
        PrimaryAction(if (ui.busy) "Saving…" else "Add to bag", !ui.busy) {
            if (size.isEmpty()) showError = true else onAdd(product, size)
        }
        OutlinedButton(onClick = onBag, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("View bag (${ui.cart.sumOf { it.quantity }})") }
        HorizontalDivider()
        Text("THE DETAILS", style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
        Text("Comfortable fit · Easy everyday styling\nCare: gentle cold wash, dry in shade.\nIllustrations represent the sample collection.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CartScreen(ui: BazarUiState, onQuantity: (CartLine, Int) -> Unit, onCheckout: () -> Unit, onShop: () -> Unit) {
    if (ui.cart.isEmpty()) {
        EmptyState("Room for something lovely.", "Your bag is empty. Find a new everyday favorite.", "Explore the collection", onShop)
        return
    }
    LazyColumn(modifier = Modifier.testTag("bagList"), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Text("Your bag", style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Serif) }
        items(ui.cart, key = { "${it.productId}/${it.size}" }) { line ->
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .4f)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        ProductArtwork(line.product, Modifier.width(76.dp).height(96.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(line.product.name, style = MaterialTheme.typography.titleMedium)
                            Text("Size: ${line.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(money(line.total), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = { onQuantity(line, line.quantity - 1) }, enabled = !ui.busy, modifier = Modifier.semantics { contentDescription = "Decrease ${line.product.name}, size ${line.size}" }, contentPadding = PaddingValues(0.dp)) { Text("−", fontSize = 20.sp) }
                        Text("${line.quantity}", Modifier.padding(horizontal = 16.dp))
                        OutlinedButton(onClick = { onQuantity(line, line.quantity + 1) }, enabled = !ui.busy && line.quantity < BazarRepository.MAX_QUANTITY, modifier = Modifier.semantics { contentDescription = "Increase ${line.product.name}, size ${line.size}" }, contentPadding = PaddingValues(0.dp)) { Text("+", fontSize = 20.sp) }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { onQuantity(line, 0) }, enabled = !ui.busy) { Text("Remove") }
                    }
                }
            }
        }
        item { PriceSummary(ui.cart) }
        item { PrimaryAction(if (ui.customerEmail == null) "Log in to checkout" else "Continue to checkout", !ui.busy, onCheckout) }
        item { Text("Free delivery from ${money(Pricing.freeDeliveryThreshold)}. All orders in this app are demonstrations.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
fun PriceSummary(cart: List<CartLine>) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        HorizontalDivider()
        PriceRow("Subtotal", money(Pricing.subtotal(cart)))
        PriceRow("Delivery", if (Pricing.deliveryFee(cart) == 0) "Free" else money(Pricing.deliveryFee(cart)))
        HorizontalDivider()
        PriceRow("Total", money(Pricing.total(cart)), emphasis = true)
    }
}

@Composable
fun PriceRow(label: String, value: String, emphasis: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (emphasis) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontWeight = if (emphasis) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
fun CheckoutScreen(ui: BazarUiState, onPlaceOrder: (ShippingAddress) -> Unit) {
    var name by rememberSaveable { mutableStateOf(ui.customerName.orEmpty()) }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val shipping = ShippingAddress(name, phone, address, city)
    val errors = if (submitted) CheckoutValidator.validate(shipping) else ShippingErrors()
    val submit = {
        submitted = true
        if (CheckoutValidator.validate(shipping).isValid && !ui.busy) onPlaceOrder(shipping)
    }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("The final details.", style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Serif)
        Text("Delivery details", style = MaterialTheme.typography.titleMedium)
        SimpleField(name, { name = it }, "Recipient name", error = errors.fullName, enabled = !ui.busy)
        SimpleField(phone, { phone = it }, "Phone number", error = errors.phone, keyboardType = KeyboardType.Phone, enabled = !ui.busy)
        SimpleField(address, { address = it }, "Street address", error = errors.address, enabled = !ui.busy, singleLine = false)
        SimpleField(city, { city = it }, "City", error = errors.city, enabled = !ui.busy, onDone = { focus.clearFocus() })
        Text("Order summary · ${ui.cart.sumOf { it.quantity }} items", style = MaterialTheme.typography.titleMedium)
        ui.cart.forEach { Text("${it.quantity} × ${it.product.name} · ${it.size}", style = MaterialTheme.typography.bodyMedium) }
        PriceSummary(ui.cart)
        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp)) {
            Text("Demo checkout\nNo payment is collected and no delivery is scheduled. This order is saved only on your device.", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.bodyMedium)
        }
        PrimaryAction(if (ui.busy) "Placing order…" else "Place demo order", !ui.busy && ui.cart.isNotEmpty(), submit)
    }
}

@Composable
fun OrdersScreen(ui: BazarUiState, onShop: () -> Unit, onLogin: () -> Unit) {
    if (ui.customerEmail == null) {
        EmptyState("Your stories start here.", "Log in to see the demo orders saved for your account.", "Log in", onLogin)
    } else if (ui.orders.isEmpty()) {
        EmptyState("Your first favorite awaits.", "Once you place a demo order, you can find it here.", "Explore the collection", onShop)
    } else {
        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item { Text("Your orders", style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Serif) }
            items(ui.orders, key = { it.id }) { order -> OrderCard(order) }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    OutlinedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(order.id, style = MaterialTheme.typography.titleMedium)
            Text(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(order.placedAt)), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            Text("Demo order saved", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            order.items.forEach { item ->
                Text("${item.quantity} × ${item.name} · ${item.size}\n${money(item.unitPrice * item.quantity)}", style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider()
            PriceRow("Delivery", if (order.deliveryFee == 0) "Free" else money(order.deliveryFee))
            PriceRow("Total", money(order.total), emphasis = true)
            Text("${order.shipping.fullName}\n${order.shipping.address}, ${order.shipping.city}\n${order.shipping.phone}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            Text("No payment collected. No delivery scheduled.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun OrderSuccessScreen(order: Order?, onOrders: () -> Unit, onShop: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("✓", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Text("A lovely choice.", style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Serif)
        Text("Your demo order has been saved. No payment was collected and no delivery will be made.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        order?.let { OrderCard(it) }
        PrimaryAction("View my orders", onClick = onOrders)
        TextButton(onClick = onShop, modifier = Modifier.fillMaxWidth()) { Text("Keep exploring") }
    }
}

@Composable
fun AccountScreen(ui: BazarUiState, onLogin: () -> Unit, onRegister: () -> Unit, onOrders: () -> Unit, onLogout: () -> Unit) {
    var confirmLogout by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Your little corner.", style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Serif)
        if (ui.customerEmail == null) {
            Text("You're exploring as a guest. Create an account to save your demo orders.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            PrimaryAction("Log in", onClick = onLogin)
            OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Create account") }
        } else {
            Text(ui.customerName.orEmpty(), style = MaterialTheme.typography.headlineSmall)
            Text(ui.customerEmail, color = MaterialTheme.colorScheme.onSurfaceVariant)
            PrimaryAction("My orders", onClick = onOrders)
            OutlinedButton(onClick = { confirmLogout = true }, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Log out") }
        }
        HorizontalDivider()
        Text("MEENA BAZAR", style = MaterialTheme.typography.labelLarge, letterSpacing = 3.sp)
        Text("Thoughtful styles for everyday stories.\n\nIntroduction to Software Engineering\nCourse project · Version 1.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("This offline demo keeps accounts, bags and orders on this device. Uninstalling or clearing app data removes them. It does not process payments or connect to a store.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    if (confirmLogout) AlertDialog(
        onDismissRequest = { confirmLogout = false },
        title = { Text("Log out?") },
        text = { Text("Your bag and orders will be waiting when you log in again on this device.") },
        confirmButton = { TextButton(onClick = { confirmLogout = false; onLogout() }) { Text("Log out") } },
        dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Stay here") } }
    )
}

@Composable
fun EmptyState(title: String, message: String, action: String, onAction: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif)
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        PrimaryAction(action, onClick = onAction)
    }
}
