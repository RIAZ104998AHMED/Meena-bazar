package com.example.meenabazar

import java.text.NumberFormat
import java.util.Locale

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val color: Long,
    val style: String,
    val description: String,
    val sizes: List<String> = listOf("S", "M", "L", "XL")
)

object Catalog {
    val products = listOf(
        Product("kurta-sage", "Sage Everyday Kurta", "Women", 1890, 0xFF839784, "kurta", "An easy, breathable cotton kurta with a relaxed fit and a soft sage finish. Made for everyday moments."),
        Product("dress-rose", "Rose Linen Dress", "Women", 2790, 0xFFBE8B89, "dress", "A flowing midi silhouette in a soft linen blend, with comfortable sleeves and room to move."),
        Product("shirt-sand", "Sand Oxford Shirt", "Men", 1590, 0xFFC4AB89, "shirt", "A timeless button-down shirt in breathable cotton. A versatile layer for workdays and weekends."),
        Product("tee-ink", "Essential Cotton Tee", "Men", 790, 0xFF48586B, "tee", "Your everyday essential, with a comfortable crew neck and soft, midweight cotton."),
        Product("kurta-midnight", "Midnight Festive Kurta", "Men", 2490, 0xFF5C627A, "kurta", "A refined long kurta with subtle detailing, designed for celebrations and special evenings."),
        Product("dress-marigold", "Marigold Day Dress", "Kids", 1290, 0xFFD1A55C, "dress", "A cheerful cotton dress for little adventures. A relaxed cut keeps playtime comfortable.", listOf("2–3Y", "4–5Y", "6–7Y", "8–9Y")),
        Product("scarf-clay", "Clay Woven Scarf", "Accessories", 590, 0xFFB97F65, "scarf", "A light, textured woven scarf that adds warmth and a finishing touch to your favorite outfits.", listOf("One size")),
        Product("tee-forest", "Little Explorer Tee", "Kids", 690, 0xFF6E8774, "tee", "Soft cotton, a classic fit and a forest-green finish. Ready for a full day of exploring.", listOf("2–3Y", "4–5Y", "6–7Y", "8–9Y"))
    )
    val categories = listOf("All", "Women", "Men", "Kids", "Accessories")
    fun find(id: String): Product? = products.find { it.id == id }

    fun search(query: String, category: String): List<Product> = products.filter {
        (category == "All" || it.category == category) &&
            (it.name.contains(query.trim(), ignoreCase = true) || it.category.contains(query.trim(), ignoreCase = true))
    }
}

fun money(amount: Int): String = "৳${NumberFormat.getIntegerInstance(Locale.US).format(amount)}"

data class CartLine(val productId: String, val size: String, val quantity: Int) {
    val product: Product get() = requireNotNull(Catalog.find(productId))
    val total: Int get() = product.price * quantity
}

data class ShippingAddress(val fullName: String = "", val phone: String = "", val address: String = "", val city: String = "")

data class ShippingErrors(val fullName: String? = null, val phone: String? = null, val address: String? = null, val city: String? = null) {
    val isValid: Boolean get() = listOf(fullName, phone, address, city).all { it == null }
}

object CheckoutValidator {
    fun validate(shipping: ShippingAddress) = ShippingErrors(
        fullName = if (shipping.fullName.trim().length !in 2..80) "Enter a recipient name (2–80 characters)" else null,
        phone = if (!Regex("^\\+?[0-9 ()-]{7,24}$").matches(shipping.phone.trim()) || shipping.phone.count { it.isDigit() } !in 7..15) "Enter a valid phone number" else null,
        address = if (shipping.address.trim().length !in 8..240) "Enter a full street address (8–240 characters)" else null,
        city = if (shipping.city.trim().length !in 2..80) "Enter a city (2–80 characters)" else null
    )
}

data class OrderItem(val name: String, val size: String, val quantity: Int, val unitPrice: Int)
data class Order(
    val id: String,
    val customerEmail: String,
    val placedAt: Long,
    val items: List<OrderItem>,
    val shipping: ShippingAddress,
    val deliveryFee: Int
) {
    val subtotal: Int get() = items.sumOf { it.unitPrice * it.quantity }
    val total: Int get() = subtotal + deliveryFee
}

object Pricing {
    const val freeDeliveryThreshold = 3000
    const val standardDeliveryFee = 80
    fun subtotal(cart: List<CartLine>): Int = cart.sumOf { it.total }
    fun deliveryFee(cart: List<CartLine>): Int = if (cart.isEmpty() || subtotal(cart) >= freeDeliveryThreshold) 0 else standardDeliveryFee
    fun total(cart: List<CartLine>): Int = subtotal(cart) + deliveryFee(cart)
}
