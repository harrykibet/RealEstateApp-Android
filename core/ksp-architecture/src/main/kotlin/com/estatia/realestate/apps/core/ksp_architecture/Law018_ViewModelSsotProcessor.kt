package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-018: ViewModel SSoT (Single Source of Truth).
 * 
 * Enforces that a ViewModel has exactly one canonical persistent UI-state owner (StateFlow).
 */
class Law018_ViewModelSsotProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val allowedAnnotation = "com.estatia.realestate.apps.core.common.annotations.AllowedArchitectureDependency"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val viewModelFqn = "androidx.lifecycle.ViewModel"
        val viewModelMarkerFqn = "com.estatia.realestate.apps.core.common.annotations.ViewModelMarker"
        val stateFlowFqn = "kotlinx.coroutines.flow.StateFlow"

        val viewModelType = resolver.getClassDeclarationByName(resolver.getKSNameFromString(viewModelFqn))?.asStarProjectedType()
        val stateFlowType = resolver.getClassDeclarationByName(resolver.getKSNameFromString(stateFlowFqn))?.asStarProjectedType()

        val symbols = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { clazz ->
                val hasMarker = clazz.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == viewModelMarkerFqn }
                val isViewModel = viewModelType?.isAssignableFrom(clazz.asStarProjectedType()) == true
                hasMarker || isViewModel
            }

        symbols.forEach { clazz ->
            if (clazz.classKind == ClassKind.INTERFACE || clazz.modifiers.contains(Modifier.ABSTRACT)) return@forEach

            // 🛡️ REFINEMENT: Use getAllProperties() to catch inherited state ownership.
            // Filter only public properties that aren't part of the infrastructure base classes.
            val publicProperties = clazz.getAllProperties().filter { prop ->
                prop.isPublic() && !isFromBaseViewModel(prop)
            }

            // 🛡️ REFINEMENT: Identify canonical state owners (StateFlow or subtypes).
            // This is more semantic than literal FQN matching as it supports custom state wrappers
            // that inherit from StateFlow.
            val stateFlows = publicProperties.filter { prop ->
                val type = prop.type.resolve()
                val isStateFlow = stateFlowType?.isAssignableFrom(type) == true
                val isAuthorized = prop.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation }
                
                isStateFlow && !isAuthorized
            }.toList()

            // 1. Enforce SSoT: Multiple state authorities indicate a "Bag of State" smell.
            // This prevents the ViewModel from becoming an uncoordinated collection of state pieces.
            if (stateFlows.size > 1) {
                logger.report(
                    Law.LAW_018,
                    "Multiple state authorities detected in '${clazz.simpleName.asString()}' (${stateFlows.joinToString { it.simpleName.asString() }}). " +
                    "A ViewModel must expose exactly one canonical persistent UI-state owner to maintain Single Source of Truth.",
                    clazz
                )
            }

            // 2. Enforce Presence: A ViewModel should have a state owner unless authorized.
            if (stateFlows.isEmpty() && !clazz.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation }) {
                logger.report(
                    Law.LAW_018,
                    "ViewModel '${clazz.simpleName.asString()}' has no public StateFlow. " +
                    "ViewModels must expose a canonical persistent UI-state owner.",
                    clazz
                )
            }
        }
        return emptyList()
    }

    private fun KSPropertyDeclaration.isPublic(): Boolean {
        return !modifiers.contains(Modifier.PRIVATE) &&
               !modifiers.contains(Modifier.INTERNAL) &&
               !modifiers.contains(Modifier.PROTECTED)
    }

    private fun isFromBaseViewModel(prop: KSPropertyDeclaration): Boolean {
        val parent = prop.parentDeclaration as? KSClassDeclaration ?: return false
        val parentFqn = parent.qualifiedName?.asString() ?: ""
        return parentFqn == "androidx.lifecycle.ViewModel" || parentFqn == "java.lang.Object"
    }
}

class Law018_ViewModelSsotProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law018_ViewModelSsotProcessor(environment.logger)
    }
}
