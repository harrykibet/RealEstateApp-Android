
@file:Suppress("ConstPropertyName")
object MLKitDeps {
    // GOOGLE  ML Kit On-Device
    const val mlKitBarcodeScanning = "com.google.mlkit:barcode-scanning:${Versions.mlKitBarcodeScanning}"
    const val mlKitTextRecognitionOnDevice = "com.google.mlkit:text-recognition:${Versions.mlKitTextRecognitionOnDevice}"
    const val mlKitFaceDetection = "com.google.mlkit:face-detection:${Versions.mlKitFaceDetection}"
    const val mlKitImageLabelingOnDevice = "com.google.mlkit:image-labeling:${Versions.mlKitImageLabelingOnDevice}"

    // GOOGLE ML Kit Cloud-Based
    const val mlKitTextRecognitionCloud = "com.google.android.gms:play-services-mlkit-text-recognition:${Versions.mlKitTextRecognitionCloud}"
    const val mlKitImageLabelingCloud = "com.google.android.gms:play-services-mlkit-image-labeling:${Versions.mlKitImageLabelingCloud}"

    val allMlKitDependencies = listOf(
        mlKitBarcodeScanning,
        mlKitTextRecognitionOnDevice,
        mlKitFaceDetection,
        mlKitImageLabelingOnDevice,
        mlKitTextRecognitionCloud,
        mlKitImageLabelingCloud
    )
}