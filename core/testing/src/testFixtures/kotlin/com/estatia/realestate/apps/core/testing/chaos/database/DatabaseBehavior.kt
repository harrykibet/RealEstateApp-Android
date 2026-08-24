package com.estatia.realestate.apps.core.testing.chaos.database

/**
 * Represents the behavior of a database operation in a chaos scenario.
 */
sealed interface DatabaseBehavior {
    data object Success : DatabaseBehavior
    data object Locked : DatabaseBehavior
    data object ConstraintViolation : DatabaseBehavior
    data object Corrupted : DatabaseBehavior
    data object DiskFull : DatabaseBehavior
}
