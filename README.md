# Product Manager – Android App

A simple Android application for managing products and tracking inventory. Product data is stored in Firebase Cloud Firestore, so it remains available even after closing and reopening the application.

## Features

- Add, edit and delete products, with a confirmation dialog before deletion.
- Each product has a name, cost and quantity (between 10 and 100).
- Automatically calculates the total price (`cost × quantity`).
- Sort products by name or price in ascending or descending order.
- Displays a summary of the total number of products and their combined value.
- Validates user input and displays relevant error messages.
- Handles loading, empty and error states.
- Add, update and delete operations require an internet connection and have a 10-second timeout.
- Previously loaded product data remains visible when the device is offline.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM with a layered architecture
- **Dependency Injection:** Hilt
- **Asynchronous Operations:** Kotlin Coroutines and StateFlow
- **Backend:** Firebase Cloud Firestore

## Project Structure

```text
com.practice.trialapp
├── ProductManagementApp.kt
├── MainActivity.kt
├── di/
│   ├── FirebaseModule.kt
│   ├── RepositoryModule.kt
│   └── DispatcherModule.kt
├── data/
│   ├── remote/
│   │   └── FirestoreDataSource.kt
│   ├── mapper/
│   │   └── ProductMapper.kt
│   └── repository/
│       └── ProductRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   └── Product.kt
│   └── repository/
│       └── ProductRepository.kt
├── presentation/
│   └── product/
│       ├── ProductListScreen.kt
│       ├── ProductListViewModel.kt
│       ├── ProductUiState.kt
│       └── components/
│           ├── ProductItem.kt
│           ├── AddEditProductDialog.kt
│           ├── ProductSummary.kt
│           └── SortDropdown.kt
└── ui/
    └── theme/
```

## Architecture

The application follows a layered architecture that separates UI components, business logic and data operations.

Data flows through the following layers:

**Firestore → DataSource → Repository → ViewModel (StateFlow) → Compose UI**

- **Presentation:** Jetpack Compose screens and ViewModels manage the UI and its state.
- **Domain:** Contains the Product model and repository interface.
- **Data:** Handles Firestore operations, data mapping and repository implementation.
- **DI:** Hilt provides dependencies throughout the application.

## Firebase Firestore

Product information is stored in a Firestore collection named `products`.

Each product is stored as a separate document with an automatically generated document ID.

| Field | Type |
|---|---|
| `name` | String |
| `cost` | Number |
| `quantity` | Number |

The total price is calculated within the application (`cost × quantity`) and is not stored in Firestore.

## Getting Started

### Prerequisites

- Android Studio
- JDK 17
- Android device or emulator
- Internet connection

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Muskan125/Product-Management-App.git
   ```

2. Open the project in Android Studio.
3. Wait for Gradle sync to complete.
4. The repository includes the Firebase configuration file (`google-services.json`) inside the `app/` directory.
5. The application is configured to use the associated Firebase project and its Cloud Firestore database.
6. Run the application on an Android device or emulator.

**Package name:** `com.practice.trialapp`

**Minimum SDK:** 30


