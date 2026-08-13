package com.estatia.realestate.apps.core.model.user

/**
 * Represents the trust status of a user within the Estatia ecosystem.
 */
enum class VerificationLevel {
    /**
     * Initial state for new users. No documentation provided.
     */
    NONE,

    /**
     * User has provided a valid Government ID that matches their profile.
     */
    IDENTITY_VERIFIED,

    /**
     * User is a verified landlord or agent who has successfully verified
     * ownership or legal right to list at least one physical asset.
     */
    TRUSTED_PARTNER
}
