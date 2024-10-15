package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.http.HttpStack
import okio.Buffer
import okio.Source
import okio.Timeout

class SlowSource constructor(
    private val source: Source,
    val readDelayMillis: Long
) : Source {

    override fun close() {
        source.close()
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        val length = source.read(sink, byteCount)
        block(readDelayMillis)
        return length
    }

    override fun timeout(): Timeout {
        return source.timeout()
    }
}

fun Source.slow(readDelayMillis: Long): SlowSource {
    return SlowSource(this, readDelayMillis)
}

class SourceContent(private val source: Source) : HttpStack.Content {

    override suspend fun read(buffer: ByteArray): Int {
        val buffer1 = Buffer()
        val length = source.read(buffer1, buffer.size.toLong()).toInt()
        if (length > 0) {
            buffer1.read(buffer, 0, length)
        }
        return length
    }

    override fun close() {
        source.close()
    }
}

fun Source.content(): HttpStack.Content {
    return SourceContent(this)
}