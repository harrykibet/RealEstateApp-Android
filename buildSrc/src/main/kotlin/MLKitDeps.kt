
@file:Suppress("ConstPropertyName")
object MLKitDeps {
    // GOOGLE  ML Kit On-Device
    const val mlKitBarcodeScanning = "com.google.mlkit:barcode-scanning:${Versions.google_mlkit_barcode_scanning}"
    const val mlKitTextRecognitionOnDevice = "com.google.mlkit:text-recognition:${Versions.google_mlkit_text_recognition}"
    const val mlKitFaceDetection = "com.google.mlkit:face-detection:${Versions.google_mlkit_face_detection}"
    const val mlKitImageLabelingOnDevice = "com.google.mlkit:image-labeling:${Versions.google_mlkit_image_labeling}"

    // GOOGLE ML Kit Cloud-Based
    const val mlKitTextRecognitionCloud = "com.google.android.gms:play-services-mlkit-text-recognition:${Versions.play_services_mlkit_text_recognition}"
    const val mlKitImageLabelingCloud = "com.google.android.gms:play-services-mlkit-image-labeling:${Versions.play_services_mlkit_image_labeling}"

    val AllMlKitDependencies = listOf(
        mlKitBarcodeScanning,
        mlKitTextRecognitionOnDevice,
        mlKitFaceDetection,
        mlKitImageLabelingOnDevice,
        mlKitTextRecognitionCloud,
        mlKitImageLabelingCloud
    )
}