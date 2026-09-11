package com.estatia.realestate.apps.core.network.db_names

import com.estatia.realestate.apps.core.architecture.annotations.Utility

@Utility
object FirestoreCollections {
    const val USERS = "users"
    const val PROPERTIES = "properties"
    const val ANALYTICS = "analytics"

@Utility
    object SubCollections {
        const val LIKES = "likes"
        const val COMMENTS = "comments"
        const val OWNED_PROPERTIES = "ownedproperties"
        const val LIKED_PROPERTIES = "likedproperties"
        const val CONTACT = "contact"
    }
}
