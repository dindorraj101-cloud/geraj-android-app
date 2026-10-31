# GERAJ Native Android App (Kotlin + Jetpack Compose)

This is the complete, native Android Kotlin & Jetpack Compose codebase for **GERAJ - Professional Vehicle Mechanic Booking**, ready to build as an Android APK.

---

## 🚀 Key Features Ported

1. **Brand Identity & Design:**
   - Deep Black background (`#050608`) with Neon Green (`#00FF66`) accents.
   - High contrast status cards and dark mode elevation.

2. **Vehicle Breakdown Coverage:**
   - 4 Vehicle Categories: **Bike**, **Car**, **Truck**, and **Bus**.
   - Instant issue selection (Puncture repair, Battery Jumpstart, Overheating, Towing, Brake failure).

3. **India Specific Localization:**
   - **Emergency 112 Helpline**: Integrated dialer button calling `tel:112` for police/highway rescue.
   - **Indian Rupee (₹ INR) Pricing**: All service rate sheets, estimates, and dispatches priced in ₹.

4. **Dual Role Architecture:**
   - **Customer Portal**: Select breakdown location, request nearby garages, track ETA and arrival.
   - **Mechanic & Garage Terminal**: Receive SOS requests, toggle Online/Offline radar, update dispatch status (`pending` → `accepted` → `en_route` → `arrived` → `completed`).

5. **Cloud Persistence (Firebase):**
   - Configured with project `watchful-xenops-ppqwl` via `app/google-services.json`.
   - Native Firebase Auth and Firestore real-time flows.

---

## 🛠️ How to Build the Android APK

### Option A: Via Android Studio (Recommended)
1. Open **Android Studio** (Hedgehog / Iguana / Ladybug or newer).
2. Click **Open** and select the `/android` directory.
3. Allow Gradle to sync dependencies.
4. Click **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
5. Once completed, your debug APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Option B: Via Command Line (Gradle)
Run from the `/android` directory:
```bash
./gradlew assembleDebug
```
For release signed APK:
```bash
./gradlew assembleRelease
```

---

## 📂 Project Architecture

```
android/
├── app/
│   ├── build.gradle.kts           # App-level dependencies (Compose, Material3, Firebase)
│   ├── google-services.json       # Firebase credentials for watchful-xenops-ppqwl
│   └── src/main/
│       ├── AndroidManifest.xml    # Permissions (Internet, Call 112, GPS Location)
│       ├── java/com/geraj/mechanic/
│       │   ├── MainActivity.kt    # Root Compose Activity & Navigation Host
│       │   ├── data/
│       │   │   └── FirebaseRepository.kt # Firebase Auth & Firestore Flow Sync
│       │   ├── model/
│       │   │   └── Models.kt      # VehicleType, UserRole, Mechanic, Booking (₹ INR)
│       │   ├── ui/
│       │   │   ├── navigation/    # Screen navigation routes
│       │   │   ├── screens/       # HomeScreen, FindMechanicsScreen, BookingsScreen, ProfileScreen, AuthScreen
│       │   │   └── theme/         # Color.kt (#00FF66, #050608), Theme.kt, Type.kt
│       └── res/values/            # colors.xml, strings.xml, themes.xml
├── gradle/
│   └── libs.versions.toml         # Version catalog (AGP 8.7, Kotlin 2.0, Compose BoM)
├── build.gradle.kts               # Top-level build config
├── gradle.properties              # JVM and AndroidX settings
└── settings.gradle.kts            # Project include settings
```
