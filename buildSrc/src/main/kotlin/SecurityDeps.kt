
@file:Suppress("ConstPropertyName")
object SecurityDeps {
    const val securityCrypto = "androidx.security:security-crypto-ktx:${Versions.securityKtx}"
    const val bouncyCastle = "org.bouncycastle:bcprov-jdk18on:${Versions.bouncyCastle}"
    const val bouncyCastlePkix = "org.bouncycastle:bcpkix-jdk18on:${Versions.bouncyCastle}"

    val AllCryptoDeps = listOf(
        securityCrypto,
        bouncyCastle,
        bouncyCastlePkix
    )

    val BouncyDeps = listOf(
        bouncyCastle,
        bouncyCastlePkix
    )
}

