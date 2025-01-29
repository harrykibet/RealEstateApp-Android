
@file:Suppress("ConstPropertyName")
object DatabaseDeps {
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    val allRoomDependencies = listOf(
        roomKtx,
        roomRuntime
    )

    val allRoomKaptDependencies = listOf(
        roomCompiler
    )
}