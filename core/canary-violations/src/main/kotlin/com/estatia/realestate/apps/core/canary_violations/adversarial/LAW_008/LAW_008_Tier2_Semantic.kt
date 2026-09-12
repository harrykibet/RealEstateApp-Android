package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_008

import com.estatia.realestate.apps.core.architecture.annotations.Identity
import java.util.ArrayList

/**
 * LAW-008: Tier 2 - Semantic Bypasses
 */

@Identity.Repository
public class SemanticAbstractionAdversary

// [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
public fun SemanticAbstractionAdversary.leakedExtension(): ArrayList<Int> = ArrayList()

@Identity.UseCase
public class DeepNestingAdversary {
    // [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
    public fun superDeep(): List<Map<String, ArrayList<Int>>> = emptyList()
}

@Identity.Contract
public interface TrickyContract {
    public object Constants {
        // [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
        public val SHARED_CACHE: ArrayList<String> = ArrayList()
    }
}
