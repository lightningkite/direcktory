package com.lightningkite.kabinet

import com.lightningkite.kommon.string.MediaType
import com.lightningkite.lokalize.time.TimeStamp

data class FileInformation(
        val fileSystem: FileSystem,
        val childrenPaths: List<AbsolutePath>?,
        val type: MediaType,
        val created: TimeStamp,
        val lastModified: TimeStamp,
        val readable: Boolean,
        val writable: Boolean,
        val length: Long
) {
    val isDirectory: Boolean get() = childrenPaths != null
    val isFile: Boolean get() = childrenPaths == null
}