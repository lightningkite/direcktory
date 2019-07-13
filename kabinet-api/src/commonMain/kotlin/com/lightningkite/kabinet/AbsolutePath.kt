package com.lightningkite.kabinet

import com.lightningkite.kommon.string.BackedByString

inline class AbsolutePath(override val string: String) : BackedByString {
    fun standardize(): AbsolutePath = AbsolutePath(string.replace('\\', '/').trim('/'))
    fun child(part: String): AbsolutePath = if (string.endsWith('/')) AbsolutePath(string + part) else AbsolutePath("$string/$part")
    fun parent(): AbsolutePath {
        val slash = string.lastIndexOf('/', string.lastIndex - 1)
        return AbsolutePath(string.substring(0, slash))
    }
}