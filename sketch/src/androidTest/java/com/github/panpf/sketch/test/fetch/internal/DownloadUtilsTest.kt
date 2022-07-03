package com.github.panpf.sketch.test.fetch.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.internal.copyToWithActive
import com.github.panpf.sketch.fetch.internal.getMimeType
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.test.utils.DownloadProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.SlowInputStream
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import kotlin.math.ceil

@RunWith(AndroidJUnit4::class)
class DownloadUtilsTest {

    @Test
    fun testCopyToWithActive() {
        val context = getTestContext()
        val string = "abcdefghijklmnopqrstuvwxyz"
        val progressListener = DownloadProgressListenerSupervisor()

        progressListener.callbackActionList.clear()
        Assert.assertEquals(listOf<String>(), progressListener.callbackActionList)
        val outputStream = ByteArrayOutputStream()
        runBlocking {
            copyToWithActive(
                request = DownloadRequest(context, "http://sample.com/sample.jpeg") {
                    progressListener(progressListener)
                },
                inputStream = SlowInputStream(string.byteInputStream(), 100),
                outputStream = outputStream,
                bufferSize = ceil(string.length / 3f).toInt(),
                coroutineScope = this@runBlocking,
                contentLength = string.length.toLong()
            )
            delay(100)
        }
        Assert.assertEquals(outputStream.toString(), string)
        Assert.assertEquals(listOf("9", "26"), progressListener.callbackActionList)

        progressListener.callbackActionList.clear()
        Assert.assertEquals(listOf<String>(), progressListener.callbackActionList)
        val outputStream2 = ByteArrayOutputStream()
        runBlocking {
            copyToWithActive(
                request = DownloadRequest(context, "http://sample.com/sample.jpeg"),
                inputStream = SlowInputStream(string.byteInputStream(), 100),
                outputStream = outputStream2,
                bufferSize = ceil(string.length / 3f).toInt(),
                coroutineScope = this@runBlocking,
                contentLength = string.length.toLong()
            )
            delay(100)
        }
        Assert.assertEquals(outputStream2.toString(), string)
        Assert.assertEquals(listOf<String>(), progressListener.callbackActionList)

        progressListener.callbackActionList.clear()
        Assert.assertEquals(listOf<String>(), progressListener.callbackActionList)
        val outputStream3 = ByteArrayOutputStream()
        runBlocking {
            copyToWithActive(
                request = DownloadRequest(context, "http://sample.com/sample.jpeg") {
                    progressListener(progressListener)
                },
                inputStream = SlowInputStream(string.byteInputStream(), 100),
                outputStream = outputStream3,
                bufferSize = ceil(string.length / 3f).toInt(),
                coroutineScope = this@runBlocking,
                contentLength = 0
            )
            delay(100)
        }
        Assert.assertEquals(outputStream3.toString(), string)
        Assert.assertEquals(listOf<String>(), progressListener.callbackActionList)

        progressListener.callbackActionList.clear()
        Assert.assertEquals(listOf<String>(), progressListener.callbackActionList)
        val outputStream4 = ByteArrayOutputStream()
        runBlocking {
            copyToWithActive(
                request = DownloadRequest(context, "http://sample.com/sample.jpeg") {
                    progressListener(progressListener)
                },
                inputStream = SlowInputStream(string.byteInputStream(), 400),
                outputStream = outputStream4,
                bufferSize = ceil(string.length / 3f).toInt(),
                coroutineScope = this@runBlocking,
                contentLength = string.length.toLong()
            )
            delay(100)
        }
        Assert.assertEquals(outputStream4.toString(), string)
        Assert.assertEquals(listOf("9", "18", "26"), progressListener.callbackActionList)
    }

    @Test
    fun testGetMimeType() {
        Assert.assertEquals("image/jpeg", getMimeType("http://sample.com/sample.jpeg", null))
        Assert.assertEquals("image/jpeg", getMimeType("http://sample.com/sample.jpeg", ""))
        Assert.assertEquals("image/jpeg", getMimeType("http://sample.com/sample.jpeg", " "))

        Assert.assertEquals(
            "image/jpeg",
            getMimeType("http://sample.com/sample.jpeg", "text/plain; charset=utf-8")
        )
        Assert.assertEquals("image/png", getMimeType("http://sample.com/sample.jpeg", "image/png"))
    }
}