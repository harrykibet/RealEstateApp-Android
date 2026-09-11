package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-041: Identity Integrity Processor.
 * 
 * Verifies that architectural annotations (claimed identity) are consistent 
 * with the structural reality of the component (implementation truth).
 */
class Law041_IdentityIntegrityProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val archAnnotationPrefix = "com.estatia.realestate.apps.core.architecture.annotations."
        val viewModelFqn = "androidx.lifecycle.ViewModel"
        val viewModelType = resolver.getClassDeclarationByName(resolver.getKSNameFromString(viewModelFqn))?.asStarProjectedType()

        val symbols = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()

        symbols.forEach { clazz ->
            clazz.annotations.forEach { ann ->
                val qn = ann.annotationType.resolve().declaration.qualifiedName?.asString() ?: ""
                if (!qn.startsWith(archAnnotationPrefix)) return@forEach

                val role = qn.substringAfterLast(".")

                when (role) {
                    "Contract" -> {
                        if (clazz.classKind != ClassKind.INTERFACE) {
                            reportMismatch(role, "Interfaces only", clazz)
                        }
                    }
                    "ViewModelMarker" -> {
                        val isViewModel = viewModelType?.isAssignableFrom(clazz.asStarProjectedType()) == true
                        if (!isViewModel) {
                            reportMismatch(role, "Subclasses of androidx.lifecycle.ViewModel only", clazz)
                        }
                    }
                    "DomainModel", "UiState", "BatteryState", "NetworkState", "EnvironmentState", "AnalyticsState", "AuthState", "PlayerState", "UiAction", "UiEvent" -> {
                        val isData = clazz.modifiers.contains(Modifier.DATA)
                        val isSealed = clazz.modifiers.contains(Modifier.SEALED)
                        val isValue = clazz.modifiers.contains(Modifier.VALUE) || clazz.annotations.any { it.shortName.asString() == "JvmInline" }
                        
                        if (!isData && !isSealed && !isValue) {
                            reportMismatch(role, "Data classes, Sealed classes, or Value classes only", clazz)
                        }
                    }
                    "EntityModel" -> {
                        if (!clazz.modifiers.contains(Modifier.DATA)) {
                            reportMismatch(role, "Data classes only", clazz)
                        }
                    }
                    "Mapper" -> {
                        if (clazz.classKind != ClassKind.OBJECT && clazz.classKind != ClassKind.CLASS) {
                            reportMismatch(role, "Objects or Classes only", clazz)
                        }
                    }
                    "Foundation" -> {
                        if (clazz.classKind != ClassKind.INTERFACE && clazz.classKind != ClassKind.CLASS) {
                            reportMismatch(role, "Interfaces or Implementation classes only", clazz)
                        }
                    }
                    "Helper" -> {
                        // 🛡️ REFINEMENT: Verify @Helper is only for passive holders
                        if (clazz.getDeclaredFunctions().any { it.simpleName.asString() != "<init>" }) {
                            reportMismatch(role, "Passive constant holders only (no functions allowed). Use @Utility or @Service for logic.", clazz)
                        }
                    }
                    "Repository", "UseCase" -> {
                        if (clazz.classKind != ClassKind.CLASS) {
                            reportMismatch(role, "Classes only", clazz)
                        }
                    }
                }
            }
        }
        return emptyList()
    }

    private fun reportMismatch(role: String, requirement: String, node: KSNode) {
        logger.report(
            Law.LAW_041,
            "Identity/Truth Mismatch: Component annotated as '@$role' does not satisfy its structural requirement ($requirement).",
            node
        )
    }
}

class Law041_IdentityIntegrityProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law041_IdentityIntegrityProcessor(environment.logger)
    }
}
