package com.estatia.realestate.apps.core.canary_violations.specs

/**
 * LAW-030: Orchestration Monster
 * LAW-028: Spaghetti Method
 */

public class Law028_Positive_Spec {
    public fun spaghetti() {
        // [CANARY:POSITIVE:SpaghettiMethodFatal:WARN:spaghetti]
        if (true) { if (true) { if (true) { if (true) { if (true) { if (true) {
            println(12345) // [CANARY:POSITIVE:MagicNumber:WARN:Magic number]
        } } } } } }
    }
}

// [CANARY:POSITIVE:OrchestrationMonsterError:WARN:Orchestration Monster]
public class Law030_Positive_Spec(
    public val r1: PositiveStateViewModel,
    public val r2: PositiveStateViewModel,
    public val r3: PositiveStateViewModel,
    public val r4: PositiveStateViewModel,
    public val r5: PositiveStateViewModel,
    public val r6: PositiveStateViewModel,
    public val r7: PositiveStateViewModel,
    public val r8: PositiveStateViewModel,
    public val r9: PositiveStateViewModel,
    public val r10: PositiveStateViewModel
)

public class Complexity_Negative_Spec(
    public val r1: PositiveStateViewModel
) {
    public fun clean() {
        println("ok")
    }
}
