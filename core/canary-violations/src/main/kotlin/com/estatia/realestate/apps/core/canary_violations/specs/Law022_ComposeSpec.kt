package com.estatia.realestate.apps.core.canary_violations.specs

// [CANARY:POSITIVE:DesignSystemViolation:WARN:EstatiaText]
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

public class PositiveComposeUiSpec {
    @Composable
    public fun PositiveCanary() {
        // [CANARY:POSITIVE:HardcodedDesignValue:WARN:Hardcoded color]
        val color = Color(0xFFFF0000)
        
        // [CANARY:POSITIVE:HardcodedDesignValue:WARN:Hardcoded color]
        val padding = 16.dp
        
        // [CANARY:POSITIVE:HardcodedStringInCompose:WARN:Hardcoded string]
        Text(text = "Hardcoded", color = color)
    }
}
