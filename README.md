# Meena Bazar – Android Shopping Application

## Project Overview

Meena Bazar is an Android clothing-shopping application developed as part of the **Introduction to Software Engineering** course.

The project aims to provide a simple and user-friendly mobile shopping experience. Users can create accounts, browse clothing products, add items to their shopping bags, and place demonstration orders.

The application is developed using Kotlin and Jetpack Compose in Android Studio.

## Technologies Used

- **Programming Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Development Environment:** Android Studio
- **Architecture:** MVVM (Model-View-ViewModel) with Repository
- **Data Storage:** Local device storage
- **Version Control:** Git and GitHub

## Main Features

### 1. Splash Screen and Welcome Screen
- Displays Meena Bazar branding when the application starts.
- Provides navigation to registration, login, and guest browsing.

### 2. User Registration
- Users can enter their full name, email address, and password.
- Password confirmation is required.
- Form validation displays errors for incorrect inputs.
- Duplicate email registration is prevented.

### 3. User Login
- Registered users can log in with their credentials.
- Passwords are stored as salted hashes rather than plain text.
- User sessions are saved locally.
- Users can log out from the account section.

### 4. Product Catalogue
- Displays eight sample clothing products.
- Users can browse, search, and filter products by category.
- Each product has a details screen.
- Users can choose available clothing sizes.

### 5. Shopping Bag
- Users can add products to a shopping bag.
- Product quantities can be adjusted.
- The application calculates the total price.
- Guest shopping bags can be transferred to registered accounts.

### 6. Checkout
- Users provide delivery information.
- Input validation checks the required delivery fields.
- The application calculates product totals and delivery charges.
- Users can place demonstration orders.

### 7. Order History
- Users can view their previously placed demonstration orders.
- Orders are associated with the relevant customer.
- Order information is saved locally.

### 8. Additional Features
- Light and dark themes.
- Back navigation support.
- Persistent local data storage.
- Responsive scrolling and keyboard handling.

## Project Architecture

The application follows an MVVM-style architecture with separate components for the user interface, application state, business logic, and local persistence.

Main components include:

- `MainActivity.kt` – Application startup and navigation.
- `AuthScreens.kt` – Login and registration screens.
- `AuthModels.kt` – Authentication validation.
- `ShopScreens.kt` – Product browsing, shopping bag, checkout, and orders.
- `ShopModels.kt` – Catalogue and pricing logic.
- `BazarViewModel.kt` – Application state management.
- `BazarRepository.kt` – Accounts, bags, and order operations.
- `LocalStorePersistence.kt` – Local data persistence.

## How to Run the Project

1. Download or clone the GitHub repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronize the project dependencies.
4. Configure an Android emulator or connect an Android device.
5. Run the `app` configuration.
6. Explore the application features.

**GitHub Repository:**

https://github.com/RIAZ104998AHMED/Meena-bazar

## Testing

The project documentation reports:

- 25 successful JVM tests.
- 5 successful Android device tests.
- Successful APK build.
- No Android lint errors.

Testing covers authentication validation, shopping bag operations, account isolation, order persistence, and navigation.

## Current Project Status

The main shopping demonstration features are implemented.

The application currently works as an offline prototype. User accounts, shopping bags, and order history are stored locally on the device.

It does not currently support real online payments, a backend server, or live inventory management.

## Future Improvements

Possible future improvements include:

- Backend API and online database integration.
- Secure online payment processing.
- Real-time inventory management.
- Password recovery and email verification.
- Order tracking and delivery integration.
- Additional product categories.

## Conclusion

Meena Bazar demonstrates the practical application of Android development, UI design, input validation, state management, local persistence, and software engineering principles.

The project helped me gain experience in developing a functional Android shopping application and managing its source code using Git and GitHub.

## Developer

**GitHub:** RIAZ104998AHMED

**Course:** Introduction to Software Engineering

**Project Name:** Meena Bazar
