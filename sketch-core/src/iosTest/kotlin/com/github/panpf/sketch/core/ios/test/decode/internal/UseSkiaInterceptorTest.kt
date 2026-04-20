package com.github.panpf.sketch.core.ios.test.decode.internal

import com.github.panpf.sketch.decode.internal.UseSkiaInterceptor
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.fetch.parseLocalIdentifier
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.InterceptorChain
import com.github.panpf.sketch.request.preferredFileCacheForImagePhotosAsset
import com.github.panpf.sketch.request.useSkiaForImagePhotosAsset
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeInterceptor
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import platform.Photos.PHAsset
import platform.Photos.PHAssetResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UseSkiaInterceptorTest {

    @Test
    fun testConstructor() {
        UseSkiaInterceptor()
    }

    @Test
    fun testCompanion() {
        assertEquals(
            expected = 95,
            actual = UseSkiaInterceptor.SORT_WEIGHT
        )
    }

    @Test
    fun testSortWeight() {
        assertEquals(
            expected = 95,
            actual = UseSkiaInterceptor().sortWeight
        )
    }

    @Test
    fun testKey() {
        assertNull(actual = UseSkiaInterceptor().key)
    }

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val photosAssetDataSource = PhotosAssetDataSource(
            localIdentifier = parseLocalIdentifier(request.uri)!!,
            preferredThumbnail = true,
            networkAccessAllowed = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val fileDataSource = FileDataSource("/sdcard/test.jpg".toPath(), sketch.fileSystem)

        val interceptors = listOf(UseSkiaInterceptor(), FakeInterceptor())
        val executeRequest: suspend (ImageRequest, FetchResult) -> Result<ImageData> =
            { request, fetchResult ->
                withContext(Dispatchers.Main) {
                    val requestContext = request.toRequestContext(sketch)
                    requestContext.fetchResult = fetchResult
                    InterceptorChain(
                        requestContext = requestContext,
                        interceptors = interceptors,
                        index = 0,
                    ).proceed(request)
                }
            }

        executeRequest(
            request.newRequest { useSkiaForImagePhotosAsset() },
            FetchResult(photosAssetDataSource, "image/jpeg")
        ).apply {
            assertFalse(this.isSuccess)
            assertTrue(
                this.exceptionOrNull()!!.message!!
                    .contains("Failed get bytes for PHAssetResource")
            )
        }

        executeRequest(
            request.newRequest {
                useSkiaForImagePhotosAsset()
                preferredFileCacheForImagePhotosAsset()
            },
            FetchResult(photosAssetDataSource, "image/jpeg")
        ).apply {
            assertFalse(this.isSuccess)
            assertTrue(
                this.exceptionOrNull()!!.message!!
                    .contains("Failed to write PHAssetResource")
            )
        }

        executeRequest(
            request,
            FetchResult(photosAssetDataSource, "image/jpeg")
        ).apply {
            assertTrue(this.isSuccess)
        }

        executeRequest(
            request.newRequest { useSkiaForImagePhotosAsset() },
            FetchResult(photosAssetDataSource, "video/mp4")
        ).apply {
            assertTrue(this.isSuccess)
        }

        executeRequest(
            request.newRequest { useSkiaForImagePhotosAsset() },
            FetchResult(fileDataSource, "image/jpeg")
        ).apply {
            assertTrue(this.isSuccess)
        }

        executeRequest(
            request,
            FetchResult(photosAssetDataSource, "image/gif")
        ).apply {
            assertFalse(this.isSuccess)
        }

        executeRequest(
            request,
            FetchResult(photosAssetDataSource, "image/webp")
        ).apply {
            assertFalse(this.isSuccess)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = UseSkiaInterceptor()
        val element11 = UseSkiaInterceptor()
        val element2 = UseSkiaInterceptor()

        assertNotSame(illegal = element1, actual = element11)
        assertNotSame(illegal = element1, actual = element2)
        assertNotSame(illegal = element2, actual = element11)

        assertEquals(expected = element1, actual = element11)
        assertEquals(expected = element1, actual = element2)
        assertEquals(expected = element2, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertEquals(expected = element1.hashCode(), actual = element2.hashCode())
        assertEquals(expected = element2.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "UseSkiaInterceptor",
            actual = UseSkiaInterceptor().toString()
        )
    }
}