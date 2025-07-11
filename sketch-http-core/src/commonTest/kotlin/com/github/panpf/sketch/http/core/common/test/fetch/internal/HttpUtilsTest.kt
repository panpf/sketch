package com.github.panpf.sketch.http.core.common.test.fetch.internal

import com.github.panpf.sketch.fetch.internal.copyToWithProgress
import com.github.panpf.sketch.fetch.internal.getMimeType
import com.github.panpf.sketch.images.content
import com.github.panpf.sketch.images.slow
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.utils.ProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.test.runTest
import okio.Buffer
import okio.buffer
import okio.use
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpUtilsTest {

    @Test
    fun testCopyToWithProgress() = runTest {
        val context = getTestContext()
        val logger = Logger()
        val string = "abcdefghijklmnopqrstuvwxyz"
        val progressListener = ProgressListenerSupervisor()

        progressListener.callbackActionList.clear()
        assertEquals(listOf(), progressListener.callbackActionList)
        val buffer = Buffer()
        buffer.use { sink ->
            Buffer().writeUtf8(string).slow(100).content().use { content ->
                copyToWithProgress(
                    coroutineScope = this@runTest,
                    logger = logger,
                    sink = sink,
                    content = content,
                    request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                        addProgressListener(progressListener)
                    },
                    contentLength = string.length.toLong(),
                    bufferSize = ceil(string.length / 3f).toInt(),
                )
            }
        }
        block(100)
        assertEquals(string, buffer.readByteArray().decodeToString())
        assertEquals(listOf("9", "26"), progressListener.callbackActionList)

        progressListener.callbackActionList.clear()
        assertEquals(listOf(), progressListener.callbackActionList)
        val buffer2 = Buffer()
        buffer2.use { sink ->
            Buffer().writeUtf8(string).slow(100).content().use { content ->
                copyToWithProgress(
                    coroutineScope = this@runTest,
                    logger = logger,
                    sink = sink,
                    content = content,
                    request = ImageRequest(context, "http://sample.com/sample.jpeg"),
                    contentLength = string.length.toLong(),
                    bufferSize = ceil(string.length / 3f).toInt(),
                )
            }
        }
        block(100)
        assertEquals(string, buffer2.readByteArray().decodeToString())
        assertEquals(listOf(), progressListener.callbackActionList)

        progressListener.callbackActionList.clear()
        assertEquals(listOf(), progressListener.callbackActionList)
        val buffer3 = Buffer()
        buffer3.use { sink ->
            Buffer().writeUtf8(string).slow(100).buffer().content().use { content ->
                copyToWithProgress(
                    coroutineScope = this@runTest,
                    logger = logger,
                    sink = sink,
                    content = content,
                    request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                        addProgressListener(progressListener)
                    },
                    bufferSize = ceil(string.length / 3f).toInt(),
                    contentLength = 0
                )
            }
        }
        block(100)
        assertEquals(string, buffer3.readByteArray().decodeToString())
        assertEquals(listOf(), progressListener.callbackActionList)

        progressListener.callbackActionList.clear()
        assertEquals(listOf(), progressListener.callbackActionList)
        val buffer4 = Buffer()
        buffer4.use { sink ->
            Buffer().writeUtf8(string).slow(readDelayMillis = 400).content().use { content ->
                copyToWithProgress(
                    coroutineScope = this@runTest,
                    logger = logger,
                    sink = sink,
                    content = content,
                    request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                        addProgressListener(progressListener)
                    },
                    bufferSize = ceil(string.length / 3f).toInt(),
                    contentLength = string.length.toLong()
                )
            }
        }
        block(100)
        assertEquals(string, buffer4.readByteArray().decodeToString())
        assertEquals(listOf("9", "18", "26"), progressListener.callbackActionList)
    }

    @Test
    fun testGetMimeType() {
        assertEquals(
            "image/jpeg",
            getMimeType("http://sample.com/sample.jpeg", null)
        )
        assertEquals(
            "image/jpeg",
            getMimeType("http://sample.com/sample.jpeg", "")
        )
        assertEquals(
            "image/jpeg",
            getMimeType("http://sample.com/sample.jpeg", " ")
        )

        assertEquals(
            "image/jpeg",
            getMimeType("http://sample.com/sample.jpeg", "text/plain; charset=utf-8")
        )
        assertEquals(
            "image/png",
            getMimeType("http://sample.com/sample.jpeg", "image/png")
        )
    }
}