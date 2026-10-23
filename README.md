# Smart Pantry Manager

## Application Overview
**Smart Pantry Manager** is a fully functional Java Android application designed to help users cut food waste by tracking ingredients currently available at home (their "pantry") and suggesting recipes they can cook using **strictly** those leftover ingredients—no shopping trip required, and no recipe suggested unless the user genuinely has every required ingredient in sufficient quantity.

## Core Features
1. **Pantry Management (CRUD)**: Add, edit, and delete pantry items (ingredient name, quantity, unit, and optional expiry date). Data persists locally across app restarts using SQLite.
2. **Pantry List Screen**: Dynamic `RecyclerView` displaying current pantry ingredients with edit and delete actions.
3. **Pre-Loaded Recipe Collection**: 16 diverse recipes pre-seeded in the SQLite database on first installation, each with required ingredient quantities and step-by-step preparation instructions.
4. **Strict-Matching Suggested Recipes**: Evaluates the user's pantry against recipe requirements. Only recipes where every required ingredient is present in sufficient quantity appear in suggestions. Zero-match fallback messaging is provided when no recipes match.
5. **Recipe Detail Screen**: Full breakdown of required ingredients, quantities, and cooking methods.
6. **Settings & Preferences**: Expiring-soon alerts toggle, theme preference, and app metadata.

## Database Implementation
- **SQLite Database** (`SQLiteOpenHelper`): Chosen for robust, reliable on-device persistence, zero external cloud dependencies, fast local query execution, and seamless integration with Android's Activity lifecycle.

## Setup and Run Instructions
1. Clone or download the repository.
2. Open the project in **Android Studio** (Koala / Jellyfish or newer).
3. Ensure Gradle sync completes successfully.
4. Run the app on an Android Emulator (API 34+) or physical Android device.
5. Add items to your pantry (e.g., `egg`, `bread`, `butter`, `pasta`, `tomato sauce`, `garlic`), then tap **View Suggested Recipes** to test the strict-matching engine.
