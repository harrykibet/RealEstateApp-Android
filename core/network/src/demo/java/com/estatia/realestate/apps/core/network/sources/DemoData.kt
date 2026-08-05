package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel

object DemoData {
    val sampleProperties = listOf(
        PropertyEntityModel(
            id = "1",
            title = "Luxury Villa in Nairobi",
            description = "A beautiful 5-bedroom villa with a pool.",
            price = 45000000.0,
            county = "Nairobi",
            bedrooms = 5,
            bathrooms = 4,
            areaSize = 450.0,
            imageUrl = listOf("https://images.unsplash.com/photo-1613490493576-7fde63acd811?q=80&w=1000")
        ),
        PropertyEntityModel(
            id = "2",
            title = "Modern Apartment in Westlands",
            description = "Fully furnished 2-bedroom apartment.",
            price = 15000000.0,
            county = "Nairobi",
            bedrooms = 2,
            bathrooms = 2,
            areaSize = 120.0,
            imageUrl = listOf("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1000")
        )
    )

    val demoUser = UserEntityModel(
        userId = "demo_user",
        name = "Harry Kemboi",
        email = "truman948@gmail.com",
        userType = "AGENT",
        verified = true,
        likedProperties = listOf("1")
    )
}
