# Core Testing Player

This module provides testing utilities specifically for media player functionality, including mocks for Media3/ExoPlayer and related engine logic.

## Why was this split from core:testing?

Splitting the player testing utilities into a dedicated module provides several benefits:

1.  **Domain Isolation**: Media player testing requires heavy dependencies (Media3, ExoPlayer mocks). Most feature modules do not need these and should not have them in their classpath.
2.  **Faster Builds**: By decoupling player-specific test infrastructure, we reduce the complexity of the main `core:testing` module and speed up builds for non-media features.
3.  **Explicit Opt-in**: Developers must explicitly depend on this module to access player mocks, making the architecture more intentional.
