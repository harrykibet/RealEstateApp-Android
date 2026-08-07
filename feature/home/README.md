# feature:home

The `home` module implements the landing experience of the Estatia app, featuring a high-performance video feed of property listings.

## Key Features

- **Property Feed**: A vertical scroll of immersive video listings.
- **Integration**: Leverages `core:player-ui` and `core:player-engine` for optimized video playback.
- **Interactions**: Supports liking, commenting (via bottom sheet), and navigating to property details.

## Screen Structure

- `HomeRoute`: The entry point that ties together the `HomeViewModel` and `HomeVideoPlaybackViewModel`.
- `HomeScreen`: Top-level Composable for state handling (Loading, Error, Empty, Success).
- `HomeFeedContent`: Orchestrates the `PropertyFeedScreen` with specific property item content.
