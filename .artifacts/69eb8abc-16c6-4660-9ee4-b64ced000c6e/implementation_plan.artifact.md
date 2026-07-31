# TikTok-Style Overlays for Home Feed

This plan addresses the visual overhaul of the property feed overlays (Info and Actions) to closely match the "TikTok" aesthetic. This involves refining typography, adding creator avatars, improving text contrast, and styling the action buttons.

## User Review Required

> [!IMPORTANT]
> - **Creator Avatar**: I will add a circular creator avatar at the top of the action stack on the right.
> - **Visual Contrast**: I will apply subtle text shadows to ensure titles and descriptions remain legible over bright video content.
> - **Compact Styling**: The action column will be made more compact with refined font weights and icon sizes.

## Proposed Changes

### [Component] Core UI Overlays

#### [MODIFY] [PropertyInfoOverlay.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/ui/src/main/java/com/estatia/realestate/apps/core/ui/screens/PropertyInfoOverlay.kt)
- Add the creator's name with a `@` prefix (using a new `creatorName` field in `ListingUiModel` if possible, otherwise placeholder).
- Apply `shadow` to text for better contrast.
- Refine font weights: Bold for title, medium for description.

#### [MODIFY] [FeedActionsColumn.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/ui/src/main/java/com/estatia/realestate/apps/core/ui/screens/FeedActionsColumn.kt)
- Add a circular avatar at the top of the column.
- Update `FeedActionButton` styling (or create a localized version) to match the TikTok "glow" or minimalist look.
- Use `Inter` or standard bold fonts for counts.

#### [MODIFY] [ListingUiModel.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/model/src/main/java/com/estatia/realestate/apps/core/model/property/ListingUiModel.kt)
- Add `ownerName` (String) and `ownerAvatarUrl` (String?) to the UI model.

### [Component] Home Feature Integration

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/home/src/main/java/com/estatia/realestate/apps/feature/home/ui/screens/HomeScreen.kt)
- Update the mapper to include the owner's information.
- Update previews to use these new fields.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify.

### Manual Verification
- Verify the new overlay layout in the Home feed previews.
- Ensure text is legible on various background colors.
