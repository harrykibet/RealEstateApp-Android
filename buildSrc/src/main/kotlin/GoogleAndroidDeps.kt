
@file:Suppress("ConstPropertyName")
object GoogleAndroidDeps {
    const val playServicesMaps = "com.google.android.gms:play-services-maps:${Versions.play_services_maps}"
    const val playServicesLocation = "com.google.android.gms:play-services-location:${Versions.play_services_location}"
    const val places = "com.google.android.libraries.places:places:${Versions.google_libs_places}"
    const val playServicesAuth = "com.google.android.gms:play-services-auth:${Versions.play_services_auth}"

    val AllPlayServicesDependencies = listOf(
        playServicesMaps,
        playServicesLocation,
        places,
        playServicesAuth
    )
}