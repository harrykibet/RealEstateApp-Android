
@file:Suppress("ConstPropertyName")
object GoogleDeps {
    const val playServicesMaps = "com.google.android.gms:play-services-maps:${Versions.play_services_maps}"
    const val playServicesLocation = "com.google.android.gms:play-services-location:${Versions.play_services_location}"
    const val places = "com.google.android.libraries.places:places:${Versions.google_libs_places}"
    const val playServicesAuth = "com.google.android.gms:play-services-auth:${Versions.play_services_auth}"

    const val googleSecretsManager = "com.google.cloud:google-cloud-secretmanager:${Versions.google_secrets_manager}"
    const val googleAuthCloud = "com.google.auth:google-auth-library-oauth2-http:${Versions.google_auth_cloud}"

    val AllPlayServicesDependencies = listOf(
        playServicesMaps,
        playServicesLocation,
        places,
        playServicesAuth
    )
}