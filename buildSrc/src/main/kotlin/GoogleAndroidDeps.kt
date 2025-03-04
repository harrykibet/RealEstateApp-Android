
@file:Suppress("ConstPropertyName")
object GoogleAndroidDeps {
    const val playServicesMaps = "com.google.android.gms:play-services-maps:${Versions.playServicesMaps}"
    const val playServicesLocation = "com.google.android.gms:play-services-location:${Versions.playServicesLocation}"
    const val places = "com.google.android.libraries.places:places:${Versions.googlePlaces}"
    const val playServicesAuth = "com.google.android.gms:play-services-auth:${Versions.playServicesAuth}"

    val AllPlayServicesDeps = listOf(
        playServicesMaps,
        playServicesLocation,
        places,
        playServicesAuth
    )
}