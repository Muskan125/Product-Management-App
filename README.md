# Product Manager

A simple Android app to manage a list of products. Products are stored in Firebase Firestore, so the data stays after the app is closed.

## Features

- Add, edit and delete products (delete asks for confirmation)
- Each product has a name, cost and quantity (10 – 100); the total price is calculated automatically
- Sort by name or price, ascending or descending
- Summary at the bottom with total products and total price
- Input validation with error messages
- Loading, empty and error states
- Add / update / delete only work when the phone is online (10 second timeout); the list still shows the last loaded data when offline

## Tech stack

- Kotlin
- Jetpack Compose (Material 3)
- Hilt for dependency injection
- Coroutines + StateFlow
- Firebase Firestore

## Project structure

```
com.practice.trialapp
├── ProductManagementApp.kt        // Hilt application class
├── MainActivity.kt
├── di/
│   ├── FirebaseModule.kt          // provides Firestore
│   ├── RepositoryModule.kt        // binds the repository
│   └── DispatcherModule.kt        // IO / Default dispatchers
├── data/
│   ├── remote/FirestoreDataSource.kt
│   ├── mapper/ProductMapper.kt
│   └── repository/ProductRepositoryImpl.kt
├── domain/
│   ├── model/Product.kt
│   └── repository/ProductRepository.kt
├── presentation/product/
│   ├── ProductListScreen.kt
│   ├── ProductListViewModel.kt
│   ├── ProductUiState.kt
│   └── components/
│       ├── ProductItem.kt
│       ├── AddEditProductDialog.kt
│       ├── ProductSummary.kt
│       └── SortDropdown.kt
└── ui/theme/                      // colors, shapes, typography
```

Data flows like this: **Firestore → DataSource → Repository → ViewModel (StateFlow) → Compose UI**.
Firestore calls, mapping and sorting run on background threads; only the UI runs on the main thread.

## Firestore

Products are saved in the `products` collection with auto-generated document IDs:

| Field      | Type   |
|------------|--------|
| `name`     | string |
| `cost`     | number |
| `quantity` | number |

The total price is not stored, it is calculated in the app (`cost × quantity`).

## Setup

1. Clone the project and open it in Android Studio.
2. Create a Firebase project and add an Android app with package name `com.practice.trialapp`.
3. Download `google-services.json` and put it in the `app/` folder.
4. Enable **Cloud Firestore** in the Firebase console.
5. Run the app on a device or emulator (min SDK 30).

Build from the command line:

```
./gradlew assembleDebug
```
