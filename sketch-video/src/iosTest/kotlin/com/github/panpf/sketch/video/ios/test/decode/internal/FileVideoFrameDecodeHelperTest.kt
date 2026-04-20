package com.github.panpf.sketch.video.ios.test.decode.internal

import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.internal.FileVideoFrameDecodeHelper
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.Rect
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FileVideoFrameDecodeHelperTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.jpeg")
        val dataSource = FileDataSource(
            path = "/sdcard/sample.jpeg".toPath(),
            fileSystem = sketch.fileSystem,
        )

        FileVideoFrameDecodeHelper(request, dataSource, "image/jpeg")
        FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.jpeg")
        val dataSource = FileDataSource(
            path = "/sdcard/sample.jpeg".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val decoder = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            decoder.imageInfo
        }
    }

    @Test
    fun testSupportRegion() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.jpeg")
        val dataSource = FileDataSource(
            path = "/sdcard/sample.jpeg".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val decoder = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        assertFalse(decoder.supportRegion)
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.jpeg")
        val dataSource = FileDataSource(
            path = "/sdcard/sample.jpeg".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val decoder = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            decoder.decode(1)
        }
    }

    @Test
    fun testDecodeRegion() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.jpeg")
        val dataSource = FileDataSource(
            path = "/sdcard/sample.jpeg".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val decoder = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        assertFailsWith(UnsupportedOperationException::class) {
            decoder.decodeRegion(Rect(100, 200, 200, 100), 1)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.jpeg")
        val dataSource = FileDataSource(
            path = "/sdcard/sample.jpeg".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val element1 = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        val element11 = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.jpeg")
        val dataSource = FileDataSource(
            path = "/sdcard/sample.jpeg".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val decoder = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        assertTrue(
            actual = decoder.toString().contains("FileVideoFrameDecodeHelper"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }
}