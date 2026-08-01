package com.estatia.realestate.apps.core.common.exceptions

/**
 * Marks an exception as carrying infrastructure-specific detail (SDK/backend
 * vocabulary) that MUST be translated into a feature-domain exception before
 * crossing the repository boundary — never surface these to a ViewModel directly.
 *
 * Exceptions that do NOT implement this (AuthException, NetworkException) are
 * considered terminal: they already encode cross-cutting, backend-agnostic
 * semantics (session/transport state) and are safe for direct ViewModel
 * consumption as-is.
 */
sealed interface InfrastructureException
