# Fix Identified App Crashes: Navigation and Places Initialization

This plan addresses two critical crashes identified in recent app runs:
1. `IllegalArgumentException`: Destination with route `FavoritesRoute` cannot be found.
2. `IllegalStateException`: Places must be initialized first.

## User Review Required

> [!IMPORTANT]
> - **Navigation Change**: Standardizing `:feature:favorites` to use a nested navigation graph.
> - **Application Change**: Moving Google Places initialization to the `EstatiaApplication` class to ensure it's available before any UI component attempts to create a `PlacesClient`.

## Proposed Changes

### [Component] App Initialization

#### [MODIFY] [EstatiaApplication.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/java/com/estatia/realestate/apps/EstatiaApplication.kt)
- Inject `PlacesManager`.
- Call `placesManager.initialize(this)` in `onCreate`.

### [Component] Search Feature

#### [MODIFY] [MapWithSearchBar.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/search/src/main/java/com/estatia/realestate/apps/feature/search/ui/screens/MapWithSearchBar.kt)
- Remove the `LaunchedEffect` that was previously initializing Places (now handled in Application).
- Ensure `PlacesClient` is created safely.

### [Component] Favorites Feature

#### [MODIFY] [FavoritesNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/favorites/src/main/java/com/estatia/realestate/apps/feature/favorites/navigation/FavoritesNavigation.kt)
- Wrap the `composable<FavoritesRoute>` in a `navigation<FavoritesBaseRoute>(startDestination = FavoritesRoute)` block. This ensures that the navigation graph structure matches the definitions in `TopLevelDestination`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure all changes are valid.

### Manual Verification
- Launch the app and verify:
    - Navigating to the **Favorites** screen from the bottom bar no longer crashes.
    - Navigating to the **Search** screen (Map) no longer crashes with "Places must be initialized first".
