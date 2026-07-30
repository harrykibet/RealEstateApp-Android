# Migration of Encrypted Local Storage to Jetpack DataStore

The `EncryptedSharedPreferences` and `MasterKey` APIs from the `androidx.security:security-crypto` library are now deprecated in modern Android development (as of July 2026). The recommended replacement is to use **Jetpack DataStore** combined with the **Android Keystore** or **Tink** for encryption.

This plan outlines the migration of the authentication token storage from the deprecated `EncryptedSharedPreferences` to a secure `DataStore` implementation in the `:core:security` module.

## User Review Required

> [!IMPORTANT]
> This change involves a major architectural shift for local sensitive data storage. While it resolves all deprecation warnings, it changes how tokens are stored and accessed.
> - **Migration**: A one-time migration will be implemented to move existing tokens from `EncryptedSharedPreferences` to the new `DataStore`.
> - **Dependency Change**: Adds `androidx.datastore:datastore-preferences` to the project.
> - **Encryption**: We will use the existing `IAesGcmCryptoEngine` (which utilizes the Android Keystore) to encrypt tokens before they are persisted in `DataStore`.

## Proposed Changes

### [Component] Dependency Management

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/gradle/libs.versions.toml)
- Add `androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "androidxDataStore" }`.

#### [MODIFY] [build.gradle.kts (:core:security)](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/security/build.gradle.kts)
- Add `implementation(libs.androidx.datastore.preferences)`.

### [Component] Security & Data Persistence

#### [MODIFY] [SecurityModule.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/security/src/main/java/com/estatia/realestate/apps/core/security/di/SecurityModule.kt)
- Remove the deprecated `provideEncryptedSharedPreferences` function.
- Add a new `@Provides` function to initialize and provide a `DataStore<Preferences>` for secure token storage, including migration logic from the old `EncryptedSharedPreferences`.
- Suppress "unused" warnings for `@Binds` methods that are identified as unused by the IDE (standard practice for Dagger modules).

#### [MODIFY] [TokenLocalDataSource.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/security/src/main/java/com/estatia/realestate/apps/core/security/TokenLocalDataSource.kt)
- Update the constructor to accept `DataStore<Preferences>` and `IAesGcmCryptoEngine`.
- Refactor `saveToken`, `getToken`, and `clearToken` to use `DataStore` and encrypt/decrypt data using `IAesGcmCryptoEngine`.
- Token data will be stored as a Base64-encoded string containing both the Initialization Vector (IV) and the ciphertext.

## Verification Plan

### Automated Tests
- Run `./gradlew :core:security:assembleDebug` to ensure it builds correctly.
- Verify that `analyze_file` no longer reports deprecation warnings for `SecurityModule.kt`.

### Manual Verification
- Verify that authentication tokens are correctly persisted and retrieved during login/logout flows.
- Verify that existing tokens are correctly migrated upon first app launch after this change.
