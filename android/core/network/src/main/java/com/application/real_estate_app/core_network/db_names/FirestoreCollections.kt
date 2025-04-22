package com.application.real_estate_app.core_network.db_names

object FirestoreCollections {
    const val USERS = "users"
    const val PROPERTIES = "properties"
    const val ANALYTICS = "analytics"

    object SubCollections {
        const val LIKES = "likes"
        const val COMMENTS = "comments"
        const val OWNED_PROPERTIES = "ownedproperties"
        const val LIKED_PROPERTIES = "likedproperties"
    }
}
