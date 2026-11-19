package com.example.meenabazar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class BazarUiState(
    val loading: Boolean = true,
    val busy: Boolean = false,
    val screen: ScreenState = ScreenState.AUTHORIZATION,
    val customerName: String? = null,
    val customerEmail: String? = null,
    val cart: List<CartLine> = emptyList(),
    val orders: List<Order> = emptyList(),
    val selectedProduct: Product = Catalog.products.first(),
    val lastOrder: Order? = null,
    val error: String? = null,
    val notice: String? = null
)

class BazarViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: BazarRepository? = null
    private val mutableUi = MutableStateFlow(BazarUiState())
    val ui = mutableUi.asStateFlow()
    private var authReturn = ScreenState.SHOP
    private var authOrigin = ScreenState.AUTHORIZATION

    init { load() }

    fun load() {
        if (mutableUi.value.busy) return
        mutableUi.value = mutableUi.value.copy(loading = true, busy = true, error = null)
        viewModelScope.launch {
            try {
                repository = withContext(Dispatchers.IO) { BazarRepository(LocalStorePersistence(getApplication())) }
                refresh()
                mutableUi.value = mutableUi.value.copy(screen = if (repository?.account != null) ScreenState.SHOP else ScreenState.AUTHORIZATION)
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                mutableUi.value = mutableUi.value.copy(error = "Saved data could not be opened. Please retry. Your data has been kept.")
            } finally {
                mutableUi.value = mutableUi.value.copy(loading = false, busy = false)
            }
        }
    }

    private fun refresh() {
        val store = repository ?: return
        mutableUi.value = mutableUi.value.copy(
            customerName = store.account?.name,
            customerEmail = store.account?.email,
            cart = store.cart.toList(),
            orders = store.orders.toList()
        )
    }

    fun navigate(screen: ScreenState) {
        if (mutableUi.value.busy) return
        if (screen == ScreenState.LOGIN || screen == ScreenState.SIGN_UP) {
            if (mutableUi.value.screen !in listOf(ScreenState.LOGIN, ScreenState.SIGN_UP)) {
                authOrigin = mutableUi.value.screen
                authReturn = when (mutableUi.value.screen) {
                    ScreenState.CART, ScreenState.ORDERS, ScreenState.ACCOUNT -> mutableUi.value.screen
                    else -> ScreenState.SHOP
                }
            }
        }
        if (screen == ScreenState.CHECKOUT && mutableUi.value.customerEmail == null) {
            authReturn = ScreenState.CART
            authOrigin = ScreenState.CART
            mutableUi.value = mutableUi.value.copy(screen = ScreenState.LOGIN, error = null, notice = "Log in or create an account to continue. Your bag is saved.")
        } else {
            mutableUi.value = mutableUi.value.copy(screen = screen, error = null, notice = null)
        }
    }

    fun back() {
        val destination = when (mutableUi.value.screen) {
            ScreenState.LOGIN, ScreenState.SIGN_UP -> authOrigin
            ScreenState.CHECKOUT -> ScreenState.CART
            else -> ScreenState.SHOP
        }
        navigate(destination)
    }

    fun openProduct(product: Product) {
        if (!mutableUi.value.busy) mutableUi.value = mutableUi.value.copy(screen = ScreenState.PRODUCT, selectedProduct = product, error = null, notice = null)
    }

    private fun perform(success: () -> Unit = {}, action: (BazarRepository) -> Unit) {
        val store = repository ?: return
        if (mutableUi.value.busy) return
        mutableUi.value = mutableUi.value.copy(busy = true, error = null, notice = null)
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { action(store) }
                refresh()
                success()
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                mutableUi.value = mutableUi.value.copy(error = when (exception) {
                    is IllegalArgumentException, is IllegalStateException -> exception.message ?: "Could not complete this action"
                    else -> "Could not save your changes. Please try again."
                })
            } finally {
                mutableUi.value = mutableUi.value.copy(busy = false)
            }
        }
    }

    fun register(data: RegistrationData) = perform(success = {
        mutableUi.value = mutableUi.value.copy(screen = authReturn, notice = "Welcome to Meena Bazar, ${mutableUi.value.customerName}.")
    }) { it.register(data) }

    fun login(data: LoginData) = perform(success = {
        mutableUi.value = mutableUi.value.copy(screen = authReturn, notice = "Welcome back, ${mutableUi.value.customerName}.")
    }) { it.login(data) }

    fun logout() = perform(success = {
        authReturn = ScreenState.SHOP
        mutableUi.value = mutableUi.value.copy(screen = ScreenState.AUTHORIZATION, lastOrder = null)
    }) { it.logout() }

    fun addToCart(product: Product, size: String) = perform(success = {
        mutableUi.value = mutableUi.value.copy(notice = "Added to your bag")
    }) { it.addToCart(product.id, size) }

    fun changeQuantity(line: CartLine, quantity: Int) = perform { it.changeQuantity(line.productId, line.size, quantity) }

    fun placeOrder(shipping: ShippingAddress) = perform(success = {
        mutableUi.value = mutableUi.value.copy(screen = ScreenState.ORDER_SUCCESS, lastOrder = mutableUi.value.orders.firstOrNull())
    }) { it.placeOrder(shipping) }

    fun clearNotice() { mutableUi.value = mutableUi.value.copy(notice = null) }
}
