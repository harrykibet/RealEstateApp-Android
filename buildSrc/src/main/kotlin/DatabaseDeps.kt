
@file:Suppress("ConstPropertyName")
object DatabaseDeps {
    const val roomKtx = "androidx.room:room-ktx:${Versions.androidx_room}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.androidx_room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.androidx_room}"

    val AllRoomDependencies = listOf(
        roomKtx,
        roomRuntime
    )

    val AllRoomKaptDependencies = listOf(
        roomCompiler
    )
}