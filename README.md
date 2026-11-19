# Meena Bazar

An Android clothing-shopping app built with Kotlin and Jetpack Compose for the **Introduction to Software Engineering** course. This version completes the startup and authentication stage and provides a working offline shopping demonstration.

## What works

- Branded Android splash screen and welcome page.
- Registration with full-name, email, password and confirmation validation; inline errors and password visibility controls.
- Login, duplicate-email detection, persistent local sessions and logout.
- Guest browsing, eight sample clothing products, search and category filters.
- Product details, size selection, persistent shopping bags and quantity controls.
- Validated delivery forms, price breakdowns and demo checkout.
- Per-customer order history and order confirmation.
- Light/dark themes, scrollable forms, keyboard handling and system Back navigation.

**Scope:** Accounts, bags and orders are stored on the device. There is no server, payment gateway, real inventory or delivery service. Checkout explicitly creates a demo order. Clearing app data or uninstalling removes saved data. This is a course prototype, not a production commerce service.

## Run the app

1. Open this repository root in Android Studio and let Gradle sync.
2. Use JDK 17 or newer (Android Studio's bundled JDK works).
3. Install Android SDK Platform 35 and accept SDK licenses. Let Android Studio generate the ignored `local.properties` file for your machine.
4. Select an emulator/device running Android 9 (API 28) or newer and run the `app` configuration.

Only `:app` is included in the Gradle build. The existing Gradle wrapper and dependency versions are retained.

From PowerShell at the repository root:

```powershell
# If Java is not already configured:
$env:JAVA_HOME = 'C:/Program Files/Android/Android Studio/jbr'

./gradlew.bat assembleDebug
./gradlew.bat testDebugUnitTest
./gradlew.bat lintDebug
./gradlew.bat connectedDebugAndroidTest  # requires a booted emulator/device
```

On macOS/Linux use `./gradlew` instead. The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Try the complete flow

1. Choose **Explore as guest** and select **Sage Everyday Kurta**.
2. Choose a size, add it to your bag and open **View bag**.
3. Choose **Log in to checkout**, then **New here? Create account**.
4. Enter your name, an email and a password of 8–128 characters; repeat the password. No email service is contacted.
5. Your guest bag carries over to the new account. Continue to checkout.
6. Enter a recipient, phone, street address and city. Review the total and choose **Place demo order**.
7. Open **Orders** to see the saved order. Log out through **Account**, then log back in to verify persistence.

There are no preinstalled accounts. Use invented contact and address details for demonstrations.

## Chosen defaults

The brief did not specify pricing or a catalog. This implementation uses a small original sample collection, prices in Bangladeshi taka (BDT), an 80 BDT delivery fee and free demo delivery from 3,000 BDT. Sizes are product-specific and each size has a 10-item quantity limit. These are demonstration rules, not live stock or shipping estimates.

## Code organization

| File | Responsibility |
| --- | --- |
| [MainActivity.kt](app/src/main/java/com/example/meenabazar/MainActivity.kt) | Native splash, application shell, navigation and system Back |
| [AuthScreens.kt](app/src/main/java/com/example/meenabazar/AuthScreens.kt) | Welcome, registration, login and reusable fields |
| [AuthModels.kt](app/src/main/java/com/example/meenabazar/AuthModels.kt) | Authentication data and pure validation |
| [ShopScreens.kt](app/src/main/java/com/example/meenabazar/ShopScreens.kt) | Catalog, product illustrations, bag, checkout, orders and account |
| [ShopModels.kt](app/src/main/java/com/example/meenabazar/ShopModels.kt) | Sample catalog, delivery validation and integer-based pricing |
| [BazarViewModel.kt](app/src/main/java/com/example/meenabazar/BazarViewModel.kt) | Screen state, serialized actions and background work |
| [BazarRepository.kt](app/src/main/java/com/example/meenabazar/BazarRepository.kt) | Accounts, password hashing, customer bags and order transactions |
| [LocalStorePersistence.kt](app/src/main/java/com/example/meenabazar/LocalStorePersistence.kt) | Versioned private JSON persistence |
| [ui/theme/](app/src/main/java/com/example/meenabazar/ui/theme/) | Shared colors and typography |

The UI observes an immutable `StateFlow`. The ViewModel survives rotation and runs password hashing and disk operations on an IO dispatcher. The repository validates operations independently of the UI. It saves the full next snapshot before changing in-memory state, so a failed checkout write keeps the bag intact. Cold launches restore the signed-in account, its bag and its orders; transient navigation starts at the storefront.

Passwords are stored as salted PBKDF2-HMAC-SHA256 hashes (120,000 iterations), never as plaintext. Password fields are excluded from saved-instance-state bundles. Local data is excluded from Android backup and transfer. This local model is for demonstration; a real service must authenticate on a server.

## Verification

JVM tests cover registration and login validation, salted hashes, duplicate accounts, login failures, customer isolation, guest-bag merging, size and quantity rules, delivery validation, totals, persisted sessions and failed checkout writes. Device tests cover form errors, navigation, search, a complete guest-to-registration-to-order journey, rotation and JSON persistence. UI tests reset the demo store, so run them on a test device.

Reports are generated under `app/build/reports/`. See the [course project report](docs/PROJECT_REPORT.md) for requirements, design and acceptance criteria.

The completed verification passed **25 JVM tests and 5 Android device tests**, with a successful APK build and no lint errors. See [test results and screenshots](docs/TEST_RESULTS.md) for the exact command, environment and remaining lint advisories.

## Remaining production work

A real launch needs an authenticated backend, server-side inventory and pricing, verified email/password recovery, secure payment integration, an order-fulfillment service and privacy/support policies. Social sign-in, real product photography and remote catalog administration are outside this offline version.
