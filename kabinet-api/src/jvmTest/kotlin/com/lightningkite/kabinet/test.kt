package com.lightningkite.kabinet

import kotlinx.coroutines.runBlocking
import kotlinx.io.core.readText
import kotlinx.io.core.writeFully
import kotlinx.io.core.writeText
import kotlin.test.Test
import kotlin.test.assertEquals

class JavaFileSystemTest {
    @Test fun writeReadAll(){
        runBlocking {
            val fs = JavaFileSystem(java.io.File("./build/test"))
            val textFile = fs.root.child("test.txt")
            val content = "Test"
            textFile.overwriteAll(content.toByteArray())
            assertEquals(content, textFile.readAll()?.toString(Charsets.UTF_8))
        }
    }
    @Test fun writeRead(){
        runBlocking {
            val fs = JavaFileSystem(java.io.File("./build/test"))
            val textFile = fs.root.child("test.txt")
            val content = "Test"
            textFile.overwrite().use {
                it.writeText(content)
            }
            assertEquals(content, textFile.read()?.use {
                it.readText()
            })
        }
    }
}