# Refactor Feature Navigation: Separate Routes and Navigation

This plan aims to standardize the navigation package structure across all feature modules to follow the `:feature:auth` pattern. This involves splitting merged `*Navigation.kt` files into separate `*Navigation.kt` (graph and functions) and `*Routes.kt` (serializable route objects) files.

## User Review Required

> [!NOTE]
> This is a structural refactoring that does not change navigation logic. It improves code organization and consistency.

## Proposed Changes

### [Component] Navigation Standardization

#### [MODIFY] [CommentsNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/comments/src/main/java/com/estatia/realestate/apps/feature/comments/navigation/CommentsNavigation.kt) & [NEW] [CommentsRoutes.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/comments/src/main/java/com/estatia/realestate/apps/feature/comments/navigation/CommentsRoutes.kt)
- Rename `CommentRoutes.kt` to `CommentsRoutes.kt` for consistency.
- Ensure all `@Serializable` routes are in `CommentsRoutes.kt`.

#### [MODIFY] [HomeNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/home/src/main/java/com/estatia/realestate/apps/feature/home/navigation/HomeNavigation.kt) & [NEW] [HomeRoutes.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/home/src/main/java/com/estatia/realestate/apps/feature/home/navigation/HomeRoutes.kt)
- Move `HomeRoute`, `HomeBaseRoute`, and `PropertyDetailRoute` to `HomeRoutes.kt`.
- Clean up `HomeNavigation.kt`.

#### [MODIFY] [FavoritesNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/favorites/src/main/java/com/estatia/realestate/apps/feature/favorites/navigation/FavoritesNavigation.kt) & [NEW] [FavoritesRoutes.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/favorites/src/main/java/com/estatia/realestate/apps/feature/favorites/navigation/FavoritesRoutes.kt)
- Move `FavoritesRoute` and `FavoritesBaseRoute` to `FavoritesRoutes.kt`.
- Clean up `FavoritesNavigation.kt`.

#### [MODIFY] [ProfileNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/profile/src/main/java/com/estatia/realestate/apps/feature/profile/navigation/ProfileNavigation.kt) & [NEW] [ProfileRoutes.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/profile/src/main/java/com/estatia/realestate/apps/feature/profile/navigation/ProfileRoutes.kt)
- Move `ProfileRoute` and `ProfileBaseRoute` to `ProfileRoutes.kt`.
- Clean up `ProfileNavigation.kt`.

#### [MODIFY] [PropertyNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/property/src/main/java/com/estatia/realestate/apps/feature/property/navigation/PropertyNavigation.kt) & [NEW] [PropertyRoutes.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/property/src/main/java/com/estatia/realestate/apps/feature/property/navigation/PropertyRoutes.kt)
- Move `PropertyRoute` and `PropertyBaseRoute` to `PropertyRoutes.kt`.
- Clean up `PropertyNavigation.kt`.

#### [MODIFY] [SearchNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/search/src/main/java/com/estatia/realestate/apps/feature/search/navigation/SearchNavigation.kt) & [NEW] [SearchRoutes.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/search/src/main/java/com/estatia/realestate/apps/feature/search/navigation/SearchRoutes.kt)
- Move `SearchRoute` and `SearchBaseRoute` to `SearchRoutes.kt`.
- Clean up `SearchNavigation.kt`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure no broken references or missing imports.
- Use `analyze_file` to verify that the split files are correct.

### Manual Verification
- Verify that navigation still works correctly in the app by navigating between top-level destinations.
