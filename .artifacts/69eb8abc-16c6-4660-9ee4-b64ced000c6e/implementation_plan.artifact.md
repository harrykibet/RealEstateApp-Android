# TikTok-Style Interaction: Swipe for Details & Bottom Sheet Comments

This plan enhances the Home feed interactions by adding a horizontal swipe gesture to view property details and transforming the comments section into a modern TikTok-style bottom sheet.

## User Review Required

> [!IMPORTANT]
> - **Horizontal Swipe**: I will implement a custom swipe-to-navigate gesture on each property video. Swiping right-to-left will transition the user to the full Property Details screen.
> - **Comments Bottom Sheet**: Clicking the comment icon will no longer navigate to a new screen. Instead, it will open a half-screen `ModalBottomSheet` overlaying the video, allowing users to comment without losing their place in the feed.
> - **Property Details**: A new dedicated details screen will be created to show all images/videos and comprehensive listing information.

## Proposed Changes

### [Component] Comments Feature

#### [MODIFY] [CommentsScreen.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/comments/src/main/java/com/estatia/realestate/apps/feature/comments/ui/screens/CommentsScreen.kt)
- Extract the comment list and input area into a reusable `CommentSheetContent` composable.
- Keep `CommentsScreen` as a full-page wrapper for when users navigate directly (e.g., from deep links).

### [Component] Core UI Refactoring

#### [MODIFY] [PropertyFeedScreen.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/ui/src/main/java/com/estatia/realestate/apps/core/ui/screens/PropertyFeedScreen.kt)
- Add state management for the comments bottom sheet (`sheetState`, `showSheet`).
- Integrated `ModalBottomSheet` displaying `CommentSheetContent`.
- Update the `onCommentClick` callback to toggle the sheet visibility.

#### [MODIFY] [PropertyFeedItem.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/ui/src/main/java/com/estatia/realestate/apps/core/ui/screens/PropertyFeedItem.kt)
- Add horizontal drag detection using `Modifier.pointerInput`.
- Trigger `onClick(listing)` (which navigates to details) when a significant horizontal swipe is detected.

### [Component] Property Details Feature

#### [MODIFY] [PropertyDetailsViewModel.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/property/src/main/java/com/estatia/realestate/apps/feature/property/ui/management/viewmodels/PropertyDetailsViewModel.kt)
- Implement state to hold fetched property details.
- Fetch property data using `IPropertyRepository.getPropertyById(id)`.

#### [NEW] [PropertyDetailsScreen.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/property/src/main/java/com/estatia/realestate/apps/feature/property/ui/screens/PropertyDetailsScreen.kt)
- Create a full-page screen featuring:
    - A top back button.
    - A horizontal media pager (images and videos).
    - Detailed sections for Title, Price, Location, and Description.
    - Dynamic listing of amenities.

### [Component] Home Feature Integration

#### [MODIFY] [HomeNavigation.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/home/src/main/java/com/estatia/realestate/apps/feature/home/navigation/HomeNavigation.kt)
- Hook up `PropertyDetailRoute` to the new `PropertyDetailsScreen`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify.

### Manual Verification
- **Feed Swipe**: Swipe right on a video and verify it navigates to the property details.
- **Comment Sheet**: Click the comment icon and verify the bottom sheet appears over the video.
- **Details View**: Verify all media (images/videos) and text details are correctly displayed in the details screen.
