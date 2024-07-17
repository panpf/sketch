package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.http.HttpStack
import java.io.InputStream

fun InputStream.content(): HttpStack.Content {
    return InputStreamContent(this)
}

class InputStreamContent(val inputStream: InputStream) : HttpStack.Content {

    override suspend fun read(buffer: ByteArray): Int {
        return inputStream.read(buffer)
    }

    override fun close() {
        inputStream.close()
    }
}