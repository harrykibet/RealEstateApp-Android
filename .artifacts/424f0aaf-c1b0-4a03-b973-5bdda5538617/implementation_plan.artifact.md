# Implementation Plan - Replace NetworkMonitor with INetworkStateProvider

This plan addresses the consolidation of network monitoring logic by replacing the legacy `NetworkMonitor` (in `core:data`) with the more modern and granular `INetworkStateProvider` (in `core:network`) across the `app` module and its state management.

## User Review Required

> [!NOTE]
> `INetworkStateProvider` provides more detailed states (`Connected`, `PoorConnection`, `NoInternet`) compared to the simple boolean `isOnline` from `NetworkMonitor`. For the `isOffline` check in the UI state, I will map `NoInternet` to `true` and other states to `false`.

## Proposed Changes

### Core Data Module

#### [DELETE] [NetworkMonitor.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/util/NetworkMonitor.kt)
- Remove the legacy interface.

#### [DELETE] [ConnectivityManagerNetworkMonitor.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/util/ConnectivityManagerNetworkMonitor.kt)
- Remove the implementation.

#### [MODIFY] [DataModule.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/di/DataModule.kt)
- Remove the `@Binds` method for `NetworkMonitor`.
- Remove unused imports.

### App Module

#### [MODIFY] [EstatiaAppState.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/java/com/estatia/realestate/apps/ui/EstatiaAppState.kt)
- Replace `NetworkMonitor` dependency with `INetworkStateProvider`.
- Update `isOffline` logic to use `networkStateProvider.observe()` and map `NetworkState.NoInternet` to `true`.
- Update `rememberEstatiaAppState` and the class constructor.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/java/com/estatia/realestate/apps/MainActivity.kt)
- Replace `@Inject lateinit var networkMonitor: NetworkMonitor` with `@Inject lateinit var networkStateProvider: INetworkStateProvider`.
- Pass `networkStateProvider` to `rememberEstatiaAppState`.

## Verification Plan

### Automated Tests
- Run `analyze_file` on all modified files to ensure all compilation errors are resolved.
- Verify Hilt dependencies by ensuring the `app` module correctly consumes the provider from `core:network`.

### Manual Verification
- None required as these are architectural alignment fixes.
