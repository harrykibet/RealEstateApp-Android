package com.estatia.realestate.apps.core.testing.chaos.input

/**
 * Utility for generating adversarial input data based on [InputBehavior].
 */
object ChaosInputGenerator {

    /**
     * Generates a string based on the provided [behavior].
     * @param validDefault A valid string to use as a baseline if the behavior is Success.
     */
    fun generateString(behavior: InputBehavior, validDefault: String = "ValidString123"): String? {
        return when (behavior) {
            InputBehavior.NullInput -> null
            InputBehavior.EmptyInput -> ""
            InputBehavior.BlankInput -> "   "
            InputBehavior.MalformedInput -> "malformed_!@#$%^&*"
            InputBehavior.OversizedInput -> "A".repeat(10_001)
            InputBehavior.UnexpectedSchema -> "{\"unexpected\": \"field\"}"
            InputBehavior.UnexpectedEnum -> "UNKNOWN_ENUM_VALUE"
            InputBehavior.UnknownServerField -> "{\"new_server_field\": true, \"data\": \"$validDefault\"}"
            InputBehavior.InvalidIdentifier -> "invalid-id-!!!"
            InputBehavior.InvalidUrl -> "ftp://invalid-url"
            InputBehavior.InvalidFileMetadata -> "{\"size\": -1, \"mime\": \"text/plain\"}"
            InputBehavior.UnicodeChaos -> "Unicode \uD83D\uDCA3 \u2623 \uD83D\uDD25"
            InputBehavior.Success,
            InputBehavior.NegativeValue,
            InputBehavior.ZeroValue,
            InputBehavior.MaximumValues -> validDefault
        }
    }

    /**
     * Generates an integer based on the provided [behavior].
     */
    fun generateInt(behavior: InputBehavior, validDefault: Int = 10): Int {
        return when (behavior) {
            InputBehavior.NegativeValue -> -1
            InputBehavior.ZeroValue -> 0
            InputBehavior.MaximumValues -> Int.MAX_VALUE
            InputBehavior.Success -> validDefault
            else -> validDefault
        }
    }
}
