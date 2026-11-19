package com.example.meenabazar

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.content.Context
import android.graphics.Bitmap
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

@RunWith(AndroidJUnit4::class)
class ShoppingFlowTest {
    @get:Rule(order = 0)
    val clearLocalDemo = TestRule { base, _ -> object : Statement() {
        override fun evaluate() {
            ApplicationProvider.getApplicationContext<Context>().getSharedPreferences("meena_local_store", Context.MODE_PRIVATE).edit().clear().commit()
            base.evaluate()
        }
    } }

    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()

    private fun start() {
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Explore as guest →").fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty() }
    }

    private fun fill(label: String, value: String) {
        compose.onNodeWithText(label).performScrollTo().performTextInput(value)
    }

    private fun click(label: String) {
        compose.onNodeWithText(label).performScrollTo().performClick()
    }

    private fun waitFor(label: String) {
        compose.waitUntil(15_000) { compose.onAllNodesWithText(label).fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty() }
    }

    private fun scrollBagTo(label: String) {
        compose.waitUntil(15_000) { compose.onAllNodesWithTag("bagList").fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty() }
        compose.onNodeWithTag("bagList").performScrollToNode(hasText(label))
    }

    private fun screenshot(name: String) {
        compose.waitForIdle()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val output = InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
        val file = File(output?.let(::File) ?: context.getExternalFilesDir(null), "screenshots/$name.png")
        file.parentFile?.mkdirs()
        file.outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun registrationReportsErrorsAndCanSwitchToLogin() {
        start()
        click("Create account")
        screenshot("registration")
        click("Sign up")
        compose.onNodeWithText("Full name is required").assertExists()
        compose.onNodeWithText("Email is required").assertExists()
        click("Already have an account? Log in")
        click("Log in")
        compose.onNodeWithText("Password is required").assertExists()
    }

    @Test fun guestCanSearchAndReturnToCatalogUsingSystemBack() {
        start()
        click("Explore as guest →")
        fill("Search clothing", "does-not-exist")
        compose.onNodeWithText("No styles found").assertExists()
        click("Reset filters")
        click("Sage Everyday Kurta")
        waitFor("Choose your size")
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        waitFor("Made for your everyday")
        compose.onNodeWithText("Made for your everyday").assertExists()
    }

    @Test fun fullGuestRegistrationCheckoutAndOrderHistoryFlow() {
        start()
        screenshot("welcome")
        click("Explore as guest →")
        screenshot("storefront")
        click("Sage Everyday Kurta")
        screenshot("product")
        click("Add to bag")
        compose.onNodeWithText("Please choose a size before adding to your bag").assertExists()
        click("M")
        click("Add to bag")
        waitFor("View bag (1)")
        click("View bag (1)")
        scrollBagTo("Log in to checkout")
        screenshot("bag")
        click("Log in to checkout")
        click("New here? Create account")
        fill("Full name", "Amina Rahman")
        fill("Email", "amina@example.com")
        fill("Password", "cotton123")
        fill("Confirm password", "cotton123")
        compose.onNodeWithText("Confirm password").performImeAction()
        scrollBagTo("Continue to checkout")
        click("Continue to checkout")
        fill("Phone number", "01712345678")
        fill("Street address", "House 12, Road 3")
        fill("City", "Dhaka")
        compose.onNodeWithText("City").performImeAction()
        screenshot("checkout")
        click("Place demo order")
        waitFor("A lovely choice.")
        screenshot("order-confirmation")
        click("View my orders")
        compose.onNodeWithText("Demo order saved").assertExists()
        compose.activityRule.scenario.recreate()
        waitFor("Your orders")
        compose.onNodeWithText("Your orders").assertExists()
    }

    @Test fun jsonSnapshotRoundTripsAccountsCartAndOrders() {
        val store = BazarRepository(LocalStorePersistence(ApplicationProvider.getApplicationContext()))
        store.register(RegistrationData("Amina Rahman", "amina@example.com", "cotton123", "cotton123"))
        store.addToCart("kurta-sage", "M")
        store.placeOrder(ShippingAddress("Amina Rahman", "01712345678", "House 12, Road 3", "Dhaka"))
        store.addToCart("tee-ink", "L")
        val restored = StoreJson.decode(StoreJson.encode(store.snapshot))
        org.junit.Assert.assertEquals(store.snapshot, restored)
        org.junit.Assert.assertFalse(StoreJson.encode(restored).contains("cotton123"))
    }
}
