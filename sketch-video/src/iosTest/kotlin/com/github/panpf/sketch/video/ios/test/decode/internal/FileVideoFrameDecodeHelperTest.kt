package com.github.panpf.sketch.video.ios.test.decode.internal

import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.internal.FileVideoFrameDecodeHelper
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.preferVideoCover
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

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        FileVideoFrameDecodeHelper(request, dataSource, "video/mp4")
        FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decodeHelper.imageInfo
        }

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper2 = FileVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decodeHelper2.imageInfo
        }
    }

    @Test
    fun testSupportRegion() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFalse(decodeHelper.supportRegion)

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper2 = FileVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFalse(decodeHelper2.supportRegion)
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decodeHelper.decode(1)
        }

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper2 = FileVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decodeHelper2.decode(1)
        }
    }

    @Test
    fun testDecodeRegion() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(UnsupportedOperationException::class) {
            decodeHelper.decodeRegion(Rect(100, 200, 200, 100), 1)
        }

        // [Test not completed] Because the test environment cannot access the kotlin resource file, the test cannot be completed.
        val decodeHelper2 = FileVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(UnsupportedOperationException::class) {
            decodeHelper2.decodeRegion(Rect(100, 200, 200, 100), 1)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val element1 = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        val element11 = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val decoderHelper = FileVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertTrue(
            actual = decoderHelper.toString().contains("FileVideoFrameDecodeHelper"),
            message = decoderHelper.toString()
        )
        assertTrue(
            actual = decoderHelper.toString().contains("@"),
            message = decoderHelper.toString()
        )
    }
}