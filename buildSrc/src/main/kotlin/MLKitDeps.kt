
@file:Suppress("ConstPropertyName")
object MLKitDeps {
    // GOOGLE  ML Kit On-Device
    const val mlKitBarcodeScanning = "com.google.mlkit:barcode-scanning:${Versions.google_mlKit_Barcode_Scanning}"
    const val mlKitTextRecognitionOnDevice = "com.google.mlkit:text-recognition:${Versions.google_mlKit_Text_Recognition}"
    const val mlKitFaceDetection = "com.google.mlkit:face-detection:${Versions.google_mlKit_Face_Detection}"
    const val mlKitImageLabelingOnDevice = "com.google.mlkit:image-labeling:${Versions.google_mlKit_Image_Labeling}"

    // GOOGLE ML Kit Cloud-Based
    const val mlKitTextRecognitionCloud = "com.google.android.gms:play-services-mlkit-text-recognition:${Versions.play_services_mlKit_Text_Recognition}"
    const val mlKitImageLabelingCloud = "com.google.android.gms:play-services-mlkit-image-labeling:${Versions.play_services_mlKit_Image_Labeling}"

    val allMlKitDependencies = listOf(
        mlKitBarcodeScanning,
        mlKitTextRecognitionOnDevice,
        mlKitFaceDetection,
        mlKitImageLabelingOnDevice,
        mlKitTextRecognitionCloud,
        mlKitImageLabelingCloud
    )
}