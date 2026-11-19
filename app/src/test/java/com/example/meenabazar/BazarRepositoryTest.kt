package com.example.meenabazar

import org.junit.Assert.*
import org.junit.Test

class BazarRepositoryTest {
    private class MemoryStore(var saved: StoreSnapshot = StoreSnapshot()) : StorePersistence {
        var failWrites = false
        override fun read() = saved
        override fun write(snapshot: StoreSnapshot) {
            check(!failWrites) { "Disk full" }
            saved = snapshot
        }
    }

    private val registration = RegistrationData("Amina Rahman", "amina@example.com", "cotton123", "cotton123")
    private val address = ShippingAddress("Amina Rahman", "01712345678", "House 12, Road 3", "Dhaka")
    private val product = Catalog.products.first()

    @Test fun registrationStoresHashAndRestoresSession() {
        val disk = MemoryStore()
        val store = BazarRepository(disk)
        store.register(registration.copy(email = " AMINA@example.com "))
        assertEquals("amina@example.com", store.account?.email)
        assertNotEquals(registration.password, store.account?.passwordHash)
        assertTrue(PasswordHasher.matches(registration.password, store.account!!))
        assertEquals(store.account, BazarRepository(disk).account)
    }

    @Test fun duplicateAccountIsRejectedIgnoringEmailCase() {
        val store = BazarRepository(MemoryStore())
        store.register(registration)
        assertThrows(IllegalArgumentException::class.java) { store.register(registration.copy(email = "AMINA@example.com")) }
        assertEquals(1, store.snapshot.accounts.size)
    }

    @Test fun invalidRegistrationIsNeverSaved() {
        val store = BazarRepository(MemoryStore())
        assertThrows(IllegalArgumentException::class.java) { store.register(registration.copy(confirmPassword = "wrong")) }
        assertTrue(store.snapshot.accounts.isEmpty())
    }

    @Test fun wrongPasswordAndUnknownAccountCannotLogIn() {
        val store = BazarRepository(MemoryStore())
        store.register(registration)
        store.logout()
        assertThrows(IllegalArgumentException::class.java) { store.login(LoginData(registration.email, "wrong")) }
        assertThrows(IllegalArgumentException::class.java) { store.login(LoginData("unknown@example.com", registration.password)) }
        assertNull(store.account)
        store.login(LoginData(" AMINA@example.com ", registration.password))
        assertEquals(registration.fullName, store.account?.name)
    }

    @Test fun samePasswordGetsDifferentSalts() {
        val store = BazarRepository(MemoryStore())
        store.register(registration)
        store.logout()
        store.register(registration.copy(email = "other@example.com"))
        assertNotEquals(store.snapshot.accounts[0].salt, store.snapshot.accounts[1].salt)
        assertNotEquals(store.snapshot.accounts[0].passwordHash, store.snapshot.accounts[1].passwordHash)
    }

    @Test fun sameVariantIncrementsQuantityButSizesRemainSeparate() {
        val store = BazarRepository(MemoryStore())
        store.addToCart(product.id, "M")
        store.addToCart(product.id, "M")
        store.addToCart(product.id, "L")
        assertEquals(listOf(2, 1), store.cart.map { it.quantity })
        assertEquals(product.price * 3, Pricing.subtotal(store.cart))
    }

    @Test fun invalidVariantsAndExcessQuantitiesAreRejected() {
        val store = BazarRepository(MemoryStore())
        assertThrows(IllegalArgumentException::class.java) { store.addToCart("missing", "M") }
        assertThrows(IllegalArgumentException::class.java) { store.addToCart(product.id, "XXS") }
        repeat(10) { store.addToCart(product.id, "M") }
        assertThrows(IllegalArgumentException::class.java) { store.addToCart(product.id, "M") }
        assertThrows(IllegalArgumentException::class.java) { store.changeQuantity(product.id, "M", -1) }
        assertEquals(10, store.cart.single().quantity)
    }

    @Test fun removingLastItemEmptiesBasketAndDeliveryFee() {
        val store = BazarRepository(MemoryStore())
        store.addToCart(product.id, "M")
        store.changeQuantity(product.id, "M", 0)
        assertTrue(store.cart.isEmpty())
        assertEquals(0, Pricing.total(store.cart))
    }

    @Test fun guestBasketMergesIntoAccountAndIsCleared() {
        val disk = MemoryStore()
        val store = BazarRepository(disk)
        store.addToCart(product.id, "M")
        store.register(registration)
        assertEquals(1, store.cart.single().quantity)
        store.logout()
        assertTrue(store.cart.isEmpty())
        store.addToCart(product.id, "M")
        store.login(LoginData(registration.email, registration.password))
        assertEquals(2, store.cart.single().quantity)
        assertEquals(store.cart, BazarRepository(disk).cart)
    }

    @Test fun cartAndOrdersStayPrivateBetweenCustomers() {
        val store = BazarRepository(MemoryStore())
        store.register(registration)
        store.addToCart(product.id, "M")
        store.placeOrder(address)
        store.addToCart(product.id, "L")
        store.logout()
        store.register(registration.copy(email = "other@example.com"))
        assertTrue(store.orders.isEmpty())
        assertTrue(store.cart.isEmpty())
        store.logout()
        store.login(LoginData(registration.email, registration.password))
        assertEquals("L", store.cart.single().size)
        assertEquals(1, store.orders.size)
    }

    @Test fun guestCannotPlaceOrder() {
        val store = BazarRepository(MemoryStore())
        store.addToCart(product.id, "M")
        assertThrows(IllegalArgumentException::class.java) { store.placeOrder(address) }
        assertEquals(1, store.cart.size)
    }

    @Test fun orderAtomicallySnapshotsPricesAndClearsCart() {
        val disk = MemoryStore()
        val store = BazarRepository(disk)
        store.register(registration)
        store.addToCart(product.id, "M")
        val total = Pricing.total(store.cart)
        val order = store.placeOrder(address)
        assertEquals(total, order.total)
        assertEquals(product.price, order.items.single().unitPrice)
        assertTrue(store.cart.isEmpty())
        assertEquals(order, BazarRepository(disk).orders.single())
        assertThrows(IllegalArgumentException::class.java) { store.placeOrder(address) }
    }

    @Test fun invalidDeliveryDetailsDoNotClearCart() {
        val store = BazarRepository(MemoryStore())
        store.register(registration)
        store.addToCart(product.id, "M")
        assertThrows(IllegalArgumentException::class.java) { store.placeOrder(ShippingAddress()) }
        assertEquals(1, store.cart.size)
        assertTrue(store.orders.isEmpty())
    }

    @Test fun failedWriteDoesNotLoseBasketOrCreateOrder() {
        val disk = MemoryStore()
        val store = BazarRepository(disk)
        store.register(registration)
        store.addToCart(product.id, "M")
        disk.failWrites = true
        assertThrows(IllegalStateException::class.java) { store.placeOrder(address) }
        assertEquals(1, store.cart.size)
        assertTrue(store.orders.isEmpty())
        disk.failWrites = false
        store.placeOrder(address)
        assertEquals(1, store.orders.size)
    }
}
