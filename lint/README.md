# lint

## Overview
The `lint` module contains custom Android Lint rules designed to enforce architectural patterns, coding standards, and best practices across the Estatia codebase.

## Custom Rules
- **DesignSystemDetector**: Ensures that feature modules use the centralized Design System components (e.g., `EstatiaText`, `EstatiaButton`) rather than raw Material 3 or Foundation components directly.
- **ModulePackageDetector**: Enforces module boundaries by checking that classes are kept within their correct package structures, preventing accidental leakage of internal details between features.

## Integration
These rules are automatically applied to all modules through the `LintConventionPlugin`. Results are reported during the build process and can be found in the `build/reports/lint-results.html` file of each module.

## Development
To add a new rule:
1. Create a new `Detector` class in `src/main/java`.
2. Register the issue in `EstatiaIssueRegistry`.
3. Add a corresponding test in `src/test/java` to verify the rule.
