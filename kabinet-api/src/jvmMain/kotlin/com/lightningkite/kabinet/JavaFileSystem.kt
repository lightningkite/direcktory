package com.lightningkite.kabinet

import com.lightningkite.kommon.string.MediaType
import com.lightningkite.lokalize.time.TimeStamp
import java.net.URLConnection

class JavaFileSystem(val root: java.io.File) : FileSystem {
    override suspend fun info(path: AbsolutePath): FileInformation? {
        val fullJavaFile = java.io.File(root, path.string)
        if (fullJavaFile.exists()) {
            return FileInformation(
                    fileSystem = this,
                    childrenPaths = fullJavaFile.list()?.map { AbsolutePath(it) },
                    type = MediaType(URLConnection.getFileNameMap().getContentTypeFor(fullJavaFile.name)),
                    created = TimeStamp(fullJavaFile.lastModified()),
                    lastModified = TimeStamp(fullJavaFile.lastModified()),
                    readable = true,
                    writable = true,
                    length = fullJavaFile.length()
            )
        } else {
            return null
        }
    }

    override suspend fun exists(path: AbsolutePath): Boolean = java.io.File(root, path.string).exists()

    override suspend fun overwrite(path: AbsolutePath, data: ByteArray) {
        val file = java.io.File(root, path.string)
        file.parentFile.mkdirs()
        file.writeBytes(data)
    }

    override suspend fun readAll(path: AbsolutePath): ByteArray? {
        val file = java.io.File(root, path.string)
        if(!file.exists()) return null
        return file.readBytes()
    }

    override suspend fun deleteIfExists(path: AbsolutePath): Boolean {
        val file = java.io.File(root, path.string)
        return file.delete()
    }

    override suspend fun deleteRecursivelyIfExists(path: AbsolutePath): Boolean {
        val file = java.io.File(root, path.string)
        return file.deleteRecursively()
    }

    override suspend fun moveIfExists(path: AbsolutePath, newPath: AbsolutePath): Boolean {
        val file = java.io.File(root, path.string)
        val newFile = java.io.File(root, newPath.string)
        return if(file.renameTo(newFile)){
            true
        } else {
            file.copyTo(newFile, true)
            file.delete()
        }
    }
}