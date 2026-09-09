package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Enforcement
import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

/**
 * Standardized logging for Estatia Architectural Laws in KSP.
 * Decides whether to use error() or warn() based on the Enforcement type.
 */
fun KSPLogger.report(law: Law, message: String, node: KSNode? = null) {
    val prefix = when (law.enforcement) {
        Enforcement.BLOCK -> "FATAL Architecture Law"
        Enforcement.WARN -> "Architecture Warning"
        Enforcement.INFO -> "Architecture Convention"
    }
    
    val fullMessage = "$prefix (${law.id}) [Risk: ${law.risk.name}, Confidence: ${law.confidence.name}]: ${law.description} $message"
    
    when (law.enforcement) {
        Enforcement.BLOCK -> error(fullMessage, node)
        else -> warn(fullMessage, node)
    }
}
