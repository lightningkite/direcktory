package com.lightningkite.kabinet



data class File(val fileSystem: FileSystem, val path: AbsolutePath) {
    suspend fun info(): FileInformation? = fileSystem.info(path)
    suspend fun exists(): Boolean = fileSystem.exists(path)
    /**
     * Makes the directories if needed.
     */
    suspend fun overwrite(data: ByteArray) = fileSystem.overwrite(path, data)

    suspend fun readAll(): ByteArray? = fileSystem.readAll(path)
}

val FileSystem.root: File get() = File(fileSystem = this, path = AbsolutePath("/"))
val FileInformation.children: List<File>? get() = childrenPaths?.map { File(fileSystem = fileSystem, path = it) }