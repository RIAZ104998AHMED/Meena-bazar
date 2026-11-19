package com.example.meenabazar

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class LocalAccount(val name: String, val email: String, val passwordHash: String, val salt: String)
data class StoreSnapshot(
    val accounts: List<LocalAccount> = emptyList(),
    val sessionEmail: String? = null,
    val baskets: Map<String, List<CartLine>> = emptyMap(),
    val orders: List<Order> = emptyList()
)

interface StorePersistence {
    fun read(): StoreSnapshot
    fun write(snapshot: StoreSnapshot)
}

object PasswordHasher {
    fun newSalt(): String = Base64.getEncoder().encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) })

    fun hash(password: String, salt: String): String {
        val specification = PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 120_000, 256)
        return try {
            Base64.getEncoder().encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(specification).encoded)
        } finally {
            specification.clearPassword()
        }
    }

    fun matches(password: String, account: LocalAccount): Boolean = MessageDigest.isEqual(
        Base64.getDecoder().decode(hash(password, account.salt)),
        Base64.getDecoder().decode(account.passwordHash)
    )
}

/** Local course-demo repository. All writes succeed before the in-memory snapshot changes. */
class BazarRepository(private val persistence: StorePersistence) {
    var snapshot: StoreSnapshot = persistence.read()
        private set

    val account: LocalAccount? get() = snapshot.accounts.find { it.email == snapshot.sessionEmail }
    private val basketOwner: String get() = account?.email ?: GUEST
    val cart: List<CartLine> get() = snapshot.baskets[basketOwner].orEmpty()
    val orders: List<Order> get() = snapshot.orders.filter { it.customerEmail == account?.email }.sortedByDescending { it.placedAt }

    private fun save(next: StoreSnapshot) {
        persistence.write(next)
        snapshot = next
    }

    fun register(data: RegistrationData) {
        require(AuthValidator.registration(data).isValid) { "Check the registration fields" }
        val email = AuthValidator.normalizeEmail(data.email)
        require(snapshot.accounts.none { it.email == email }) { "An account with this email already exists. Please log in." }
        val salt = PasswordHasher.newSalt()
        val newAccount = LocalAccount(data.fullName.trim(), email, PasswordHasher.hash(data.password, salt), salt)
        save(snapshot.copy(accounts = snapshot.accounts + newAccount, sessionEmail = email, baskets = mergeGuestCart(email)))
    }

    fun login(data: LoginData) {
        require(AuthValidator.login(data).isValid) { "Enter your email and password" }
        val email = AuthValidator.normalizeEmail(data.email)
        val found = snapshot.accounts.find { it.email == email }
        require(found != null && PasswordHasher.matches(data.password, found)) { "Email or password is incorrect" }
        save(snapshot.copy(sessionEmail = email, baskets = mergeGuestCart(email)))
    }

    private fun mergeGuestCart(email: String): Map<String, List<CartLine>> {
        val merged = (snapshot.baskets[email].orEmpty() + snapshot.baskets[GUEST].orEmpty())
            .groupBy { it.productId to it.size }
            .values.map { lines -> lines.first().copy(quantity = lines.sumOf { it.quantity }.coerceAtMost(MAX_QUANTITY)) }
        return snapshot.baskets + (email to merged) + (GUEST to emptyList())
    }

    fun logout() = save(snapshot.copy(sessionEmail = null))

    fun addToCart(productId: String, size: String) {
        val product = requireNotNull(Catalog.find(productId)) { "Product is unavailable" }
        require(size in product.sizes) { "Choose a size" }
        val existing = cart.find { it.productId == productId && it.size == size }
        require(existing == null || existing.quantity < MAX_QUANTITY) { "Maximum $MAX_QUANTITY per size" }
        val next = if (existing == null) cart + CartLine(productId, size, 1) else cart.map {
            if (it == existing) it.copy(quantity = it.quantity + 1) else it
        }
        save(snapshot.copy(baskets = snapshot.baskets + (basketOwner to next)))
    }

    fun changeQuantity(productId: String, size: String, quantity: Int) {
        require(quantity in 0..MAX_QUANTITY) { "Maximum $MAX_QUANTITY per size" }
        val next = cart.mapNotNull {
            if (it.productId == productId && it.size == size) {
                if (quantity == 0) null else it.copy(quantity = quantity)
            } else it
        }
        save(snapshot.copy(baskets = snapshot.baskets + (basketOwner to next)))
    }

    fun placeOrder(shipping: ShippingAddress): Order {
        val customer = requireNotNull(account) { "Please log in before checkout" }
        require(cart.isNotEmpty()) { "Your bag is empty" }
        require(CheckoutValidator.validate(shipping).isValid) { "Check your delivery details" }
        val order = Order(
            id = "MB-${UUID.randomUUID().toString().take(8).uppercase()}",
            customerEmail = customer.email,
            placedAt = System.currentTimeMillis(),
            items = cart.map { OrderItem(it.product.name, it.size, it.quantity, it.product.price) },
            shipping = shipping.copy(fullName = shipping.fullName.trim(), phone = shipping.phone.trim(), address = shipping.address.trim(), city = shipping.city.trim()),
            deliveryFee = Pricing.deliveryFee(cart)
        )
        save(snapshot.copy(orders = snapshot.orders + order, baskets = snapshot.baskets + (basketOwner to emptyList())))
        return order
    }

    companion object {
        private const val GUEST = "__guest__"
        const val MAX_QUANTITY = 10
    }
}
