# Firebase Decoupling Refactor Plan (MVVM + Clean Architecture)

## Goal
Remove Firebase types and callback mechanics from ViewModels and data interfaces. Keep Firebase confined to `core/network`, expose only domain models and clean repository contracts to the feature layer.

## Scope
- ViewModels currently coupled to Firebase: `LoginViewModel`, `SignUpViewModel`, `PhoneVerificationViewModel`.
- Repository interfaces that leak Firebase types: `IAuthRepository` and `IAuthRemoteDataSource`.
- Data layer adapters: `AuthRepository` and any auth remote data source implementation.

## Guiding Rules
- Feature/ViewModel layer uses only domain models and repo interfaces.
- Data layer maps network DTOs/Firebase models into domain models.
- Network module is the only module allowed to import Firebase SDK types.
- Use suspend functions and `Result`/`Flow` instead of Firebase `Task` callbacks.

## Proposed Domain Models
Create in `core/model` (or `core/domain`) as small data classes/sealed types:
- `AuthUser` (id, email, phone, displayName, photoUrl, isEmailVerified, userType)
- `AuthSession` (user: AuthUser, isAuthenticated: Boolean)
- `AuthState` (sealed: Unauthenticated, EmailVerificationRequired, PhoneVerificationRequired, Authenticated)
- `PhoneVerificationState` (sealed: Idle, CodeSent(verificationId), Verifying, Verified, Error(message), Expired)
- Optional value wrappers: `VerificationId`, `ResendToken` (if you want stronger typing)

## Step-by-Step Plan

### 1) Define Clean Interfaces (Data Layer)
Update `IAuthRepository` to remove Firebase types:
- Replace `Task<AuthResult>` returns with `suspend fun` returning `Result<AuthSession>` or `Result<AuthUser>`.
- Replace `FirebaseUser` return with `AuthUser?`.
- Replace `PhoneAuthCredential` and `PhoneAuthProvider.ForceResendingToken` with domain types.
- Add `fun authState(): Flow<AuthState>` or expose auth state transitions via use cases.

Update `IAuthRemoteDataSource` similarly (domain types only).

### 2) Implement Data Mappers
Add mappers in data module (or network module if you keep Firebase models there):
- `FirebaseUser -> AuthUser`
- `AuthResult -> AuthSession`
- `PhoneAuth` callbacks -> `Flow<PhoneVerificationState>`

### 3) Refactor AuthRepository
Update `AuthRepository` to:
- Use the new `IAuthRemoteDataSource` contract.
- Map remote Firebase models to domain models.
- Convert callback/task APIs into `suspend`/`Flow`.

### 4) Refactor Remote Data Source (Network)
Confine Firebase SDK usage here:
- Implement `IAuthRemoteDataSource` with Firebase types internally.
- Convert `Task<AuthResult>` to `suspend` (use `kotlinx-coroutines-play-services`).
- Wrap phone verification callbacks into a `callbackFlow` producing `PhoneVerificationState`.

### 5) Update ViewModels (Feature Layer)
- `LoginViewModel`:
  - Use `Result<AuthSession>` and `AuthState` only.
  - Remove `FirebaseUser` and `Task` usage.
- `SignUpViewModel`:
  - Remove direct dependency on `AuthResult`.
  - Receive `AuthUser` or `AuthSession` from repo.
- `PhoneVerificationViewModel`:
  - Remove `PhoneAuthProvider`, `PhoneAuthCredential`, `FirebaseException`.
  - Observe `Flow<PhoneVerificationState>` from repository/use case.
  - Do not store `Activity`; pass it into the repository call if required by Firebase APIs.

### 6) Update DI Bindings
- Ensure all ViewModels inject `IAuthRepository` not `AuthRepository`.
- Bind the updated repository and remote data source implementations in Hilt modules.

### 7) Clean Up Imports and Build
- Remove Firebase imports from ViewModels and data interfaces.
- Confirm Firebase imports only exist in `core/network`.
- Run compilation checks (or at least Gradle sync).

### 8) Optional: Add Tests
- Unit test auth mappers.
- Fake `IAuthRepository` for ViewModel tests.
- Validate phone verification flow emits correct `PhoneVerificationState` sequence.

## Notes
- This plan intentionally keeps the UI-facing state in ViewModels, but all data/source types are domain types.
- If you prefer UseCases, insert them between ViewModels and repositories (same repository contract still applies).

