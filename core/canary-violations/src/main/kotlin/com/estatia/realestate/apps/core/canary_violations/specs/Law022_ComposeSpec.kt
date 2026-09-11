package com.estatia.realestate.apps.core.canary_violations.specs

// [CANARY:POSITIVE:DesignSystemViolation:WARN:Design system component required]
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

public class PositiveComposeUiSpec {
    @Composable
    public fun PositiveCanary() {
        // [CANARY:POSITIVE:HardcodedDesignValue:WARN:Hardcoded color or dimension]
        val color = Color(0xFFFF0000)
        
        // [CANARY:POSITIVE:HardcodedDesignValue:WARN:Hardcoded color or dimension]
        val padding = 16.dp
        
        // [CANARY:POSITIVE:HardcodedStringInCompose:WARN:Hardcoded string in Composable]
        Text(text = "Hardcoded", color = color)
    }
}
