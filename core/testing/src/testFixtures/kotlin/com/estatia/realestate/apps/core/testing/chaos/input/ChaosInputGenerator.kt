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
            InputBehavior.UnicodeChaos -> "Unicode \uD83D\uDCA3 \u2623 \uD83D\uDD25"
            InputBehavior.Success -> validDefault
            else -> validDefault
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
