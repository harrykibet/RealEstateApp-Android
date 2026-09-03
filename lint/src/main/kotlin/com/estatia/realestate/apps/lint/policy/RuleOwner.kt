package com.estatia.realestate.apps.lint.policy

/**
 * Identifies the team or individual responsible for maintaining a rule.
 */
enum class RuleOwner(val handle: String) {
    PLATFORM("@estatia/platform-engineers"),
    ARCHITECTURE("@estatia/architects"),
    PRODUCT("@estatia/product-developers"),
    SECURITY("@estatia/security-team")
}
