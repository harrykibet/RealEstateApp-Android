/**
 * Object containing dependencies for Google ML Kit features, both on-device and cloud-based.
 *
 * This object provides easy access to the different ML Kit libraries by exposing them as
 * [Dependency.VersionedDependency] objects. It also offers a helper function to retrieve
 * all ML Kit dependencies as a list of dependency strings suitable for Gradle configuration.
 *
 * @property mlKitBarcodeScanning Dependency for on-device barcode scanning.
 * @property mlKitTextRecognitionOnDevice Dependency for on-device text recognition.
 * @property mlKitFaceDetection Dependency for on-device face detection.
 * @property mlKitImageLabelingOnDevice Dependency for on-device image labeling.
 * @property mlKitTextRecognitionCloud Dependency for cloud-based text recognition.
 * @property mlKitImageLabelingCloud Dependency for cloud-based image labeling.
 */
@Suppress("MemberVisibilityCanBePrivate")
object MLKitDeps {
    // GOOGLE ML Kit On-Device
    val mlKitBarcodeScanning = Dependency.VersionedDependency(
        group = "com.google.mlkit",
        name = "barcode-scanning",
        version = Versions.mlKitBarcodeScanning
    ).toGradleNotation

    val mlKitTextRecognitionOnDevice = Dependency.VersionedDependency(
        group = "com.google.mlkit",
        name = "text-recognition",
        version = Versions.mlKitTextRecognition
    ).toGradleNotation

    val mlKitFaceDetection = Dependency.VersionedDependency(
        group = "com.google.mlkit",
        name = "face-detection",
        version = Versions.mlKitFaceDetection
    ).toGradleNotation

    val mlKitImageLabelingOnDevice = Dependency.VersionedDependency(
        group = "com.google.mlkit",
        name = "image-labeling",
        version = Versions.mlKitImageLabeling
    ).toGradleNotation

    // GOOGLE ML Kit Cloud-Based
    val mlKitTextRecognitionCloud = Dependency.VersionedDependency(
        group = "com.google.android.gms",
        name = "play-services-mlkit-text-recognition",
        version = Versions.mlKitCloudTextRecognition
    ).toGradleNotation

    val mlKitImageLabelingCloud = Dependency.VersionedDependency(
        group = "com.google.android.gms",
        name = "play-services-mlkit-image-labeling",
        version = Versions.mlKitCloudImageLabeling
    ).toGradleNotation

    // Function to Retrieve All Dependencies
    fun getAllMLKitDeps() = listOf(
        mlKitBarcodeScanning,
        mlKitTextRecognitionOnDevice,
        mlKitFaceDetection,
        mlKitImageLabelingOnDevice,
        mlKitTextRecognitionCloud,
        mlKitImageLabelingCloud
    )
}
