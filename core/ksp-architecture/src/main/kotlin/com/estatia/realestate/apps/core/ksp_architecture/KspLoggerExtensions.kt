package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.LawType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

/**
 * Standardized logging for Estatia Architectural Laws in KSP.
 * Decides whether to use error() or warn() based on the LawType.
 */
fun KSPLogger.report(law: Law, message: String, node: KSNode? = null) {
    val prefix = when (law.type) {
        LawType.FATAL -> "FATAL Architecture Law"
        LawType.ERROR -> "Architecture Law"
        LawType.CONVENTION -> "Architecture Convention"
        else -> "Architecture Warning"
    }
    
    val fullMessage = "$prefix (${law.id}) [Fidelity: ${law.primaryFidelity.name}]: ${law.description} $message"
    
    when (law.type) {
        LawType.FATAL, LawType.ERROR -> error(fullMessage, node)
        else -> warn(fullMessage, node)
    }
}
