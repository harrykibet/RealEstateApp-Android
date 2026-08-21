# core:datastore-proto

## Overview
The `datastore-proto` module is a dedicated library for defining and generating Protocol Buffer classes used by the `:core:datastore` module. Separating the definitions allows other modules to depend on the data structures without bringing in the full storage implementation.

## Proto Definitions
- `user_preferences.proto`: Stores the user's primary app settings.
- `theme_brand.proto`: Handles brand-specific styling configurations.
- `dark_theme_config.proto`: Manages dark mode and system theme preferences.

## Usage
Add this module as a dependency to access the generated Java/Kotlin classes for these proto definitions.

## Dependency Graph
![Module Graph](module_graph.png)
