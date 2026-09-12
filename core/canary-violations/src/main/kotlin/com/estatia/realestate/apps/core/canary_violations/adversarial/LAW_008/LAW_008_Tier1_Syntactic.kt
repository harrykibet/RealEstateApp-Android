package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_008

import com.estatia.realestate.apps.core.architecture.annotations.Identity
import java.util.ArrayList

/**
 * LAW-008: Tier 1 - Syntactic Bypasses
 */

@Identity.Repository
public class SyntacticAbstractionAdversary {
    // [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
    public fun getRawData(): ArrayList<String> = ArrayList()
}

public class NameInferenceAdversaryRepository {
    // [CANARY:NEGATIVE:ImplementationTypeInPublicApi]
    // Suffix match but not annotated - should be clean if we shifted to semantic truth.
    public fun getLeakedData(): ArrayList<String> = ArrayList()
}
