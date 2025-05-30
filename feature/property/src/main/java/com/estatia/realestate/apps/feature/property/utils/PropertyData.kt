package com.estatia.realestate.apps.feature.property.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyData @Inject constructor() {
    // Static data for property types
    val propertyTypes: List<String> = listOf(
        "Apartment", "House", "Condo", "Single Room", "Bedsitter", "Studio", "Duplex",
        "Triplex", "Townhouse", "Office", "Commercial", "Retail", "Warehouse", "Land",
        "Industrial", "Co-working Space", "Farmhouse", "Villa", "Bungalow", "Mansion",
        "Penthouse", "Cottage", "Hostel", "Guest House", "Resort", "Chalet", "Cabin"
    )

    // Static data for counties in Kenya
    val counties: List<String> = listOf(
        "Baringo", "Bomet", "Bungoma", "Busia", "Elgeyo-Marakwet", "Embu", "Garissa",
        "Homa Bay", "Isiolo", "Kajiado", "Kakamega", "Kericho", "Kiambu", "Kilifi",
        "Kirinyaga", "Kisii", "Kisumu", "Kitui", "Kwale", "Laikipia", "Lamu", "Machakos",
        "Makueni", "Mandera", "Marsabit", "Meru", "Migori", "Mombasa", "Muranga", "Nairobi",
        "Nakuru", "Nandi", "Narok", "Nyamira", "Nyandarua", "Nyeri", "Samburu", "Siaya",
        "Taita-Taveta", "Tana River", "Tharaka-Nithi", "Trans-Nzoia", "Turkana", "Uasin Gishu",
        "Vihiga", "Wajir", "West Pokot"
    )
}
