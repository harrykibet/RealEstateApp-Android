# Implementation Plan - Fix Repositories after AppResult Refactoring

This plan addresses compilation errors in `CommentsRepository`, `PropertyRepository`, `UserRepository`, and `SearchRepository` caused by recent refactorings in the network and database layers that enforced the use of `AppResult` as a return type and removed callback-based error handling.

## Proposed Changes

### Core Data Module

#### [NEW] [translateUserFailures.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/util/translateUserFailures.kt)
#### [NEW] [translateSearchFailures.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/util/translateSearchFailures.kt)
- Create utility functions similar to `translatePropertyFailures` to handle user and search related infrastructure exceptions.

#### [MODIFY] [IPropertyRepository.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/interfaces/IPropertyRepository.kt)
- Update `deleteDraft` and `clearAllDrafts` method signatures to return `AppResult<Unit>`.

#### [MODIFY] [ISearchRepository.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/interfaces/ISearchRepository.kt)
- Standardize all methods to return `AppResult`.
- Remove `onFailure` callback from `searchProperties`.
- Rename `loadNearbyProperties` to `getNearbyProperties` and update its parameters to match the remote data source (`radiusKm`, `latitude`, `longitude`).

#### [MODIFY] [CommentsRepository.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/repositories/CommentsRepository.kt)
- Update `submitComment` to handle the `AppResult` from `IUserRepository.getUserById` and use `translateCommentFailures` on the result from the remote data source.

#### [MODIFY] [PropertyRepository.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/repositories/PropertyRepository.kt)
- Refactor `saveDraft`, `getAllDrafts`, and `getDraftById` to properly handle the `AppResult` returned by `IPropertyLocalDataSource`.
- Update `deleteDraft` and `clearAllDrafts` to return `AppResult<Unit>`.

#### [MODIFY] [UserRepository.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/repositories/UserRepository.kt)
- Refactor `getUserById` to handle the `AppResult<UserEntityModel>` returned by `IUserRemoteDataSource` and map it to `AppResult<UserDomainModel>` using `translateUserFailures`.

#### [MODIFY] [SearchRepository.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/repositories/SearchRepository.kt)
- Refactor all methods to correctly handle `AppResult` from both local and remote data sources.
- Implement the mapping from `PropertyEntityModel` to `PropertyDomainModel`.
- Use `translateSearchFailures` for consistent error handling.

## Verification Plan

### Automated Tests
- Run `analyze_file` on all modified files to ensure all compilation errors are resolved.
- Build the project to verify that no downstream dependencies are broken by the interface changes.

### Manual Verification
- None required as these are primarily compilation and architectural alignment fixes.
