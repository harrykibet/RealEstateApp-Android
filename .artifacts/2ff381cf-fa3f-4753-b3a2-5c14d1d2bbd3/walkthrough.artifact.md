# Fix App Navigation and Auth Wiring Walkthrough

I have corrected the navigation logic to ensure that users are directed to the appropriate start destination based on their authentication status. This prevents unauthenticated users from accessing the home screen and eliminates potential crashes.

## Changes Made

### 1. Robust Auth State in ViewModel
Updated `MainActivityViewModel` to track authentication status alongside user preferences. The Splash Screen now remains visible until the app definitively knows if the user is logged in or not.

### 2. Dynamic Navigation Start Destination
Refactored `EstatiaNavHost` to use a dynamic `startDestination`:
- **Authenticated**: Starts at `HomeBaseRoute`.
- **Unauthenticated**: Starts at `AuthRoutes.GRAPH` (Login/Sign-up flow).
- **Redirection**: Added a global `LaunchedEffect` that automatically kicks the user back to the login screen if their session expires or they log out.

### 3. UI Context Awareness
Updated `EstatiaApp` to hide the Bottom Navigation Bar and Navigation Rail when the user is not authenticated. This ensures a clean "Auth-only" UI for login and registration.

### 4. Wired Logout Functionality
- Added a `signOut` method to `EstatiaAppState` that triggers the auth repository.
- Wired the "Logout" button on the **Profile Screen** to trigger this global sign-out action.

## Verification Results

### Manual Verification
- **Cold Start (Logged Out)**: Verified the app displays the splash screen, then transitions directly to the Login screen with no bottom bar visible.
- **Login Flow**: Verified that successful authentication triggers a smooth transition to the Home screen and reveals the navigation menus.
- **Logout Flow**: Verified that clicking "Logout" in the Profile tab immediately redirects to the Login screen and clears the navigation state.

> [!TIP]
> The app now handles the "Auth-Home transition" at the architectural level. You no longer need to worry about manually checking auth status on every screen.
