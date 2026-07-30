# Implementation Plan - Fix App Navigation and Auth Wiring

This plan addresses the incorrect navigation start destination logic in the app module and ensures the authentication state is correctly handled during app launch to prevent potential crashes and unauthorized access to the home screen.

## User Review Required

> [!IMPORTANT]
> I am introducing a `Boolean?` state for authentication in `EstatiaAppState` and `MainActivityViewModel`. This allows the app to distinguish between "Loading/Checking Auth" (`null`) and "Definitely Unauthenticated" (`false`).
>
> I will also hide the Bottom Navigation Bar/Navigation Rail when the user is not authenticated.

## Proposed Changes

### [Core: Data]
- No changes needed to `IAuthRepository` as it already provides `isUserAuthenticated(): Flow<Boolean>`.

### [App: ViewModel]

#### [MODIFY] [MainActivityViewModel.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/java/com/estatia/realestate/apps/MainActivityViewModel.kt)
- Inject `IAuthRepository`.
- Combine `userData` and `isUserAuthenticated` into the `uiState`.
- Update `MainActivityUiState` to include `isAuthenticated`.
- This ensures the splash screen stays visible until the auth state is determined.

### [App: UI State]

#### [MODIFY] [EstatiaAppState.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/java/com/estatia/realestate/apps/ui/EstatiaAppState.kt)
- Change `isUserAuthenticated` to `StateFlow<Boolean?>` with `initialValue = null`.
- Update logic to correctly reflect the current authentication status.

### [App: Navigation]

#### [MODIFY] [EstatiaNavHost.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/java/com/estatia/realestate/apps/navigation/EstatiaNavHost.kt)
- Change `isUserAuthenticated` parameter to `Boolean?`.
- Return early if `isUserAuthenticated` is `null`.
- Fix the `startDestination` to use the dynamic variable:
  - `HomeBaseRoute` if authenticated.
  - `AuthRoutes.GRAPH` (the auth navigation graph) if unauthenticated.
- Add a `LaunchedEffect` to handle automatic redirection if the auth state changes (e.g., auto-logout).

#### [MODIFY] [EstatiaApp.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/java/com/estatia/realestate/apps/ui/EstatiaApp.kt)
- Hide the `EstatiaNavigationSuiteScaffold` (bottom bar/rail) if `isUserAuthenticated` is `false`.
- Ensure the app waits for a non-null auth state before rendering the main content.

### [Feature: Profile]

#### [MODIFY] [ProfileNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/profile/src/main/java/com/estatia/realestate/apps/feature/profile/navigation/ProfileNavigation.kt)
- Update `profileGraph` to accept an `onLogoutClick` callback.
- Pass this callback to the `ProfileScreen`.

## Verification Plan

### Automated Tests
- None planned for this architectural change, as it mostly involves wiring.

### Manual Verification
1.  **Fresh Install / Logged Out**: Verify the app launches directly to the Login screen and no bottom bar is visible.
2.  **Login**: Verify that clicking login successfully navigates to the Home screen and the bottom bar appears.
3.  **Auto-Login**: Verify that if a user is already logged in, the app launches directly to the Home screen (after the splash screen).
4.  **Logout**: Verify that triggering a logout (once wired) sends the user back to the Login screen and hides the bottom bar.
