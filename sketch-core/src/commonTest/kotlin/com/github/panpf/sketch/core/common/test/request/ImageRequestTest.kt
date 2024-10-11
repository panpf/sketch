/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.Extras
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.Listeners
import com.github.panpf.sketch.request.internal.PairListener
import com.github.panpf.sketch.request.internal.PairProgressListener
import com.github.panpf.sketch.request.internal.ProgressListeners
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.FakeTransition
import com.github.panpf.sketch.test.utils.ScopeAction
import com.github.panpf.sketch.test.utils.TestComponentsTarget
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestFetcher2
import com.github.panpf.sketch.test.utils.TestImageOptionsTarget
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.TestLifecycleTarget
import com.github.panpf.sketch.test.utils.TestListenerTarget
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestRequestInterceptor2
import com.github.panpf.sketch.test.utils.TestScaleResolverTarget
import com.github.panpf.sketch.test.utils.TestSizeResolverTarget
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.target
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.application
import com.github.panpf.sketch.util.screenSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ImageRequestTest {

    @Test
    fun testImageRequest() {
        val context1 = getTestContext()
        val uri1 = ResourceImages.jpeg.uri
        ImageRequest(context1, uri1).apply {
            assertEquals(context1, this.context)
            assertEquals(context1.application, this.context)
            assertEquals(ResourceImages.jpeg.uri, uri.toString())
            assertNull(this.listener)
            assertNull(this.progressListener)
            assertNull(this.target)
            assertEquals(LifecycleResolver(GlobalLifecycle), this.lifecycleResolver)
            assertEquals(NETWORK, this.depthHolder.depth)
            assertNull(this.extras)
            assertNull(this.httpHeaders)
            assertEquals(ENABLED, this.downloadCachePolicy)
            assertEquals(SizeResolver(context1.screenSize()), this.sizeResolver)
            assertEquals(FixedPrecisionDecider(LESS_PIXELS), this.precisionDecider)
            assertEquals(FixedScaleDecider(CENTER_CROP), this.scaleDecider)
            assertNull(this.transformations)
            assertEquals(ENABLED, this.resultCachePolicy)
            assertNull(this.placeholder)
            assertNull(this.fallback)
            assertNull(this.error)
            assertNull(this.transitionFactory)
            assertFalse(this.disallowAnimatedImage)
            assertFalse(this.resizeOnDraw ?: false)
            assertEquals(ENABLED, this.memoryCachePolicy)
        }
    }

    @Test
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    fun testNewBuilder() {
        val context1 = getTestContext()
        val jpegUri = ResourceImages.jpeg.uri

        ImageRequest(context1, jpegUri).apply {
            assertEquals(expected = jpegUri, actual = this.uri.toString())
        }.newBuilder().build().apply {
            assertEquals(expected = jpegUri, actual = this.uri.toString())
        }.newBuilder(uri = ResourceImages.png.uri).build().apply {
            assertEquals(expected = ResourceImages.png.uri, actual = this.uri.toString())
        }

        ImageRequest(context1, jpegUri).apply {
            assertEquals(NETWORK, depthHolder.depth)
        }.newBuilder().build().apply {
            assertEquals(NETWORK, depthHolder.depth)
        }.newBuilder {
            depth(LOCAL)
        }.build().apply {
            assertEquals(LOCAL, depthHolder.depth)
        }

        ImageRequest(context1, jpegUri).apply {
            assertNull(listener)
            assertNull(progressListener)
        }.newBuilder().build().apply {
            assertNull(listener)
            assertNull(progressListener)
        }.newBuilder {
            registerListener(
                onStart = { request: ImageRequest ->

                },
                onCancel = { request: ImageRequest ->

                },
                onError = { request: ImageRequest, result: ImageResult.Error ->

                },
                onSuccess = { request: ImageRequest, result: ImageResult.Success ->

                },
            )
            registerProgressListener { _, _ ->

            }
        }.build().apply {
            assertNotNull(listener)
            assertNotNull(progressListener)
        }
    }

    @Test
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    fun testNewRequest() {
        val context1 = getTestContext()
        val jpegUri = ResourceImages.jpeg.uri

        ImageRequest(context1, jpegUri).apply {
            assertEquals(expected = jpegUri, actual = this.uri.toString())
        }.newRequest().apply {
            assertEquals(expected = jpegUri, actual = this.uri.toString())
        }.newRequest(uri = ResourceImages.png.uri).apply {
            assertEquals(expected = ResourceImages.png.uri, actual = this.uri.toString())
        }

        ImageRequest(context1, jpegUri).apply {
            assertEquals(NETWORK, depthHolder.depth)
        }.newRequest().apply {
            assertEquals(NETWORK, depthHolder.depth)
        }.newRequest {
            depth(LOCAL)
        }.apply {
            assertEquals(LOCAL, depthHolder.depth)
        }

        ImageRequest(context1, jpegUri).apply {
            assertNull(listener)
            assertNull(progressListener)
        }.newRequest().apply {
            assertNull(listener)
            assertNull(progressListener)
        }.newRequest {
            registerListener(
                onStart = { request: ImageRequest ->

                },
                onCancel = { request: ImageRequest ->

                },
                onError = { request: ImageRequest, result: ImageResult.Error ->

                },
                onSuccess = { request: ImageRequest, result: ImageResult.Success ->

                },
            )
            registerProgressListener { _, _ ->

            }
        }.apply {
            assertNotNull(listener)
            assertNotNull(progressListener)
        }
    }

    @Test
    fun testContext() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            assertEquals(context1, context)
            assertEquals(context1.application, context)
        }
    }

    @Test
    fun testTarget() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri

        ImageRequest(context1, uri).apply {
            assertNull(target)
        }

        ImageRequest(context1, uri) {
            target(TestTarget())
        }.apply {
            assertTrue(target is TestTarget)
        }

        ImageRequest(context1, uri) {
            target(
                onStart = { _, _, _ -> },
                onSuccess = { _, _, _ -> },
                onError = { _, _, _ -> }
            )
        }.apply {
            assertNotNull(target)
            assertEquals(ENABLED, memoryCachePolicy)
        }
        ImageRequest(context1, uri) {
            target(onStart = { _, _, _ -> })
        }.apply {
            assertNotNull(target)
            assertEquals(ENABLED, memoryCachePolicy)
        }
        ImageRequest(context1, uri) {
            target(onSuccess = { _, _, _ -> })
        }.apply {
            assertNotNull(target)
            assertEquals(ENABLED, memoryCachePolicy)
        }
        ImageRequest(context1, uri) {
            target(onError = { _, _, _ -> })
        }.apply {
            assertNotNull(target)
            assertEquals(ENABLED, memoryCachePolicy)
        }
    }

    @Test
    fun testLifecycle() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        val testLifecycle = TestLifecycle()
        val testLifecycle2 = TestLifecycle()

        ImageRequest(context1, uri).apply {
            assertEquals(
                expected = LifecycleResolver(GlobalLifecycle),
                actual = this.lifecycleResolver
            )
            assertNull(target)
        }

        ImageRequest(context1, uri) {
            lifecycle(testLifecycle)
        }.apply {
            assertEquals(
                expected = LifecycleResolver(testLifecycle),
                actual = this.lifecycleResolver
            )
            assertNull(target)
        }

        ImageRequest(context1, uri) {
            lifecycle(LifecycleResolver(testLifecycle2))
        }.apply {
            assertEquals(
                expected = LifecycleResolver(testLifecycle2),
                actual = this.lifecycleResolver
            )
            assertNull(target)
        }

        val testLifecycle3 = TestLifecycle()
        val testLifecycleTarget = TestLifecycleTarget(testLifecycle3)
        ImageRequest(context1, uri) {
            target(testLifecycleTarget)
        }.apply {
            assertEquals(
                expected = LifecycleResolver(testLifecycle3),
                actual = this.lifecycleResolver
            )
            assertSame(expected = testLifecycleTarget, actual = target)
        }
    }

    @Test
    fun testDefinedOptions() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri

        ImageRequest(context1, uri).apply {
            assertEquals(ImageOptions(), definedOptions)
        }

        ImageRequest(context1, uri) {
            size(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }.apply {
            assertEquals(ImageOptions {
                size(100, 50)
                addTransformations(CircleCropTransformation())
                crossfade()
            }, definedOptions)
        }
    }

    @Test
    fun testDefault() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri

        ImageRequest(context1, uri).apply {
            assertNull(defaultOptions)
        }

        val options = ImageOptions {
            size(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }
        ImageRequest(context1, uri) {
            defaultOptions(options)
        }.apply {
            assertSame(options, defaultOptions)
        }

        ImageRequest(context1, uri) {
            memoryCachePolicy(DISABLED)
        }.apply {
            assertEquals(DISABLED, memoryCachePolicy)
            assertEquals(ENABLED, resultCachePolicy)
        }

        ImageRequest(context1, uri) {
            memoryCachePolicy(DISABLED)
            target(TestImageOptionsTarget(ImageOptions() {
                memoryCachePolicy(READ_ONLY)
                resultCachePolicy(WRITE_ONLY)
            }))
        }.apply {
            assertEquals(DISABLED, memoryCachePolicy)
            assertEquals(WRITE_ONLY, resultCachePolicy)
        }
    }

    @Test
    fun testMerge() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertEquals(NETWORK, depthHolder.depth)
                assertNull(extras)
            }

            merge(ImageOptions {
                size(100, 50)
                memoryCachePolicy(DISABLED)
                addTransformations(CircleCropTransformation())
                crossfade()
            })
            build().apply {
                assertEquals(FixedSizeResolver(100, 50), sizeResolver)
                assertEquals(DISABLED, memoryCachePolicy)
                assertEquals(listOf(CircleCropTransformation()), transformations)
                assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }

            merge(ImageOptions {
                memoryCachePolicy(READ_ONLY)
            })
            build().apply {
                assertEquals(FixedSizeResolver(100, 50), sizeResolver)
                assertEquals(DISABLED, memoryCachePolicy)
                assertEquals(listOf(CircleCropTransformation()), transformations)
                assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }
        }
    }

    @Test
    fun testDepth() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            assertEquals(NETWORK, depthHolder.depth)
            assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(LOCAL)
        }.apply {
            assertEquals(LOCAL, depthHolder.depth)
            assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(null)
        }.apply {
            assertEquals(NETWORK, depthHolder.depth)
            assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(LOCAL, null)
        }.apply {
            assertEquals(LOCAL, depthHolder.depth)
            assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(null, "TestDepthFrom")
        }.apply {
            assertEquals(NETWORK, depthHolder.depth)
            assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(LOCAL, "TestDepthFrom")
        }.apply {
            assertEquals(LOCAL, depthHolder.depth)
            assertEquals("TestDepthFrom", depthHolder.from)
        }
    }

    @Test
    fun testExtras() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(extras)
            }

            /* extras() */
            extras(Extras())
            build().apply {
                assertNull(extras)
            }

            extras(Extras.Builder().set("key1", "value1").build())
            build().apply {
                assertEquals(1, extras?.size)
                assertEquals("value1", extras?.get("key1"))
            }

            extras(null)
            build().apply {
                assertNull(extras)
            }

            /* setParameter(), removeParameter() */
            setExtra("key1", "value1")
            setExtra("key2", "value2", "value2")
            build().apply {
                assertEquals(2, extras?.size)
                assertEquals("value1", extras?.get("key1"))
                assertEquals("value2", extras?.get("key2"))
            }

            setExtra("key2", "value2.1", null)
            build().apply {
                assertEquals(2, extras?.size)
                assertEquals("value1", extras?.get("key1"))
                assertEquals("value2.1", extras?.get("key2"))
            }

            removeExtra("key2")
            build().apply {
                assertEquals(1, extras?.size)
                assertEquals("value1", extras?.get("key1"))
            }

            removeExtra("key1")
            build().apply {
                assertNull(extras)
            }
        }
    }

    @Test
    fun testHttpHeaders() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(httpHeaders)
            }

            /* httpHeaders() */
            httpHeaders(HttpHeaders())
            build().apply {
                assertNull(httpHeaders)
            }

            httpHeaders(HttpHeaders.Builder().set("key1", "value1").build())
            build().apply {
                assertEquals(1, httpHeaders?.size)
                assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            httpHeaders(null)
            build().apply {
                assertNull(httpHeaders)
            }

            /* httpHeader(), addHttpHeader(), removeHttpHeader() */
            httpHeader("key1", "value1")
            httpHeader("key2", "value2")
            addHttpHeader("key3", "value3")
            addHttpHeader("key3", "value3.1")
            build().apply {
                assertEquals(4, httpHeaders?.size)
                assertEquals(2, httpHeaders?.setSize)
                assertEquals(2, httpHeaders?.addSize)
                assertEquals("value1", httpHeaders?.getSet("key1"))
                assertEquals("value2", httpHeaders?.getSet("key2"))
                assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            httpHeader("key2", "value2.1")
            build().apply {
                assertEquals(4, httpHeaders?.size)
                assertEquals(2, httpHeaders?.setSize)
                assertEquals(2, httpHeaders?.addSize)
                assertEquals("value1", httpHeaders?.getSet("key1"))
                assertEquals("value2.1", httpHeaders?.getSet("key2"))
                assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            removeHttpHeader("key3")
            build().apply {
                assertEquals(2, httpHeaders?.size)
                assertEquals("value1", httpHeaders?.getSet("key1"))
                assertEquals("value2.1", httpHeaders?.getSet("key2"))
            }

            removeHttpHeader("key2")
            build().apply {
                assertEquals(1, httpHeaders?.size)
                assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            removeHttpHeader("key1")
            build().apply {
                assertNull(httpHeaders)
            }
        }
    }

    @Test
    fun testDownloadCachePolicy() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertEquals(ENABLED, downloadCachePolicy)
            }

            downloadCachePolicy(READ_ONLY)
            build().apply {
                assertEquals(READ_ONLY, downloadCachePolicy)
            }

            downloadCachePolicy(DISABLED)
            build().apply {
                assertEquals(DISABLED, downloadCachePolicy)
            }

            downloadCachePolicy(null)
            build().apply {
                assertEquals(ENABLED, downloadCachePolicy)
            }
        }
    }

    @Test
    fun testColorType() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(colorType)
            }

            colorType(LowQualityColorType)
            build().apply {
                assertEquals(LowQualityColorType, colorType)
            }

            colorType(HighQualityColorType)
            build().apply {
                assertEquals(HighQualityColorType, colorType)
            }

            colorType("ARGB_8888")
            build().apply {
                assertEquals(BitmapColorType("ARGB_8888"), colorType)
            }

            colorType(null as BitmapColorType?)
            build().apply {
                assertNull(colorType)
            }
        }
    }

    @Test
    fun testColorSpace() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(colorSpace)
            }

            colorSpace(BitmapColorSpace("ACES"))
            build().apply {
                assertEquals(BitmapColorSpace("ACES"), colorSpace)
            }

            colorSpace("BT709")
            build().apply {
                assertEquals(BitmapColorSpace("BT709"), colorSpace)
            }

            colorSpace(null as BitmapColorSpace?)
            build().apply {
                assertNull(colorSpace)
            }
        }
    }

    @Test
    fun testResize() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(definedOptions.sizeResolver)
                assertNull(definedOptions.precisionDecider)
                assertNull(definedOptions.scaleDecider)
                assertEquals(SizeResolver(context1.screenSize()), sizeResolver)
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    definedOptions.precisionDecider
                )
                assertEquals(
                    FixedScaleDecider(START_CROP),
                    definedOptions.scaleDecider
                )
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(100, 100)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                assertNull(definedOptions.precisionDecider)
                assertNull(definedOptions.scaleDecider)
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, EXACTLY)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                assertEquals(
                    FixedPrecisionDecider(EXACTLY),
                    definedOptions.precisionDecider
                )
                assertNull(definedOptions.scaleDecider)
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, scale = END_CROP)
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                assertNull(definedOptions.precisionDecider)
                assertEquals(FixedScaleDecider(END_CROP), definedOptions.scaleDecider)
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                assertNull(definedOptions.sizeResolver)
                assertNull(definedOptions.precisionDecider)
                assertNull(definedOptions.scaleDecider)
                assertEquals(SizeResolver(context1.screenSize()), sizeResolver)
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    definedOptions.precisionDecider
                )
                assertEquals(
                    FixedScaleDecider(START_CROP),
                    definedOptions.scaleDecider
                )
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(Size(100, 100))
            build().apply {
                assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                assertNull(definedOptions.precisionDecider)
                assertNull(definedOptions.scaleDecider)
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), EXACTLY)
            build().apply {
                assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                assertEquals(
                    FixedPrecisionDecider(EXACTLY),
                    definedOptions.precisionDecider
                )
                assertNull(definedOptions.scaleDecider)
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), scale = END_CROP)
            build().apply {
                assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                assertNull(definedOptions.precisionDecider)
                assertEquals(FixedScaleDecider(END_CROP), definedOptions.scaleDecider)
                assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                assertNull(definedOptions.sizeResolver)
                assertNull(definedOptions.precisionDecider)
                assertNull(definedOptions.scaleDecider)
                assertEquals(SizeResolver(context1.screenSize()), sizeResolver)
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }
        }
    }

    @Test
    fun testSize() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(definedOptions.sizeResolver)
                assertEquals(SizeResolver(context1.screenSize()), sizeResolver)
            }

            size(Size(100, 100))
            build().apply {
                assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                assertEquals(FixedSizeResolver(100, 100), sizeResolver)
            }

            size(200, 200)
            build().apply {
                assertEquals(FixedSizeResolver(200, 200), definedOptions.sizeResolver)
                assertEquals(FixedSizeResolver(200, 200), sizeResolver)
            }

            size(FixedSizeResolver(300, 200))
            build().apply {
                assertEquals(FixedSizeResolver(300, 200), definedOptions.sizeResolver)
                assertEquals(FixedSizeResolver(300, 200), sizeResolver)
            }

            size(null)
            build().apply {
                assertNull(definedOptions.sizeResolver)
                assertEquals(SizeResolver(context1.screenSize()), sizeResolver)
            }

            size(null)
            target(TestSizeResolverTarget(Size(101, 202)))
            build().apply {
                assertNull(definedOptions.sizeResolver)
                assertEquals(SizeResolver(Size(101, 202)), sizeResolver)
            }

            size(Size(1000, 1000))
            target(TestSizeResolverTarget(Size(101, 202)))
            build().apply {
                assertEquals(SizeResolver(Size(1000, 1000)), definedOptions.sizeResolver)
                assertEquals(SizeResolver(Size(1000, 1000)), sizeResolver)
            }
        }
    }

    @Test
    fun testSizeMultiplier() {
        val context = getTestContext()
        ImageRequest(context, ResourceImages.jpeg.uri).apply {
            assertEquals(null, sizeMultiplier)
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            sizeMultiplier(1.5f)
        }.apply {
            assertEquals(1.5f, sizeMultiplier)
        }
    }

    @Test
    fun testPrecision() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context, uri).apply {
            build().apply {
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
            }

            precision(LongImagePrecisionDecider(EXACTLY))
            build().apply {
                assertEquals(LongImagePrecisionDecider(EXACTLY), precisionDecider)
            }

            precision(SAME_ASPECT_RATIO)
            build().apply {
                assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
            }

            precision(null)
            build().apply {
                assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
            }
        }

        val request = ImageRequest(context, uri).apply {
            assertEquals(SizeResolver(context.screenSize()), sizeResolver)
            assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }
        val size = Size(333, 555)
        val request1 = request.newRequest {
            size(size)
        }.apply {
            assertEquals(FixedSizeResolver(size), sizeResolver)
            assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }
        request1.newRequest().apply {
            assertEquals(FixedSizeResolver(size), sizeResolver)
            assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }

        request.apply {
            assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }
        sketch.execute(request).apply {
            assertEquals(
                FixedPrecisionDecider(LESS_PIXELS),
                this.request.precisionDecider
            )
        }
    }

    @Test
    fun testScale() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            scale(LongImageScaleDecider(START_CROP, END_CROP))
            build().apply {
                assertEquals(
                    LongImageScaleDecider(START_CROP, END_CROP),
                    scaleDecider
                )
            }

            scale(FILL)
            build().apply {
                assertEquals(FixedScaleDecider(FILL), scaleDecider)
            }

            scale(null)
            build().apply {
                assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            scale(null)
            target(TestScaleResolverTarget(FILL))
            build().apply {
                assertEquals(FixedScaleDecider(FILL), scaleDecider)
            }

            scale(END_CROP)
            target(TestScaleResolverTarget(FILL))
            build().apply {
                assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }
        }
    }

    @Test
    fun testTransformations() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(transformations)
            }

            /* transformations() */
            transformations(listOf(CircleCropTransformation()))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }

            transformations(RoundedCornersTransformation(), RotateTransformation(40))
            build().apply {
                assertEquals(
                    listOf(RoundedCornersTransformation(), RotateTransformation(40)),
                    transformations
                )
            }

            transformations(null)
            build().apply {
                assertNull(transformations)
            }

            /* addTransformations(List), removeTransformations(List) */
            addTransformations(listOf(CircleCropTransformation()))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(listOf(CircleCropTransformation(), RotateTransformation(40)))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(listOf(RotateTransformation(40)))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(listOf(CircleCropTransformation()))
            build().apply {
                assertNull(transformations)
            }

            /* addTransformations(vararg), removeTransformations(vararg) */
            addTransformations(CircleCropTransformation())
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(CircleCropTransformation(), RotateTransformation(40))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(RotateTransformation(40))
            build().apply {
                assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(CircleCropTransformation())
            build().apply {
                assertNull(transformations)
            }
        }
    }

    @Test
    fun testResultCachePolicy() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertEquals(ENABLED, resultCachePolicy)
            }

            resultCachePolicy(READ_ONLY)
            build().apply {
                assertEquals(READ_ONLY, resultCachePolicy)
            }

            resultCachePolicy(DISABLED)
            build().apply {
                assertEquals(DISABLED, resultCachePolicy)
            }

            resultCachePolicy(null)
            build().apply {
                assertEquals(ENABLED, resultCachePolicy)
            }
        }
    }

    @Test
    fun testDisallowAnimatedImage() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertFalse(disallowAnimatedImage)
            }

            disallowAnimatedImage(true)
            build().apply {
                assertEquals(true, disallowAnimatedImage)
            }

            disallowAnimatedImage(false)
            build().apply {
                assertEquals(false, disallowAnimatedImage)
            }

            disallowAnimatedImage(null)
            build().apply {
                assertFalse(disallowAnimatedImage)
            }
        }
    }

    @Test
    fun testPlaceholder() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(placeholder)
            }

            placeholder(FakeStateImage())
            build().apply {
                assertEquals(FakeStateImage(), placeholder)
            }

            placeholder(null)
            build().apply {
                assertNull(placeholder)
            }
        }
    }

    @Test
    fun testFallback() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(fallback)
            }

            fallback(FakeStateImage())
            build().apply {
                assertEquals(FakeStateImage(), fallback)
            }

            fallback(null)
            build().apply {
                assertNull(fallback)
            }
        }
    }

    @Test
    fun testError() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(error)
            }

            error(FakeStateImage())
            build().apply {
                assertEquals(FakeStateImage(), error)
            }

            error(null)
            build().apply {
                assertNull(error)
            }
        }
    }

    @Test
    fun testTransitionFactory() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(transitionFactory)
            }

            transitionFactory(CrossfadeTransition.Factory())
            build().apply {
                assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }

            transitionFactory(null)
            build().apply {
                assertNull(transitionFactory)
            }
        }
    }

    @Test
    fun testCrossfade() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            assertNull(transitionFactory)
        }.newRequest {
            crossfade()
        }.apply {
            assertEquals(
                expected = CrossfadeTransition.Factory(),
                actual = transitionFactory
            )
        }.newRequest {
            crossfade(
                durationMillis = CrossfadeTransition.DEFAULT_DURATION_MILLIS * 2,
                fadeStart = !CrossfadeTransition.DEFAULT_FADE_START,
                preferExactIntrinsicSize = !CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
                alwaysUse = !CrossfadeTransition.DEFAULT_ALWAYS_USE
            )
        }.apply {
            assertEquals(
                expected = CrossfadeTransition.Factory(
                    durationMillis = CrossfadeTransition.DEFAULT_DURATION_MILLIS * 2,
                    fadeStart = !CrossfadeTransition.DEFAULT_FADE_START,
                    preferExactIntrinsicSize = !CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
                    alwaysUse = !CrossfadeTransition.DEFAULT_ALWAYS_USE
                ),
                actual = transitionFactory
            )
        }.newRequest {
            crossfade(false)
        }.apply {
            assertNull(transitionFactory)
        }.newRequest {
            crossfade(true)
        }.apply {
            assertEquals(
                expected = CrossfadeTransition.Factory(),
                actual = transitionFactory
            )
        }
    }

    @Test
    fun testResizeOnDraw() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertFalse(resizeOnDraw ?: false)
            }

            resizeOnDraw()
            build().apply {
                assertEquals(true, resizeOnDraw)
            }

            resizeOnDraw(false)
            build().apply {
                assertEquals(false, resizeOnDraw)
            }

            resizeOnDraw(null)
            build().apply {
                assertFalse(resizeOnDraw ?: false)
            }
        }
    }

    @Test
    fun testAllowNullImage() = runTest {
        val context = getTestContext()

        ImageRequest(context, ResourceImages.animGif.uri).apply {
            assertNull(allowNullImage)
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            allowNullImage()
        }.apply {
            assertTrue(allowNullImage!!)
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            allowNullImage(true)
        }.apply {
            assertTrue(allowNullImage!!)
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            allowNullImage(false)
        }.apply {
            assertFalse(allowNullImage!!)
        }
    }

    @Test
    fun testMemoryCachePolicy() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertEquals(ENABLED, memoryCachePolicy)
            }

            memoryCachePolicy(READ_ONLY)
            build().apply {
                assertEquals(READ_ONLY, memoryCachePolicy)
            }

            memoryCachePolicy(DISABLED)
            build().apply {
                assertEquals(DISABLED, memoryCachePolicy)
            }

            memoryCachePolicy(null)
            build().apply {
                assertEquals(ENABLED, memoryCachePolicy)
            }
        }
    }

    @Test
    fun testListener() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            assertNull(listener)
            assertNull(target)
        }

        val listener1 = object : Listener {}
        val listener2 = object : Listener {}
        val listener3 = object : Listener {}

        ImageRequest(context1, uri) {
            registerListener(listener1)
        }.apply {
            assertEquals(listener1, listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
        }.apply {
            assertEquals(Listeners(listOf(listener1, listener2)), listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
            registerListener(listener3)
        }.apply {
            assertEquals(Listeners(listOf(listener1, listener2, listener3)), listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
            registerListener(listener3)
            unregisterListener(listener2)
        }.apply {
            assertEquals(Listeners(listOf(listener1, listener3)), listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
            registerListener(listener3)
            unregisterListener(listener2)
            unregisterListener(listener1)
        }.apply {
            assertEquals(listener3, listener)
            assertNull(target)
        }

        val listenerTarget = TestListenerTarget()

        ImageRequest(context1, uri) {
            target(listenerTarget)
        }.apply {
            assertEquals(listenerTarget.myListener, listener)
            assertEquals(listenerTarget, target)
        }
        ImageRequest(context1, uri) {
            target(listenerTarget)
            registerListener(listener1)
        }.apply {
            assertEquals(
                PairListener(first = listener1, second = listenerTarget.myListener),
                listener
            )
            assertEquals(listenerTarget, target)
        }
        ImageRequest(context1, uri) {
            target(listenerTarget)
            registerListener(listener1)
            registerListener(listener2)
        }.apply {
            assertEquals(
                PairListener(
                    first = Listeners(listOf(listener1, listener2)),
                    second = listenerTarget.myListener
                ), listener
            )
            assertEquals(listenerTarget, target)
        }

        ImageRequest(context1, uri) {
            registerListener(
                onStart = {},
                onCancel = {},
                onError = { _, _ -> },
                onSuccess = { _, _ -> })
        }.apply {
            assertTrue(listener is Listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onStart = {})
        }.apply {
            assertTrue(listener is Listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onCancel = {})
        }.apply {
            assertTrue(listener is Listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onError = { _, _ -> })
        }.apply {
            assertTrue(listener is Listener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onSuccess = { _, _ -> })
        }.apply {
            assertTrue(listener is Listener)
            assertNull(target)
        }
    }

    @Test
    fun testProgressListener() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            assertNull(progressListener)
            assertNull(target)
        }

        val listener1 = ProgressListener { _, _ -> }
        val listener2 = ProgressListener { _, _ -> }
        val listener3 = ProgressListener { _, _ -> }

        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
        }.apply {
            assertEquals(listener1, progressListener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
        }.apply {
            assertEquals(ProgressListeners(listOf(listener1, listener2)), progressListener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
            registerProgressListener(listener3)
        }.apply {
            assertEquals(
                ProgressListeners(listOf(listener1, listener2, listener3)),
                progressListener
            )
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
            registerProgressListener(listener3)
            unregisterProgressListener(listener2)
        }.apply {
            assertEquals(ProgressListeners(listOf(listener1, listener3)), progressListener)
            assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
            registerProgressListener(listener3)
            unregisterProgressListener(listener2)
            unregisterProgressListener(listener1)
        }.apply {
            assertEquals(listener3, progressListener)
            assertNull(target)
        }

        val listenerTarget = TestListenerTarget()

        ImageRequest(context1, uri) {
            target(listenerTarget)
        }.apply {
            assertEquals(listenerTarget.myProgressListener, progressListener)
            assertEquals(listenerTarget, target)
        }
        ImageRequest(context1, uri) {
            target(listenerTarget)
            registerProgressListener(listener1)
        }.apply {
            assertEquals(
                PairProgressListener(
                    first = listener1,
                    second = listenerTarget.myProgressListener
                ), progressListener
            )
            assertEquals(listenerTarget, target)
        }
        ImageRequest(context1, uri) {
            target(listenerTarget)
            registerProgressListener(listener1)
            registerProgressListener(listener2)
        }.apply {
            assertEquals(
                PairProgressListener(
                    first = ProgressListeners(
                        listOf(
                            listener1,
                            listener2
                        )
                    ), second = listenerTarget.myProgressListener
                ), progressListener
            )
            assertEquals(listenerTarget, target)
        }
    }

    @Test
    fun testComponents() {
        val context = getTestContext()
        ImageRequest(context, ResourceImages.jpeg.uri).apply {
            assertNull(componentRegistry)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            components {
                addFetcher(HttpUriFetcher.Factory())
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor())
            }
        }.apply {
            assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(HttpUriFetcher.Factory())
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                }.build(),
                componentRegistry
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            components {
                addFetcher(HttpUriFetcher.Factory())
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder.Factory())
            }
            components {
                addRequestInterceptor(TestRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor())
            }
        }.apply {
            assertEquals(
                ComponentRegistry.Builder().apply {
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                }.build(),
                componentRegistry
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            components {
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor())
            }
            target(TestComponentsTarget(ComponentRegistry {
                addFetcher(TestFetcher2.Factory())
                addDecoder(TestDecoder2.Factory())
                addRequestInterceptor(TestRequestInterceptor2())
                addDecodeInterceptor(TestDecodeInterceptor2())
            }))
        }.apply {
            assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                    addFetcher(TestFetcher2.Factory())
                    addDecoder(TestDecoder2.Factory())
                    addRequestInterceptor(TestRequestInterceptor2())
                    addDecodeInterceptor(TestDecodeInterceptor2())
                }.build(),
                componentRegistry
            )
        }
    }

    @Test
    fun testAddComponents() {
        val context = getTestContext()
        ImageRequest(context, ResourceImages.jpeg.uri).apply {
            assertNull(componentRegistry)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            components {
                addFetcher(HttpUriFetcher.Factory())
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder.Factory())
            }
            addComponents {
                addRequestInterceptor(TestRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor())
            }
        }.apply {
            assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(HttpUriFetcher.Factory())
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                }.build(),
                componentRegistry
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = ImageRequest(context, ResourceImages.jpeg.uri)
        val element11 = ImageRequest(context, ResourceImages.jpeg.uri)
        val element2 = ImageRequest(context, ResourceImages.png.uri)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())

        val scopeActions = listOfNotNull<ScopeAction<ImageRequest.Builder>>(
            ScopeAction {
                registerListener(onStart = {})
            },
            ScopeAction {
                registerProgressListener { _, _ -> }
            },
            ScopeAction {
                defaultOptions(ImageOptions {
                    size(100, 100)
                })
            },
            ScopeAction {
                depth(LOCAL, "test")
            },
            ScopeAction {
                setExtra("type", "list")
                setExtra("big", "true")
            },
            ScopeAction {
                httpHeader("from", "china")
                httpHeader("job", "Programmer")
                addHttpHeader("Host", "www.google.com")
            },
            ScopeAction {
                downloadCachePolicy(READ_ONLY)
            },
            ScopeAction {
                colorType("RAGB")
            },
            ScopeAction {
                colorSpace("SRGB")
            },
            ScopeAction {
                size(300, 200)
            },
            ScopeAction {
                precision(EXACTLY)
            },
            ScopeAction {
                precision(LongImagePrecisionDecider())
            },
            ScopeAction {
                scale(FILL)
            },
            ScopeAction {
                scale(LongImageScaleDecider())
            },
            ScopeAction {
                transformations(CircleCropTransformation(), BlurTransformation())
            },
            ScopeAction {
                resultCachePolicy(WRITE_ONLY)
            },
            ScopeAction {
                disallowAnimatedImage(true)
            },
            ScopeAction {
                placeholder(FakeStateImage())
            },
            ScopeAction {
                fallback(FakeStateImage())
            },
            ScopeAction {
                error(FakeStateImage())
            },
            ScopeAction {
                transitionFactory(CrossfadeTransition.Factory())
            },
            ScopeAction {
                resizeOnDraw(true)
            },
            ScopeAction {
                memoryCachePolicy(WRITE_ONLY)
            },
            ScopeAction {
                components {
                    addFetcher(TestFetcher.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addDecodeInterceptor(TestDecodeInterceptor())
                    addDecoder(TestDecoder.Factory())
                }
            },
        )

        val requests = mutableListOf<ImageRequest>()
        scopeActions.forEachIndexed { itemIndex, action ->
            val request = requests.lastOrNull() ?: ImageRequest(context, ResourceImages.jpeg.uri)
            val builder = request.newBuilder()
            with(action) {
                builder.invoke()
            }
            val newRequest = builder.build()
            assertEquals(
                expected = newRequest,
                actual = newRequest.newRequest(),
                message = "itemIndex=$itemIndex, newRequest=$newRequest",
            )
            assertEquals(
                expected = newRequest.hashCode(),
                actual = newRequest.newRequest().hashCode(),
                message = "itemIndex=$itemIndex, newRequest=$newRequest",
            )
            requests.forEachIndexed { lastRequestIndex, lastRequest ->
                assertNotEquals(
                    illegal = lastRequest,
                    actual = newRequest,
                    message = "itemIndex=$itemIndex, lastRequestIndex=$lastRequestIndex, lastRequest=$lastRequest, newRequest=$newRequest",
                )
                // equals is not the same, hashCode may be the same
            }
            requests.add(newRequest)
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val testTarget = TestTarget()
        val testListener = TestListenerTarget().myListener
        val testProgressListener = TestListenerTarget().myProgressListener
        val testLifecycle = TestLifecycle()
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            target(testTarget)
            registerListener(testListener)
            registerProgressListener(testProgressListener)
            lifecycle(testLifecycle)

            depth(LOCAL, "test")
            setExtra("key", "value")
            httpHeader("key1", "value1")
            downloadCachePolicy(WRITE_ONLY)
            colorType("RGB_565")
            colorSpace("SRGB")
            size(100, 100)
            sizeMultiplier(1.5f)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
            transformations(RotateTransformation(40))
            disallowAnimatedImage(true)
            resultCachePolicy(READ_ONLY)
            placeholder(FakeStateImage(FakeImage(SketchSize(100, 100))))
            fallback(FakeStateImage(FakeImage(SketchSize(100, 100))))
            error(FakeStateImage(FakeImage(SketchSize(100, 100))))
            transitionFactory(FakeTransition.Factory())
            resizeOnDraw(true)
            allowNullImage(true)
            memoryCachePolicy(ENABLED)
            components {
                addFetcher(HttpUriFetcher.Factory())
            }
        }.apply {
            assertEquals(
                expected = "ImageRequest(context=$context, uri=http://sample.com/sample.jpeg, target=TestTarget, listener=$testListener, progressListener=$testProgressListener, lifecycleResolver=FixedLifecycleResolver($testLifecycle), definedRequestOptions=RequestOptions(listener=$testListener, progressListener=$testProgressListener, lifecycleResolver=FixedLifecycleResolver($testLifecycle)), definedOptions=ImageOptions(depthHolder=DepthHolder(depth=LOCAL, from='test'), extras=Extras({key=Entry(value=value, cacheKey=value, requestKey=value)}), httpHeaders=HttpHeaders(sets=[key1:value1],adds=[]), downloadCachePolicy=WRITE_ONLY, colorType=FixedColorType(RGB_565), colorSpace=FixedColorSpace(SRGB), sizeResolver=FixedSizeResolver(size=100x100), sizeMultiplier=1.5, precisionDecider=FixedPrecisionDecider(SAME_ASPECT_RATIO), scaleDecider=FixedScaleDecider(scale=FILL), transformations=[RotateTransformation(40)], disallowAnimatedImage=true, resultCachePolicy=READ_ONLY, placeholder=FakeStateImage(image=FakeImage(size=100x100)), fallback=FakeStateImage(image=FakeImage(size=100x100)), error=FakeStateImage(image=FakeImage(size=100x100)), transitionFactory=FakeTransition, resizeOnDraw=true, allowNullImage=true, memoryCachePolicy=ENABLED, componentRegistry=ComponentRegistry(fetcherFactoryList=[HttpUriFetcher],decoderFactoryList=[],requestInterceptorList=[],decodeInterceptorList=[])), defaultOptions=null, depthHolder=DepthHolder(depth=LOCAL, from='test'), extras=Extras({key=Entry(value=value, cacheKey=value, requestKey=value)}), httpHeaders=HttpHeaders(sets=[key1:value1],adds=[]), downloadCachePolicy=WRITE_ONLY, colorType=FixedColorType(RGB_565), colorSpace=FixedColorSpace(SRGB), sizeResolver=FixedSizeResolver(size=100x100), sizeMultiplier=1.5, precisionDecider=FixedPrecisionDecider(SAME_ASPECT_RATIO), scaleDecider=FixedScaleDecider(scale=FILL), transformations=[RotateTransformation(40)], disallowAnimatedImage=true, resultCachePolicy=READ_ONLY, placeholder=FakeStateImage(image=FakeImage(size=100x100)), fallback=FakeStateImage(image=FakeImage(size=100x100)), error=FakeStateImage(image=FakeImage(size=100x100)), transitionFactory=FakeTransition, resizeOnDraw=true, allowNullImage=true, memoryCachePolicy=ENABLED, componentRegistry=ComponentRegistry(fetcherFactoryList=[HttpUriFetcher],decoderFactoryList=[],requestInterceptorList=[],decodeInterceptorList=[]))",
                actual = this.toString()
            )
        }
    }
}