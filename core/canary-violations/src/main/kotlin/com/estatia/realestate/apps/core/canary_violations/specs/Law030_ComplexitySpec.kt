package com.estatia.realestate.apps.core.canary_violations.specs

/**
 * LAW-030: Orchestration Monster
 * LAW-028: Spaghetti Method
 */

public class Law028_Positive_Spec {
    public fun spaghetti() {
        // [CANARY:POSITIVE:SpaghettiMethodFatal]
        if (true) { if (true) { if (true) { if (true) { if (true) { if (true) {
            println(12345) // [CANARY:POSITIVE:MagicNumber]
        } } } } } }
    }
}

// [CANARY:POSITIVE:OrchestrationMonsterError]
public class Law030_Positive_Spec(
    public val r1: Law012_018_023_029_Positive_ViewModel,
    public val r2: Law012_018_023_029_Positive_ViewModel,
    public val r3: Law012_018_023_029_Positive_ViewModel,
    public val r4: Law012_018_023_029_Positive_ViewModel,
    public val r5: Law012_018_023_029_Positive_ViewModel,
    public val r6: Law012_018_023_029_Positive_ViewModel,
    public val r7: Law012_018_023_029_Positive_ViewModel,
    public val r8: Law012_018_023_029_Positive_ViewModel,
    public val r9: Law012_018_023_029_Positive_ViewModel,
    public val r10: Law012_018_023_029_Positive_ViewModel
)

public class Complexity_Negative_Spec(
    public val r1: Law012_018_023_029_Positive_ViewModel
) {
    public fun clean() {
        println("ok")
    }
}
