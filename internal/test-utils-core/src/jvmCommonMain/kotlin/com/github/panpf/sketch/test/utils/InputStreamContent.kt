package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.http.HttpStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

fun InputStream.content(): HttpStack.Content {
    return InputStreamContent(this)
}

class InputStreamContent(val inputStream: InputStream) : HttpStack.Content {
    override suspend fun read(buffer: ByteArray): Int {
        val result = withContext(Dispatchers.IO) {
            runCatching {
                inputStream.read(buffer)
            }
        }
        if (result.isSuccess) {
            return result.getOrNull()!!
        } else {
            throw result.exceptionOrNull()!!
        }
    }

    override fun close() {
        inputStream.close()
    }
}