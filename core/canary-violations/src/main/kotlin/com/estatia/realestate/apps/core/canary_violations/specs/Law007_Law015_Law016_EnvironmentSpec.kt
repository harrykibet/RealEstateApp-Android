package com.estatia.realestate.apps.core.canary_violations.specs

import io.mockk.mockk

/**
 * LAW-007: Wall-clock Time
 * LAW-015: Time in Test
 * LAW-016: Mock in Production
 */

public class Environment_Positive_Spec {
    // [CANARY:POSITIVE:DirectSystemTimeUsage]
    public fun now(): Long = System.currentTimeMillis()
    
    // [CANARY:POSITIVE:MockInProduction]
    public fun mock(): String = mockk<String>()
}

/**
 * Heuristic based rule for Tests
 */
public class Law015_Test_Spec {
    // [CANARY:POSITIVE:DirectSystemTimeUsageInTest]
    public fun testTime(): Long = System.currentTimeMillis()
}

public class Environment_Negative_Spec {
    // No direct time or mocks
    public fun safe() = ""
}
