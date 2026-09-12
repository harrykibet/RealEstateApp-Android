package com.estatia.realestate.apps.core.canary_violations.specs

import io.mockk.mockk

/**
 * LAW-007: Wall-clock Time
 * LAW-015: Time in Test (Checked in separate test file)
 * LAW-016: Mock in Production
 */

public class Environment_Positive_Spec {
    // [CANARY:POSITIVE:DirectSystemTimeUsage:WARN:Direct usage of system time]
    public fun now(): Long = System.currentTimeMillis()
    
    // [CANARY:POSITIVE:MockInProduction:BLOCK:Testing library usage]
    public fun mock(): String = mockk<String>()
}

public class Environment_Negative_Spec {
    // No direct time or mocks
    public fun safe(): String = ""
}
