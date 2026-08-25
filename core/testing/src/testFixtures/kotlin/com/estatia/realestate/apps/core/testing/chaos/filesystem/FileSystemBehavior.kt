package com.estatia.realestate.apps.core.testing.chaos.filesystem

/**
 * Represents scriptable filesystem behaviors for adversarial testing.
 */
sealed interface FileSystemBehavior {
    data object Success : FileSystemBehavior
    data object DiskFull : FileSystemBehavior
    data object PermissionDenied : FileSystemBehavior
    data object FileMissing : FileSystemBehavior
    data object FileDisappearsDuringOp : FileSystemBehavior
    data object CorruptFile : FileSystemBehavior
    data object ZeroByteFile : FileSystemBehavior
    data object UnsupportedFormat : FileSystemBehavior
    data object WrongMimeType : FileSystemBehavior
    data object PartialFile : FileSystemBehavior
    data object FileChangesWhileReading : FileSystemBehavior
    data object VeryLargeFile : FileSystemBehavior
    data object IoFailure : FileSystemBehavior
}
