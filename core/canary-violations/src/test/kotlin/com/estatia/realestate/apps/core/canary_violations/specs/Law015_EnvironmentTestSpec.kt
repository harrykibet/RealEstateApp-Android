package com.estatia.realestate.apps.core.canary_violations.specs

/**
 * LAW-015: Time in Test (Heuristic path check)
 */
public class Law015_EnvironmentTestSpec {
    // [CANARY:POSITIVE:DirectSystemTimeUsageInTest]
    public fun testTime(): Long = System.currentTimeMillis()
}
