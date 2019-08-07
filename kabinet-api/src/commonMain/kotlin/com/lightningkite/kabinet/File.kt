package com.lightningkite.kabinet

import kotlinx.io.core.Input


data class File(val fileSystem: FileSystem, val path: AbsolutePath) {
    suspend fun info(): FileInformation? = fileSystem.info(path)
    suspend fun exists(): Boolean = fileSystem.exists(path)
    /**
     * Makes the directories if needed.
     */
    suspend fun overwriteAll(data: ByteArray) = fileSystem.overwriteAll(path, data)
    suspend fun overwrite() = fileSystem.overwrite(path)
    suspend fun read(): Input? = fileSystem.read(path)
    suspend fun readAll(): ByteArray? = fileSystem.readAll(path)

    fun child(part: String) = File(fileSystem, path.child(part))
}

val FileSystem.root: File get() = File(fileSystem = this, path = AbsolutePath("/"))
val FileInformation.children: List<File>? get() = childrenPaths?.map { File(fileSystem = fileSystem, path = it) }