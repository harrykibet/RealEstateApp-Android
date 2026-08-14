package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel

object DemoData {
    val sampleProperties = listOf(
        PropertyEntityModel(
            id = "1",
            title = "Luxury Villa in Nairobi",
            description = "A beautiful 5-bedroom villa with a pool and scenic views.",
            price = 45000000.0,
            county = "Nairobi",
            bedrooms = 5,
            bathrooms = 4,
            areaSize = 450.0,
            imageUrl = listOf("https://images.unsplash.com/photo-1613490493576-7fde63acd811?q=80&w=1000"),
            directVideoUrls = listOf("https://vjs.zencdn.net/v/oceans.mp4"),
            hlsUrl = listOf("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
            video = true,
            ownerName = "Harry Kemboi",
            ownerId = "demo_user"
        ),
        PropertyEntityModel(
            id = "2",
            title = "Modern Apartment in Westlands",
            description = "Fully furnished 2-bedroom apartment with high-speed internet.",
            price = 15000000.0,
            county = "Nairobi",
            bedrooms = 2,
            bathrooms = 2,
            areaSize = 120.0,
            imageUrl = listOf("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1000"),
            directVideoUrls = listOf("https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"),
            hlsUrl = listOf("https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"),
            video = true,
            ownerName = "Harry Kemboi",
            ownerId = "demo_user"
        ),
        PropertyEntityModel(
            id = "3",
            title = "Cosy Cottage in Karen",
            description = "A quiet and peaceful cottage surrounded by nature.",
            price = 25000000.0,
            county = "Nairobi",
            bedrooms = 3,
            bathrooms = 2,
            areaSize = 200.0,
            imageUrl = listOf("https://images.unsplash.com/photo-1518780664697-55e3ad937233?q=80&w=1000"),
            directVideoUrls = listOf("https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4"),
            video = true,
            ownerName = "James Kibet",
            ownerId = "user_123"
        ),
        PropertyEntityModel(
            id = "4",
            title = "Elegant Townhouse in Lavington",
            description = "Spacious 4-bedroom townhouse in a gated community.",
            price = 35000000.0,
            county = "Nairobi",
            bedrooms = 4,
            bathrooms = 3,
            areaSize = 300.0,
            imageUrl = listOf("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?q=80&w=1000"),
            directVideoUrls = listOf("https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4"),
            video = true,
            ownerName = "Harry Kemboi",
            ownerId = "demo_user"
        ),
        PropertyEntityModel(
            id = "5",
            title = "Stylish Studio in Kilimani",
            description = "Modern studio apartment with great amenities.",
            price = 8000000.0,
            county = "Nairobi",
            bedrooms = 1,
            bathrooms = 1,
            areaSize = 45.0,
            imageUrl = listOf("https://images.unsplash.com/photo-1536376074432-8f258fa71400?q=80&w=1000"),
            directVideoUrls = listOf("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"),
            video = true,
            ownerName = "James Kibet",
            ownerId = "user_123"
        )
    )

    val demoUser = UserEntityModel(
        userId = "demo_user",
        name = "Harry Kemboi",
        email = "truman948@gmail.com",
        userType = "AGENT",
        verificationLevel = "TRUSTED_PARTNER",
        likedProperties = listOf("1")
    )
}
