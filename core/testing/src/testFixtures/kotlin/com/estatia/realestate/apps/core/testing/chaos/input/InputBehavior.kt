package com.estatia.realestate.apps.core.testing.chaos.input

/**
 * Represents chaotic input scenarios for adversarial testing.
 */
sealed interface InputBehavior {
    data object NullInput : InputBehavior
    data object EmptyInput : InputBehavior
    data object BlankInput : InputBehavior
    data object MalformedInput : InputBehavior
    data object OversizedInput : InputBehavior
    data object UnexpectedSchema : InputBehavior
    data object NegativeValue : InputBehavior
    data object ZeroValue : InputBehavior
    data object MaximumValues : InputBehavior
    data object UnexpectedEnum : InputBehavior
    data object UnknownServerField : InputBehavior
    data object InvalidIdentifier : InputBehavior
    data object InvalidUrl : InputBehavior
    data object InvalidFileMetadata : InputBehavior
    data object UnicodeChaos : InputBehavior
}
