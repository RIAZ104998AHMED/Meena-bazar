package com.example.meenabazar

import org.junit.Assert.*
import org.junit.Test

class ShopModelsTest {
    @Test fun searchCombinesCategoryAndCaseInsensitiveQuery() {
        assertEquals(1, Catalog.search(" SAGE ", "Women").size)
        assertTrue(Catalog.search("SAGE", "Men").isEmpty())
        assertEquals(8, Catalog.search("", "All").size)
        assertEquals(2, Catalog.search("kids", "Kids").size)
    }

    @Test fun deliveryIsFreeAtThresholdAndForEmptyCart() {
        assertEquals(0, Pricing.deliveryFee(emptyList()))
        assertEquals(80, Pricing.deliveryFee(listOf(CartLine("tee-ink", "M", 1))))
        assertEquals(0, Pricing.deliveryFee(listOf(CartLine("shirt-sand", "M", 2))))
        assertEquals(1_970, Pricing.total(listOf(CartLine("kurta-sage", "M", 1))))
    }

    @Test fun deliveryDetailsRequireUsefulNamesAddressesAndPhoneNumbers() {
        val valid = ShippingAddress("Amina Rahman", "+880 1712-345678", "House 12, Road 3", "Dhaka")
        assertTrue(CheckoutValidator.validate(valid).isValid)
        assertFalse(CheckoutValidator.validate(ShippingAddress()).isValid)
        listOf("letters1234", "123", "+-()---", "1234567890123456").forEach { phone ->
            assertNotNull(CheckoutValidator.validate(valid.copy(phone = phone)).phone)
        }
    }

    @Test fun currencyAndOrderTotalsAreConsistent() {
        assertEquals("৳1,890", money(1890))
        val order = Order("id", "a@example.com", 0, listOf(OrderItem("Shirt", "M", 2, 100)), ShippingAddress(), 80)
        assertEquals(200, order.subtotal)
        assertEquals(280, order.total)
    }
}
