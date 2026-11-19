package com.example.meenabazar

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** One private, atomic JSON snapshot; excluded from cloud backup and device transfer. */
class LocalStorePersistence(context: Context) : StorePersistence {
    private val preferences = context.getSharedPreferences("meena_local_store", Context.MODE_PRIVATE)

    override fun read(): StoreSnapshot = preferences.getString("snapshot", null)?.let(StoreJson::decode) ?: StoreSnapshot()

    override fun write(snapshot: StoreSnapshot) {
        check(preferences.edit().putString("snapshot", StoreJson.encode(snapshot)).commit()) {
            "Could not save your changes. Please try again."
        }
    }
}

internal object StoreJson {
    fun encode(snapshot: StoreSnapshot): String = JSONObject().apply {
        put("version", 1)
        put("sessionEmail", snapshot.sessionEmail ?: JSONObject.NULL)
        put("accounts", JSONArray().apply {
            snapshot.accounts.forEach { account -> put(JSONObject().apply {
                put("name", account.name)
                put("email", account.email)
                put("hash", account.passwordHash)
                put("salt", account.salt)
            }) }
        })
        put("baskets", JSONObject().apply {
            snapshot.baskets.forEach { (owner, lines) -> put(owner, JSONArray().apply {
                lines.forEach { line -> put(JSONObject().apply {
                    put("productId", line.productId)
                    put("size", line.size)
                    put("quantity", line.quantity)
                }) }
            }) }
        })
        put("orders", JSONArray().apply {
            snapshot.orders.forEach { order -> put(JSONObject().apply {
                put("id", order.id)
                put("email", order.customerEmail)
                put("placedAt", order.placedAt)
                put("deliveryFee", order.deliveryFee)
                put("shipping", JSONObject().apply {
                    put("name", order.shipping.fullName)
                    put("phone", order.shipping.phone)
                    put("address", order.shipping.address)
                    put("city", order.shipping.city)
                })
                put("items", JSONArray().apply {
                    order.items.forEach { item -> put(JSONObject().apply {
                        put("name", item.name)
                        put("size", item.size)
                        put("quantity", item.quantity)
                        put("price", item.unitPrice)
                    }) }
                })
            }) }
        })
    }.toString()

    fun decode(raw: String): StoreSnapshot {
        val json = JSONObject(raw)
        require(json.getInt("version") == 1) { "Unsupported saved data version" }
        val accounts = json.getJSONArray("accounts").objects().map {
            LocalAccount(it.getString("name"), it.getString("email"), it.getString("hash"), it.getString("salt"))
        }
        val baskets = json.getJSONObject("baskets")
        return StoreSnapshot(
            accounts = accounts,
            sessionEmail = if (json.isNull("sessionEmail")) null else json.getString("sessionEmail").takeIf { email -> accounts.any { it.email == email } },
            baskets = baskets.keys().asSequence().associateWith { owner ->
                baskets.getJSONArray(owner).objects().mapNotNull {
                    val product = Catalog.find(it.getString("productId"))
                    val size = it.getString("size")
                    val quantity = it.getInt("quantity")
                    if (product != null && size in product.sizes && quantity in 1..BazarRepository.MAX_QUANTITY) CartLine(product.id, size, quantity) else null
                }
            },
            orders = json.getJSONArray("orders").objects().map {
                val shipping = it.getJSONObject("shipping")
                Order(
                    id = it.getString("id"),
                    customerEmail = it.getString("email"),
                    placedAt = it.getLong("placedAt"),
                    deliveryFee = it.getInt("deliveryFee"),
                    shipping = ShippingAddress(shipping.getString("name"), shipping.getString("phone"), shipping.getString("address"), shipping.getString("city")),
                    items = it.getJSONArray("items").objects().map { item ->
                        OrderItem(item.getString("name"), item.getString("size"), item.getInt("quantity"), item.getInt("price"))
                    }
                )
            }
        )
    }

    private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
}
