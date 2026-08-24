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
    data object Unavailable : DatabaseBehavior
    data object MigrationFailure : DatabaseBehavior
    data object SchemaMismatch : DatabaseBehavior
    data object DuplicateData : DatabaseBehavior
    data object ConcurrentWrites : DatabaseBehavior
    data object TransactionFailure : DatabaseBehavior
    data object PartialTransaction : DatabaseBehavior
    data object ProcessDeathDuringTransaction : DatabaseBehavior
    data object VeryLargeDataset : DatabaseBehavior
    data object EmptyDataset : DatabaseBehavior
}
