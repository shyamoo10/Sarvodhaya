# Sarvodhaya Hardware

A complete Android storefront for a hardware and building-supplies shop, built with Kotlin and Jetpack Compose on top of Firebase. Shoppers can browse paints, door locks, cement, steel, plumbing, electrical goods, tools and adhesives, manage a cart and wishlist, check out with cash on delivery and follow their order status.

The app ships with a bundled demo catalog (8 categories, 32 products, 4 banners) so it runs immediately, and syncs to Firestore as soon as Firebase is configured.

## Features

- **Home** - promo banner carousel, category grid, featured rails, popular products, cart badge
- **Search** - instant search across product names, brands and descriptions
- **Category listing** - in-stock filter and sorting by popularity, price and discount
- **Product details** - image, rating, stock, quantity stepper, specifications, delivery info, wishlist
- **Cart and checkout** - live quantity editing, delivery fee rules, address form, cash on delivery / UPI on delivery
- **Orders** - order history with a five-step status timeline (placed, packed, shipped, out for delivery, delivered)
- **Wishlist** - saved products synced per account
- **Accounts** - email and password registration, sign-in, profile editing, sign-out

## Architecture

```
app/src/main/java/com/sarvodhaya/store
  core/ui/               theme, formatting helpers, reusable components
  domain/model           plain Kotlin models (Product, Order, Address...)
  domain/repository      repository contracts
  data/firebase          Firestore + Auth implementations
  data/seed              bundled demo catalog
  di                     AppContainer (manual dependency graph)
  ui                     navigation, view models, screens
```

- Unidirectional data flow: repositories expose `StateFlow`, view models combine and expose UI state, composables render it.
- No DI framework: `AppContainer` builds the graph once in `SarvodhayaApp` and view models are created with a small `viewModelFactory` helper.
- Firebase is optional. `app/build.gradle.kts` applies the `google-services` plugin only when `app/google-services.json` exists, and every Firebase call is guarded so the app still runs in demo mode.

## Getting started

Requirements: Android Studio Ladybug or newer, JDK 17, Android SDK 35.

The Gradle wrapper is committed and pinned to Gradle 8.9, so you can build straight from a checkout:

```bash
./gradlew assembleDebug        # macOS / Linux
 gradlew.bat assembleDebug     # Windows
```

You can also just open the project in Android Studio.

Without Firebase the app launches with the bundled demo catalog and guest-only cart/wishlist.

## Firebase setup

1. Create a project in the [Firebase console](https://console.firebase.google.com).
2. Add an Android app with package name `com.sarvodhaya.store`.
3. Download `google-services.json` and place it at `app/google-services.json` (it is git-ignored).
4. Enable **Authentication -> Email/Password** and create a **Firestore Database**.
5. Rebuild the app. The profile screen will report "Firebase connected".
6. Sign in, open **Profile -> Publish demo catalog to Firebase** to upload all 8 categories, 4 banners and 32 products into Firestore.

### Firestore layout

```
products/{productId}
categories/{categoryId}
banners/{bannerId}
users/{uid}
users/{uid}/cart/{productId}
users/{uid}/wishlist/{productId}
users/{uid}/orders/{orderId}
```

Deploy the security rules in [`firebase/firestore.rules`](firebase/firestore.rules) with the Firebase CLI:

```bash
firebase deploy --only firestore:rules
```

The rules allow public catalog reads, restrict writes to admins (custom claim `admin == true`), and scope cart, wishlist and order documents to their owner.

## Business rules

- Delivery is free for orders of Rs.999 and above, otherwise Rs.49.
- Orders start at `PLACED`; the status timeline drives the tracking UI.
- Prices, MRP and stock live on each product document, so changing them in Firestore updates the app live.

## Notes

- Product photos are optional: add an `imageUrl` to a product document and Coil renders it, otherwise a branded category placeholder is drawn in Compose.
- The launcher icon is a vector drawable; replace it with adaptive icon resources before publishing to the Play Store.
