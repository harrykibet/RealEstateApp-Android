package com.estatia.realestate.apps.core.testing.chaos.models

/**
 * Common failure model for chaos testing across all infrastructure domains.
 * This is used to script predictable failures in adversarial implementations.
 */
sealed interface TestFailure {

    // Network Failures
    data object Offline : TestFailure
    data object Timeout : TestFailure
    data class Http(val code: Int) : TestFailure
    data object ConnectionReset : TestFailure
    data object ConnectionRefused : TestFailure
    data object DnsFailure : TestFailure
    data object MalformedResponse : TestFailure
    data object EmptyResponse : TestFailure
    data object PartialResponse : TestFailure
    data object UnexpectedSchema : TestFailure
    data object ServerSuccessClientTimeout : TestFailure
    data object RateLimited : TestFailure
    data object OversizedResponse : TestFailure
    data object DuplicateResponse : TestFailure
    data object OutOfOrderResponse : TestFailure

    // Auth Failures
    data object Unauthorized : TestFailure
    data object Forbidden : TestFailure
    data object TokenExpired : TestFailure
    data object TokenRevoked : TestFailure
    data object RefreshFailed : TestFailure
    data object RefreshTimeout : TestFailure
    data object MultipleRefreshRequests : TestFailure
    data object LogoutDuringRefresh : TestFailure
    data object LogoutDuringRequest : TestFailure
    data object AccountDisabled : TestFailure
    data object PermissionsRevoked : TestFailure
    data object ProcessDeathDuringAuth : TestFailure
    data object NetworkLostDuringRefresh : TestFailure
    data object SessionRestorationFailure : TestFailure

    // Persistence Failures
    data object DatabaseLocked : TestFailure
    data object DiskFull : TestFailure
    data object PermissionDenied : TestFailure
    data object CorruptedData : TestFailure
    data object ConstraintViolation : TestFailure
    data object DatabaseUnavailable : TestFailure
    data object MigrationFailure : TestFailure
    data object SchemaMismatch : TestFailure
    data object DuplicateData : TestFailure
    data object TransactionFailure : TestFailure
    data object PartialTransaction : TestFailure
    data object ConcurrentWrites : TestFailure
    data object ProcessDeathDuringTransaction : TestFailure
    data object VeryLargeDataset : TestFailure
    data object EmptyDataset : TestFailure

    // File Failures
    data object FileMissing : TestFailure
    data object FileCorrupt : TestFailure
    data object FileDisappearsDuringOp : TestFailure
    data object ZeroByteFile : TestFailure
    data object UnsupportedFormat : TestFailure
    data object WrongMimeType : TestFailure
    data object PartialFile : TestFailure
    data object FileChangesWhileReading : TestFailure
    data object VeryLargeFile : TestFailure
    data object IoFailure : TestFailure

    // Concurrency Failures
    data object DuplicateOperation : TestFailure
    data object ConcurrentOperation : TestFailure
    data object OutOfOrderCompletion : TestFailure
    data object StaleResult : TestFailure
    data object CancellationRace : TestFailure
    data object CallbackRace : TestFailure
    data object DoubleRelease : TestFailure
    data object DoubleInitialization : TestFailure
    data object OperationAfterDisposal : TestFailure
    data object ConcurrentStateMutation : TestFailure
    data object MultipleRefreshOperations : TestFailure

    // Lifecycle Failures
    data object ScreenDestructionDuringOp : TestFailure
    data object NavigationAway : TestFailure
    data object NavigationBack : TestFailure
    data object ConfigurationChange : TestFailure
    data object AppBackgrounded : TestFailure
    data object AppForegrounded : TestFailure
    data object ViewModelCleared : TestFailure
    data object ProcessDeath : TestFailure
    data object DependencyDisposed : TestFailure

    // Resource Failures
    data object MemoryExhausted : TestFailure
    data object CpuExhausted : TestFailure
    data object WorkerExhausted : TestFailure
    data object ThreadExhausted : TestFailure
    data object PoolExhausted : TestFailure
    data object QueueSaturated : TestFailure
    data object HugeDataset : TestFailure
    data object HugeMedia : TestFailure
    data object TooManySimultaneousOperations : TestFailure
    data object DiskExhausted : TestFailure

    // Time Failures
    data object ClockSkipForward : TestFailure
    data object ClockSkipBackward : TestFailure
    data object FrozenClock : TestFailure
    data object HighJitter : TestFailure
    data object ExtremeDrift : TestFailure
    data object Expiration : TestFailure
    data object RetryDeadlineExceeded : TestFailure
    data object BoundaryAtExpiration : TestFailure
    data object JustBeforeExpiration : TestFailure
    data object JustAfterExpiration : TestFailure
    data object ClockSkew : TestFailure
    data object LongRunningOperation : TestFailure

    // Input Failures
    data object NullInput : TestFailure
    data object EmptyInput : TestFailure
    data object BlankInput : TestFailure
    data object MalformedInput : TestFailure
    data object OversizedInput : TestFailure
    data object UnexpectedInputSchema : TestFailure
    data object NegativeValue : TestFailure
    data object ZeroValue : TestFailure
    data object MaximumValues : TestFailure
    data object UnexpectedEnum : TestFailure
    data object UnknownServerField : TestFailure
    data object InvalidIdentifier : TestFailure
    data object InvalidUrl : TestFailure
    data object InvalidFileMetadata : TestFailure
    data object UnicodeChaos : TestFailure
}
