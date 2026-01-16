package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.block
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.DecoderInterceptor
import com.github.panpf.sketch.request.internal.ThumbnailInterceptor
import com.github.panpf.sketch.request.internal.ThumbnailInterceptor.Companion.KEY_THUMBNAIL
import com.github.panpf.sketch.request.internal.ThumbnailTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.BlockInterceptor
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestListener
import com.github.panpf.sketch.test.utils.TestProgressListener
import com.github.panpf.sketch.test.utils.TestTarget2
import com.github.panpf.sketch.test.utils.current
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ThumbnailInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        if (Platform.current == Platform.iOS) {
            // Will get stuck forever in iOS test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()

        val executeRequest: suspend (ImageRequest) -> Image = { request ->
            val result = sketch.execute(request)
            if (result is ImageResult.Success) {
                result.image
            } else {
                throw IllegalStateException("Request failed: $result")
            }
        }

        val target = TestTarget2(
            _imageOptions = ImageOptions {
                memoryCacheKey("memoryCacheKey1")
                resultCacheKey("resultCacheKey1")
                downloadCacheKey("downloadCacheKey1")
            },
            _listener = TestListener(),
            _progressListener = TestProgressListener(),
        )
        assertEquals(0, target.successImages.size)

        // No thumbnails.
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(CachePolicy.DISABLED)
            resultCachePolicy(CachePolicy.DISABLED)
            downloadCachePolicy(CachePolicy.DISABLED)
            placeholder(FakeStateImage())
            target(target)
            addListener(TestListener())
            addProgressListener(TestProgressListener())
        }
        executeRequest(request)
        block(1000)
        assertEquals(expected = 1, actual = target.startImages.size)
        assertEquals(expected = 1, actual = target.successImages.size)
        assertEquals(expected = 0, actual = target.errorImages.size)

        // No target.
        target.clearImages()
        executeRequest(request.newRequest {
            thumbnail(request.newRequest(ResourceImages.png.uri))
            target(null)
        })
        block(1000)
        assertEquals(expected = 0, actual = target.startImages.size)
        assertEquals(expected = 0, actual = target.successImages.size)
        assertEquals(expected = 0, actual = target.errorImages.size)

        // Thumbnails loaded quickly.
        target.clearImages()
        executeRequest(request.newRequest {
            thumbnail(request.newRequest(ResourceImages.png.uri))
            components {
                add(
                    BlockInterceptor(
                        blockMillis = 2000,
                        sortWeight = DecoderInterceptor.SORT_WEIGHT - 1
                    )
                )
            }
        })
        block(1000)
        assertEquals(expected = 1, actual = target.startImages.size)
        assertEquals(expected = 2, actual = target.successImages.size)
        assertEquals(expected = 0, actual = target.errorImages.size)

        // Thumbnails loaded slowly and were cancelled.
        target.clearImages()
        executeRequest(request.newRequest {
            thumbnail(request.newRequest(ResourceImages.png.uri) {
                components {
                    add(
                        BlockInterceptor(
                            blockMillis = 2000,
                            sortWeight = DecoderInterceptor.SORT_WEIGHT - 1
                        )
                    )
                }
            })
        })
        block(1000)
        assertEquals(expected = 1, actual = target.startImages.size)
        assertEquals(expected = 1, actual = target.successImages.size)
        assertEquals(expected = 0, actual = target.errorImages.size)

        // New ImageRequest for uri
        target.clearImages()
        val thumbnailTestInterceptor = ThumbnailTestInterceptor()
        executeRequest(request.newRequest {
            thumbnail(ResourceImages.png.uri)
            components {
                add(
                    BlockInterceptor(
                        blockMillis = 2000,
                        sortWeight = DecoderInterceptor.SORT_WEIGHT - 1
                    )
                )
                add(thumbnailTestInterceptor)
            }
        })
        block(1000)
        assertEquals(expected = 1, actual = target.startImages.size)
        assertEquals(expected = 2, actual = target.successImages.size)
        assertEquals(expected = 0, actual = target.errorImages.size)
        val originRequest = thumbnailTestInterceptor.originRequest
        val thumbnailRequest = thumbnailTestInterceptor.thumbnailRequest
        assertNotNull(originRequest)
        assertNotNull(thumbnailRequest)

        assertNotNull(originRequest.placeholder)
        assertNotNull(originRequest.memoryCacheKey)
        assertNotNull(originRequest.resultCacheKey)
        assertNotNull(originRequest.downloadCacheKey)
        assertNotNull(originRequest.extras?.get(KEY_THUMBNAIL))
        assertNotNull(originRequest.listener)
        assertNotNull(originRequest.progressListener)
        assertTrue(originRequest.target !is ThumbnailTarget)

        assertNull(thumbnailRequest.placeholder)
        assertNull(thumbnailRequest.memoryCacheKey)
        assertNull(thumbnailRequest.resultCacheKey)
        assertNull(thumbnailRequest.downloadCacheKey)
        assertNull(thumbnailRequest.extras?.get(KEY_THUMBNAIL))
        assertNull(thumbnailRequest.listener)
        assertNull(thumbnailRequest.progressListener)
        assertTrue(thumbnailRequest.target is ThumbnailTarget)

        // New ImageRequest for request
        target.clearImages()
        val thumbnailTestInterceptor2 = ThumbnailTestInterceptor()
        executeRequest(request.newRequest {
            thumbnail(ImageRequest(context, ResourceImages.png.uri) {
                components {
                    add(thumbnailTestInterceptor2)
                }
            })
            components {
                add(
                    BlockInterceptor(
                        blockMillis = 2000,
                        sortWeight = DecoderInterceptor.SORT_WEIGHT - 1
                    )
                )
                add(thumbnailTestInterceptor2)
            }
        })
        block(1000)
        assertEquals(expected = 1, actual = target.startImages.size)
        assertEquals(expected = 2, actual = target.successImages.size)
        assertEquals(expected = 0, actual = target.errorImages.size)
        val originRequest2 = thumbnailTestInterceptor2.originRequest
        val thumbnailRequest2 = thumbnailTestInterceptor2.thumbnailRequest
        assertNotNull(originRequest2)
        assertNotNull(thumbnailRequest2)

        assertNotNull(originRequest2.placeholder)
        assertNotNull(originRequest2.memoryCacheKey)
        assertNotNull(originRequest2.resultCacheKey)
        assertNotNull(originRequest2.downloadCacheKey)
        assertNotNull(originRequest2.extras?.get(KEY_THUMBNAIL))
        assertNotNull(originRequest2.listener)
        assertNotNull(originRequest2.progressListener)
        assertTrue(originRequest2.target !is ThumbnailTarget)

        assertNull(thumbnailRequest2.placeholder)
        assertNull(thumbnailRequest2.memoryCacheKey)
        assertNull(thumbnailRequest2.resultCacheKey)
        assertNull(thumbnailRequest2.downloadCacheKey)
        assertNull(thumbnailRequest2.extras?.get(KEY_THUMBNAIL))
        assertNull(thumbnailRequest2.listener)
        assertNull(thumbnailRequest2.progressListener)
        assertTrue(thumbnailRequest2.target is ThumbnailTarget)

        // New ImageRequest for request. Inherit the cache key, listener, progressListener and other configurations of the original request
        target.clearImages()
        val thumbnailTestInterceptor3 = ThumbnailTestInterceptor()
        executeRequest(request.newRequest {
            thumbnail(request.newRequest(ResourceImages.png.uri) {
                components {
                    add(thumbnailTestInterceptor3)
                }
            })
            components {
                add(
                    BlockInterceptor(
                        blockMillis = 2000,
                        sortWeight = DecoderInterceptor.SORT_WEIGHT - 1
                    )
                )
                add(thumbnailTestInterceptor3)
            }
        })
        block(1000)
        assertEquals(expected = 1, actual = target.startImages.size)
        assertEquals(expected = 2, actual = target.successImages.size)
        assertEquals(expected = 0, actual = target.errorImages.size)
        val originRequest3 = thumbnailTestInterceptor3.originRequest
        val thumbnailRequest3 = thumbnailTestInterceptor3.thumbnailRequest
        assertNotNull(originRequest3)
        assertNotNull(thumbnailRequest3)

        assertNotNull(originRequest3.placeholder)
        assertNotNull(originRequest3.memoryCacheKey)
        assertNotNull(originRequest3.resultCacheKey)
        assertNotNull(originRequest3.downloadCacheKey)
        assertNotNull(originRequest3.extras?.get(KEY_THUMBNAIL))
        assertNotNull(originRequest3.listener)
        assertNotNull(originRequest3.progressListener)
        assertTrue(originRequest3.target !is ThumbnailTarget)

        assertNull(thumbnailRequest3.placeholder)
        assertNotNull(thumbnailRequest3.memoryCacheKey)
        assertNotNull(thumbnailRequest3.resultCacheKey)
        assertNotNull(thumbnailRequest3.downloadCacheKey)
        assertNull(thumbnailRequest3.extras?.get(KEY_THUMBNAIL))
        assertNotNull(thumbnailRequest3.listener)
        assertNotNull(thumbnailRequest3.progressListener)
        assertTrue(thumbnailRequest3.target is ThumbnailTarget)
    }

//    @Test
//    fun testThumbnailRequest() = runTest {
//        val (context, sketch) = getTestContextAndSketch()
//
//        val request = ImageRequest(context, ResourceImages.jpeg.uri).apply {
//            assertFalse(isThumbnailRequest())
//        }
//        val requestContext = request.toRequestContext(sketch)
//
//        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
//            markThumbnailRequest()
//        }.apply {
//            assertTrue(isThumbnailRequest())
//        }
//        val requestContext1 = request1.toRequestContext(sketch)
//
//        request1.newRequest {
//
//        }.apply {
//            assertTrue(isThumbnailRequest())
//        }.newRequest {
//            markThumbnailRequest(false)
//        }.apply {
//            assertFalse(isThumbnailRequest())
//        }
//
//        assertEquals(requestContext.memoryCacheKey, requestContext1.memoryCacheKey)
//        assertEquals(requestContext.resultCacheKey, requestContext1.resultCacheKey)
//        assertEquals(requestContext.downloadCacheKey, requestContext1.downloadCacheKey)
//        assertEquals(request.key, request1.key)
//    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ThumbnailInterceptor()
        val element11 = ThumbnailInterceptor()
        val element2 = ThumbnailInterceptor()

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
    fun testSortWeight() {
        assertEquals(
            expected = 60,
            actual = ThumbnailInterceptor().sortWeight
        )
        assertEquals(
            expected = 60,
            actual = ThumbnailInterceptor.SORT_WEIGHT
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ThumbnailInterceptor",
            actual = ThumbnailInterceptor().toString()
        )
    }

    class ThumbnailTestInterceptor : Interceptor {
        override val key: String? = null
        override val sortWeight: Int = 0

        var originRequest: ImageRequest? = null
        var thumbnailRequest: ImageRequest? = null

        override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
            val request = chain.request
            if (request.target is ThumbnailTarget) {
                thumbnailRequest = request
            } else {
                originRequest = request
            }
            return chain.proceed(chain.request)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "ThumbnailTestInterceptor"

    }
}