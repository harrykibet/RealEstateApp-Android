
@file:Suppress("ConstPropertyName")
object DatabaseDeps {
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    val AllRoomDeps = listOf(
        roomKtx,
        roomRuntime
    )

    val AllRoomKaptDeps = listOf(
        roomCompiler
    )
}