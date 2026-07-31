# Tasks: TikTok-Style Interactions Implementation

- [x] Comments Feature Refactoring
    - [x] Extract `CommentSheetContent` from `CommentsScreen.kt`
    - [x] Update `CommentsViewModel.kt` to handle property-specific state if needed
- [x] Core UI: Comments Bottom Sheet
    - [x] Integrate `ModalBottomSheet` into `PropertyFeedScreen.kt`
    - [x] Connect comment action icon to toggle bottom sheet
- [x] Core UI: Swipe Interaction
    - [x] Implement horizontal drag detection in `PropertyFeedItem.kt`
    - [x] Trigger navigation to Property Details on swipe
- [x] Property Details Screen
    - [x] Implement `PropertyDetailsViewModel.kt` with property fetching logic
    - [x] Create `PropertyDetailsScreen.kt` with media gallery and full details
- [x] Navigation Integration
    - [x] Update `HomeNavigation.kt` to link `PropertyDetailRoute` to the new screen
- [x] Verification & Build
