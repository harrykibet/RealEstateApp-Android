# Task - Replace NetworkMonitor with INetworkStateProvider

- [ ] Delete legacy classes in `core:data`
    - [ ] Delete `NetworkMonitor.kt`
    - [ ] Delete `ConnectivityManagerNetworkMonitor.kt`
- [ ] Update `DataModule.kt` (remove bindings)
- [ ] Update `app` module
    - [ ] Refactor `EstatiaAppState.kt` to use `INetworkStateProvider`
    - [ ] Refactor `MainActivity.kt` to inject `INetworkStateProvider`
- [ ] Verify changes with static analysis
