package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.http.HttpStack
import okio.Buffer
import okio.use

suspend fun HttpStack.Response.readAllBytes(): ByteArray {
    val output = Buffer()
    val buffer = ByteArray(1024 * 8)
    this.content().use { content ->
        while (true) {
            val length = content.read(buffer)
            if (length != -1) {
                output.write(buffer, 0, length)
            } else {
                break
            }
        }
    }
    return output.readByteArray()
}