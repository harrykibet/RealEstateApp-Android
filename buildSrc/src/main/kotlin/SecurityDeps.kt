/**
 * Object containing dependencies related to security features, including:
 * - AndroidX Security Crypto KTX
 * - Bouncy Castle (core and PKIX)
 *
 * This object provides easy access to these dependencies and functions to retrieve
 * specific subsets of them.
 */
@Suppress("MemberVisibilityCanBePrivate")
object SecurityDeps {
    val securityCrypto = Dependency.VersionedDependency(
        group = "androidx.security",
        name = "security-crypto-ktx",
        version = Versions.securityKtx
    )

    val bouncyCastle = Dependency.VersionedDependency(
        group = "org.bouncycastle",
        name = "bcprov-jdk18on",
        version = Versions.bouncyCastle
    )

    val bouncyCastlePkix = Dependency.VersionedDependency(
        group = "org.bouncycastle",
        name = "bcpkix-jdk18on",
        version = Versions.bouncyCastle
    )

    // Function to Retrieve All Crypto Dependencies
    fun getAllCryptoDeps() = listOf(
        securityCrypto,
        bouncyCastle,
        bouncyCastlePkix
    ).map { it.get() }

    // Function to Retrieve Only BouncyCastle Dependencies
    fun getBouncyDeps() = listOf(
        bouncyCastle,
        bouncyCastlePkix
    ).map { it.get() }
}
