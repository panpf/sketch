package com.github.panpf.sketch.video.ios.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.FileVideoFrameDecoder
import com.github.panpf.sketch.decode.supportFileVideoFrame
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FileVideoFrameDecoderTest {

    @Test
    fun testSupportPhotosAssetVideoFrame() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportFileVideoFrame()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[FileVideoFrameDecoder]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportFileVideoFrame()
            supportFileVideoFrame()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[FileVideoFrameDecoder,FileVideoFrameDecoder]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ComposeResImageFiles.svg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        FileVideoFrameDecoder(requestContext, dataSource, "video/mp4")
        FileVideoFrameDecoder(
            requestContext = requestContext,
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

        // [Test not completed] Because the test environment cannot access the kotlin resource files, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            FileVideoFrameDecoder(
                requestContext = request.toRequestContext(sketch),
                dataSource = dataSource,
                mimeType = "video/mp4"
            ).getImageInfo()
        }

        // [Test not completed] Because the test environment cannot access the kotlin resource files, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            FileVideoFrameDecoder(
                requestContext = request.newRequest { preferVideoCover() }.toRequestContext(sketch),
                dataSource = dataSource,
                mimeType = "video/mp4"
            ).getImageInfo()
        }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        // [Test not completed] Because the test environment cannot access the kotlin resource files, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            FileVideoFrameDecoder(
                requestContext = request.toRequestContext(sketch),
                dataSource = dataSource,
                mimeType = "video/mp4"
            ).decode()
        }

        // [Test not completed] Because the test environment cannot access the kotlin resource files, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            FileVideoFrameDecoder(
                requestContext = request.newRequest { preferVideoCover() }.toRequestContext(sketch),
                dataSource = dataSource,
                mimeType = "video/mp4"
            ).decode()
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val requestContext = request.toRequestContext(sketch)
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val element1 = FileVideoFrameDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        val element11 = FileVideoFrameDecoder(
            requestContext = requestContext,
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
        val requestContext = request.toRequestContext(sketch)
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )
        val decoder = FileVideoFrameDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertTrue(
            actual = decoder.toString().contains("FileVideoFrameDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        FileVideoFrameDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "FileVideoFrameDecoder",
            actual = FileVideoFrameDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = FileVideoFrameDecoder.Factory()

        val request = ImageRequest(context, "/sdcard/sample_rotation.mp4")
        val requestContext = request.toRequestContext(sketch)
        val dataSource = FileDataSource(
            path = "/sdcard/sample_rotation.mp4".toPath(),
            fileSystem = sketch.fileSystem,
        )

        assertNull(
            actual = factory.create(
                requestContext = requestContext,
                fetchResult = FetchResult(
                    dataSource = ByteArrayDataSource(
                        data = byteArrayOf(),
                        dataFrom = DataFrom.LOCAL
                    ),
                    mimeType = "video/mp4"
                )
            )
        )

        assertNull(
            actual = factory.create(
                requestContext = requestContext,
                fetchResult = FetchResult(
                    dataSource = dataSource,
                    mimeType = null
                )
            )
        )

        assertNotNull(
            actual = factory.create(
                requestContext = requestContext,
                fetchResult = FetchResult(
                    dataSource = dataSource,
                    mimeType = "video/mp4"
                )
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = FileVideoFrameDecoder.Factory()
        val element11 = FileVideoFrameDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "FileVideoFrameDecoder",
            actual = FileVideoFrameDecoder.Factory().toString()
        )
    }
}