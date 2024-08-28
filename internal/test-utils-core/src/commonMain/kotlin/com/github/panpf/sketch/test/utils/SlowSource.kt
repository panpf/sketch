package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.http.HttpStack
import okio.Buffer
import okio.Source
import okio.Timeout
import kotlin.time.TimeSource

class SlowSource(
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

fun Source.content(): HttpStack.Content {
    return SourceContent(this)
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

//fun BufferedSource.content(): HttpStack.Content {
//    return BufferedSourceContent(this)
//}
//
//class BufferedSourceContent(private val source: BufferedSource) : HttpStack.Content {
//
//    override suspend fun read(buffer: ByteArray): Int {
//        return source.read(buffer)
//    }
//
//    override fun close() {
//        source.close()
//    }
//}

fun block(millis: Long) {
    if (millis > 0) {
        val startTime = TimeSource.Monotonic.markNow()
        while (startTime.elapsedNow().inWholeMilliseconds < millis) {
            // Do nothing
        }
    }
}