/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.core.android.test.request

import android.R.color
import android.R.drawable
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ACES
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.graphics.ColorSpace.Named.BT709
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.drawable.ColorDrawableEqualizer
import com.github.panpf.sketch.drawable.getEqualityDrawableCompat
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.Extras
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.Listeners
import com.github.panpf.sketch.request.internal.PairListener
import com.github.panpf.sketch.request.internal.PairProgressListener
import com.github.panpf.sketch.request.internal.ProgressListeners
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.fallback
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.OriginSizeResolver
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.MemoryCacheStateImage
import com.github.panpf.sketch.state.ResColorDrawableStateImage
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.sketch.state.addState
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ScopeAction
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestListenerImageView
import com.github.panpf.sketch.test.utils.TestOptionsImageView
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.target
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.ViewCrossfadeTransition
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageRequestTest {

    @Test
    fun testFun() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            Assert.assertSame(context1, this.context)
            Assert.assertEquals("asset://sample.jpeg", uri)
            Assert.assertNull(this.listener)
            Assert.assertNull(this.progressListener)
            Assert.assertNull(this.target)
            Assert.assertEquals(
                LifecycleResolver(GlobalLifecycle),
                this.lifecycleResolver
            )

            Assert.assertEquals(NETWORK, this.depthHolder.depth)
            Assert.assertNull(this.extras)
            Assert.assertNull(this.httpHeaders)
            Assert.assertEquals(ENABLED, this.downloadCachePolicy)
            Assert.assertNull(this.bitmapConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertNull(this.colorSpace)
            }
            Assert.assertFalse(this.preferQualityOverSpeed)
            Assert.assertEquals(OriginSizeResolver, this.sizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), this.precisionDecider)
            Assert.assertEquals(FixedScaleDecider(CENTER_CROP), this.scaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertEquals(ENABLED, this.resultCachePolicy)
            Assert.assertNull(this.placeholder)
            Assert.assertNull(this.fallback)
            Assert.assertNull(this.error)
            Assert.assertNull(this.transitionFactory)
            Assert.assertFalse(this.disallowAnimatedImage)
            Assert.assertFalse(this.resizeOnDraw ?: false)
            Assert.assertEquals(ENABLED, this.memoryCachePolicy)
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @Test
    fun testNewBuilder() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri

        ImageRequest(context1, uri).newBuilder().build().apply {
            Assert.assertEquals(NETWORK, depthHolder.depth)
        }
        ImageRequest(context1, uri).newBuilder {
            depth(LOCAL)
        }.build().apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
        }
        ImageRequest(context1, uri).newBuilder {
            depth(LOCAL)
        }.build().apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
        }

        ImageRequest(context1, uri).newRequest().apply {
            Assert.assertEquals(NETWORK, depthHolder.depth)
        }
        ImageRequest(context1, uri).newRequest {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
        }
        ImageRequest(context1, uri).newRequest {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
        }

        ImageRequest(context1, uri).newBuilder().build().apply {
            Assert.assertEquals(NETWORK, depthHolder.depth)
            Assert.assertNull(listener)
            Assert.assertNull(progressListener)
        }
        ImageRequest(context1, uri).newBuilder {
            depth(LOCAL)
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
            registerProgressListener() { _, _ ->

            }
        }.build().apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
            Assert.assertNotNull(listener)
            Assert.assertNotNull(progressListener)
        }

        ImageRequest(context1, uri).newRequest().apply {
            Assert.assertEquals(NETWORK, depthHolder.depth)
            Assert.assertNull(listener)
            Assert.assertNull(progressListener)
        }
        ImageRequest(context1, uri).newRequest {
            depth(LOCAL)
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
            registerProgressListener() { _, _ ->

            }
        }.apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
            Assert.assertNotNull(listener)
            Assert.assertNotNull(progressListener)
        }
    }

    @Test
    fun testContext() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            Assert.assertEquals(context1, context)
            Assert.assertNotEquals(context1, context.applicationContext)
        }
    }

    @Test
    fun testTarget() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        val imageView = TestOptionsImageView(context1)

        ImageRequest(context1, uri).apply {
            Assert.assertNull(target)
        }

        ImageRequest(context1, uri) {
            target(TestTarget())
        }.apply {
            Assert.assertTrue(target is TestTarget)
        }

        ImageRequest(imageView, uri) {
            target(TestTarget())
            target(null)
        }.apply {
            Assert.assertNull(target)
        }

        ImageRequest(context1, uri) {
            target(onStart = { _, _ -> }, onSuccess = { _, _ -> }, onError = { _, _ -> })
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
        ImageRequest(context1, uri) {
            target(onStart = { _, _ -> })
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
        ImageRequest(context1, uri) {
            target(onSuccess = { _, _ -> })
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
        ImageRequest(context1, uri) {
            target(onError = { _, _ -> })
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
    }

    @Test
    fun testLifecycle() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        var lifecycle1: Lifecycle? = null
        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle
                get() = lifecycle1!!
        }
        lifecycle1 = LifecycleRegistry(lifecycleOwner)

        ImageRequest(context1, uri).apply {
            Assert.assertEquals(
                LifecycleResolver(GlobalLifecycle),
                this.lifecycleResolver
            )
        }

        // TODO test lifecycle
    }

    @Test
    fun testDefinedOptions() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri

        ImageRequest(context1, uri).apply {
            Assert.assertEquals(ImageOptions(), definedOptions)
        }

        ImageRequest(context1, uri) {
            size(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }.apply {
            Assert.assertEquals(ImageOptions {
                size(100, 50)
                addTransformations(CircleCropTransformation())
                crossfade()
            }, definedOptions)
        }
    }

    @Test
    fun testDefault() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri

        ImageRequest(context1, uri).apply {
            Assert.assertNull(defaultOptions)
        }

        val options = ImageOptions {
            size(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }
        ImageRequest(context1, uri) {
            defaultOptions(options)
        }.apply {
            Assert.assertSame(options, defaultOptions)
        }
    }

    @Test
    fun testMerge() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertEquals(NETWORK, depthHolder.depth)
                Assert.assertNull(extras)
            }

            merge(ImageOptions {
                size(100, 50)
                memoryCachePolicy(DISABLED)
                addTransformations(CircleCropTransformation())
                crossfade()
            })
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 50), sizeResolver)
                Assert.assertEquals(DISABLED, memoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }

            merge(ImageOptions {
                memoryCachePolicy(READ_ONLY)
            })
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 50), sizeResolver)
                Assert.assertEquals(DISABLED, memoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }
        }
    }

    @Test
    fun testDepth() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            Assert.assertEquals(NETWORK, depthHolder.depth)
            Assert.assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
            Assert.assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(null)
        }.apply {
            Assert.assertEquals(NETWORK, depthHolder.depth)
            Assert.assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(LOCAL, null)
        }.apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
            Assert.assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(null, "TestDepthFrom")
        }.apply {
            Assert.assertEquals(NETWORK, depthHolder.depth)
            Assert.assertNull(depthHolder.from)
        }

        ImageRequest(context1, uri) {
            depth(LOCAL, "TestDepthFrom")
        }.apply {
            Assert.assertEquals(LOCAL, depthHolder.depth)
            Assert.assertEquals("TestDepthFrom", depthHolder.from)
        }
    }

    @Test
    fun testExtras() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(extras)
            }

            /* extras() */
            extras(Extras())
            build().apply {
                Assert.assertNull(extras)
            }

            extras(Extras.Builder().set("key1", "value1").build())
            build().apply {
                Assert.assertEquals(1, extras?.size)
                Assert.assertEquals("value1", extras?.get("key1"))
            }

            extras(null)
            build().apply {
                Assert.assertNull(extras)
            }

            /* setParameter(), removeParameter() */
            setExtra("key1", "value1")
            setExtra("key2", "value2", "value2")
            build().apply {
                Assert.assertEquals(2, extras?.size)
                Assert.assertEquals("value1", extras?.get("key1"))
                Assert.assertEquals("value2", extras?.get("key2"))
            }

            setExtra("key2", "value2.1", null)
            build().apply {
                Assert.assertEquals(2, extras?.size)
                Assert.assertEquals("value1", extras?.get("key1"))
                Assert.assertEquals("value2.1", extras?.get("key2"))
            }

            removeExtra("key2")
            build().apply {
                Assert.assertEquals(1, extras?.size)
                Assert.assertEquals("value1", extras?.get("key1"))
            }

            removeExtra("key1")
            build().apply {
                Assert.assertNull(extras)
            }
        }
    }

    @Test
    fun testHttpHeaders() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(httpHeaders)
            }

            /* httpHeaders() */
            httpHeaders(HttpHeaders())
            build().apply {
                Assert.assertNull(httpHeaders)
            }

            httpHeaders(HttpHeaders.Builder().set("key1", "value1").build())
            build().apply {
                Assert.assertEquals(1, httpHeaders?.size)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            httpHeaders(null)
            build().apply {
                Assert.assertNull(httpHeaders)
            }

            /* setHttpHeader(), addHttpHeader(), removeHttpHeader() */
            setHttpHeader("key1", "value1")
            setHttpHeader("key2", "value2")
            addHttpHeader("key3", "value3")
            addHttpHeader("key3", "value3.1")
            build().apply {
                Assert.assertEquals(4, httpHeaders?.size)
                Assert.assertEquals(2, httpHeaders?.setSize)
                Assert.assertEquals(2, httpHeaders?.addSize)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
                Assert.assertEquals("value2", httpHeaders?.getSet("key2"))
                Assert.assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            setHttpHeader("key2", "value2.1")
            build().apply {
                Assert.assertEquals(4, httpHeaders?.size)
                Assert.assertEquals(2, httpHeaders?.setSize)
                Assert.assertEquals(2, httpHeaders?.addSize)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
                Assert.assertEquals("value2.1", httpHeaders?.getSet("key2"))
                Assert.assertEquals(listOf("value3", "value3.1"), httpHeaders?.getAdd("key3"))
            }

            removeHttpHeader("key3")
            build().apply {
                Assert.assertEquals(2, httpHeaders?.size)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
                Assert.assertEquals("value2.1", httpHeaders?.getSet("key2"))
            }

            removeHttpHeader("key2")
            build().apply {
                Assert.assertEquals(1, httpHeaders?.size)
                Assert.assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            removeHttpHeader("key1")
            build().apply {
                Assert.assertNull(httpHeaders)
            }
        }
    }

    @Test
    fun testDownloadCachePolicy() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertEquals(ENABLED, downloadCachePolicy)
            }

            downloadCachePolicy(READ_ONLY)
            build().apply {
                Assert.assertEquals(READ_ONLY, downloadCachePolicy)
            }

            downloadCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, downloadCachePolicy)
            }

            downloadCachePolicy(null)
            build().apply {
                Assert.assertEquals(ENABLED, downloadCachePolicy)
            }
        }
    }

    @Test
    fun testBitmapConfig() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(bitmapConfig)
            }

            bitmapConfig(BitmapConfig(RGB_565))
            build().apply {
                Assert.assertEquals(BitmapConfig(RGB_565), bitmapConfig)
            }

            bitmapConfig(ARGB_8888)
            build().apply {
                Assert.assertEquals(BitmapConfig(ARGB_8888), bitmapConfig)
            }

            bitmapConfig(BitmapConfig.LowQuality)
            build().apply {
                Assert.assertEquals(BitmapConfig.LowQuality, bitmapConfig)
            }

            bitmapConfig(BitmapConfig.HighQuality)
            build().apply {
                Assert.assertEquals(BitmapConfig.HighQuality, bitmapConfig)
            }

            bitmapConfig(null)
            build().apply {
                Assert.assertNull(bitmapConfig)
            }
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(colorSpace)
            }

            colorSpace(ACES)
            build().apply {
                Assert.assertEquals(ColorSpace.get(ACES), colorSpace)
            }

            colorSpace(BT709)
            build().apply {
                Assert.assertEquals(ColorSpace.get(BT709), colorSpace)
            }

            colorSpace(null)
            build().apply {
                Assert.assertNull(colorSpace)
            }
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testPreferQualityOverSpeed() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertFalse(preferQualityOverSpeed)
            }

            preferQualityOverSpeed()
            build().apply {
                Assert.assertEquals(true, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(false)
            build().apply {
                Assert.assertEquals(false, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(null)
            build().apply {
                Assert.assertFalse(preferQualityOverSpeed)
            }
        }
    }

    @Test
    fun testResize() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(definedOptions.sizeResolver)
                Assert.assertNull(definedOptions.precisionDecider)
                Assert.assertNull(definedOptions.scaleDecider)
                Assert.assertEquals(OriginSizeResolver, sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    definedOptions.precisionDecider
                )
                Assert.assertEquals(
                    FixedScaleDecider(START_CROP),
                    definedOptions.scaleDecider
                )
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(100, 100)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                Assert.assertNull(definedOptions.precisionDecider)
                Assert.assertNull(definedOptions.scaleDecider)
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, EXACTLY)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(EXACTLY),
                    definedOptions.precisionDecider
                )
                Assert.assertNull(definedOptions.scaleDecider)
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, scale = END_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                Assert.assertNull(definedOptions.precisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), definedOptions.scaleDecider)
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                Assert.assertNull(definedOptions.sizeResolver)
                Assert.assertNull(definedOptions.precisionDecider)
                Assert.assertNull(definedOptions.scaleDecider)
                Assert.assertEquals(OriginSizeResolver, sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    definedOptions.precisionDecider
                )
                Assert.assertEquals(
                    FixedScaleDecider(START_CROP),
                    definedOptions.scaleDecider
                )
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(START_CROP), scaleDecider)
            }

            resize(Size(100, 100))
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                Assert.assertNull(definedOptions.precisionDecider)
                Assert.assertNull(definedOptions.scaleDecider)
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), EXACTLY)
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                Assert.assertEquals(
                    FixedPrecisionDecider(EXACTLY),
                    definedOptions.precisionDecider
                )
                Assert.assertNull(definedOptions.scaleDecider)
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), scale = END_CROP)
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.sizeResolver
                )
                Assert.assertNull(definedOptions.precisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), definedOptions.scaleDecider)
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), scaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                Assert.assertNull(definedOptions.sizeResolver)
                Assert.assertNull(definedOptions.precisionDecider)
                Assert.assertNull(definedOptions.scaleDecider)
                Assert.assertEquals(OriginSizeResolver, sizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }
        }
    }

    @Test
    fun testResizeSize() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(definedOptions.sizeResolver)
                Assert.assertEquals(OriginSizeResolver, sizeResolver)
            }

            size(Size(100, 100))
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.sizeResolver)
                Assert.assertEquals(FixedSizeResolver(100, 100), sizeResolver)
            }

            size(200, 200)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(200, 200), definedOptions.sizeResolver)
                Assert.assertEquals(FixedSizeResolver(200, 200), sizeResolver)
            }

            size(FixedSizeResolver(300, 200))
            build().apply {
                Assert.assertEquals(FixedSizeResolver(300, 200), definedOptions.sizeResolver)
                Assert.assertEquals(FixedSizeResolver(300, 200), sizeResolver)
            }

            size(null)
            build().apply {
                Assert.assertNull(definedOptions.sizeResolver)
                Assert.assertEquals(OriginSizeResolver, sizeResolver)
            }
        }
    }

    @Test
    fun testResizePrecision() {
        val (context, sketch) = getTestContextAndSketch()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context, uri).apply {
            build().apply {
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
            }

            precision(LongImagePrecisionDecider(EXACTLY))
            build().apply {
                Assert.assertEquals(LongImagePrecisionDecider(EXACTLY), precisionDecider)
            }

            precision(SAME_ASPECT_RATIO)
            build().apply {
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    precisionDecider
                )
            }

            precision(null)
            build().apply {
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
            }
        }

        val request = ImageRequest(context, uri).apply {
            Assert.assertEquals(OriginSizeResolver, sizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }
        val size = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
        val request1 = request.newRequest {
            size(size)
        }.apply {
            Assert.assertEquals(FixedSizeResolver(size), sizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }
        request1.newRequest().apply {
            Assert.assertEquals(FixedSizeResolver(size), sizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }

        request.apply {
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), precisionDecider)
        }
        runBlocking { sketch.execute(request) }.apply {
            Assert.assertEquals(
                FixedPrecisionDecider(LESS_PIXELS),
                this.request.precisionDecider
            )
        }
    }

    @Test
    fun testResizeScale() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }

            scale(LongImageScaleDecider(START_CROP, END_CROP))
            build().apply {
                Assert.assertEquals(
                    LongImageScaleDecider(START_CROP, END_CROP),
                    scaleDecider
                )
            }

            scale(FILL)
            build().apply {
                Assert.assertEquals(FixedScaleDecider(FILL), scaleDecider)
            }

            scale(null)
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), scaleDecider)
            }
        }
    }

    @Test
    fun testTransformations() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(transformations)
            }

            /* transformations() */
            transformations(listOf(CircleCropTransformation()))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }

            transformations(RoundedCornersTransformation(), RotateTransformation(40))
            build().apply {
                Assert.assertEquals(
                    listOf(RoundedCornersTransformation(), RotateTransformation(40)),
                    transformations
                )
            }

            transformations(null)
            build().apply {
                Assert.assertNull(transformations)
            }

            /* addTransformations(List), removeTransformations(List) */
            addTransformations(listOf(CircleCropTransformation()))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(listOf(CircleCropTransformation(), RotateTransformation(40)))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(listOf(RotateTransformation(40)))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(listOf(CircleCropTransformation()))
            build().apply {
                Assert.assertNull(transformations)
            }

            /* addTransformations(vararg), removeTransformations(vararg) */
            addTransformations(CircleCropTransformation())
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            addTransformations(CircleCropTransformation(), RotateTransformation(40))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(RotateTransformation(40))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation()),
                    transformations
                )
            }
            removeTransformations(CircleCropTransformation())
            build().apply {
                Assert.assertNull(transformations)
            }
        }
    }

    @Test
    fun testResultCachePolicy() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertEquals(ENABLED, resultCachePolicy)
            }

            resultCachePolicy(READ_ONLY)
            build().apply {
                Assert.assertEquals(READ_ONLY, resultCachePolicy)
            }

            resultCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, resultCachePolicy)
            }

            resultCachePolicy(null)
            build().apply {
                Assert.assertEquals(ENABLED, resultCachePolicy)
            }
        }
    }

    @Test
    fun testDisallowAnimatedImage() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertFalse(disallowAnimatedImage)
            }

            disallowAnimatedImage(true)
            build().apply {
                Assert.assertEquals(true, disallowAnimatedImage)
            }

            disallowAnimatedImage(false)
            build().apply {
                Assert.assertEquals(false, disallowAnimatedImage)
            }

            disallowAnimatedImage(null)
            build().apply {
                Assert.assertFalse(disallowAnimatedImage)
            }
        }
    }

    @Test
    fun testPlaceholder() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(placeholder)
            }

            placeholder(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                Assert.assertEquals(IntColorDrawableStateImage(Color.BLUE), placeholder)
            }

            placeholder(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, placeholder is DrawableStateImage)
            }

            placeholder(drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    DrawableStateImage(drawable.bottom_bar),
                    placeholder
                )
            }

            placeholder(null)
            build().apply {
                Assert.assertNull(placeholder)
            }
        }
    }

    @Test
    fun testFallback() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(fallback)
            }

            fallback(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                Assert.assertEquals(IntColorDrawableStateImage(Color.BLUE), fallback)
            }

            fallback(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, fallback is DrawableStateImage)
            }

            fallback(drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    DrawableStateImage(drawable.bottom_bar),
                    fallback
                )
            }

            fallback(null)
            build().apply {
                Assert.assertNull(fallback)
            }
        }
    }

    @Test
    fun testError() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(error)
            }

            error(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(IntColorDrawableStateImage(Color.BLUE)),
                    error
                )
            }

            error(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, error is ErrorStateImage)
            }

            error(drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(DrawableStateImage(drawable.bottom_bar)),
                    error
                )
            }

            error(drawable.bottom_bar) {
                addState(UriInvalidCondition, drawable.alert_dark_frame)
            }
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(DrawableStateImage(drawable.bottom_bar)) {
                        addState(UriInvalidCondition, drawable.alert_dark_frame)
                    },
                    error
                )
            }

            error()
            build().apply {
                Assert.assertNull(error)
            }

            error {
                addState(UriInvalidCondition, drawable.btn_dialog)
            }
            build().apply {
                Assert.assertNotNull(error)
            }
        }
    }

    @Test
    fun testTransitionFactory() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertNull(transitionFactory)
            }

            transitionFactory(ViewCrossfadeTransition.Factory())
            build().apply {
                Assert.assertEquals(ViewCrossfadeTransition.Factory(), transitionFactory)
            }

            transitionFactory(null)
            build().apply {
                Assert.assertNull(transitionFactory)
            }
        }
    }

    @Test
    fun testResizeOnDraw() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertFalse(resizeOnDraw ?: false)
            }

            resizeOnDraw()
            build().apply {
                Assert.assertEquals(true, resizeOnDraw)
            }

            resizeOnDraw(false)
            build().apply {
                Assert.assertEquals(false, resizeOnDraw)
            }

            resizeOnDraw(null)
            build().apply {
                Assert.assertFalse(resizeOnDraw ?: false)
            }
        }
    }

    @Test
    fun testAllowNullImage() = runTest {
        val context = getTestContext()

        ImageRequest(context, MyImages.animGif.uri).apply {
            Assert.assertNull(allowNullImage)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowNullImage()
        }.apply {
            Assert.assertTrue(allowNullImage!!)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowNullImage(true)
        }.apply {
            Assert.assertTrue(allowNullImage!!)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowNullImage(false)
        }.apply {
            Assert.assertFalse(allowNullImage!!)
        }
    }

    @Test
    fun testMemoryCachePolicy() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                Assert.assertEquals(ENABLED, memoryCachePolicy)
            }

            memoryCachePolicy(READ_ONLY)
            build().apply {
                Assert.assertEquals(READ_ONLY, memoryCachePolicy)
            }

            memoryCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, memoryCachePolicy)
            }

            memoryCachePolicy(null)
            build().apply {
                Assert.assertEquals(ENABLED, memoryCachePolicy)
            }
        }
    }

    @Test
    fun testListener() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            Assert.assertNull(listener)
            Assert.assertNull(target)
        }

        val listener1 = object : Listener {}
        val listener2 = object : Listener {}
        val listener3 = object : Listener {}

        ImageRequest(context1, uri) {
            registerListener(listener1)
        }.apply {
            Assert.assertEquals(listener1, listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
        }.apply {
            Assert.assertEquals(Listeners(listOf(listener1, listener2)), listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
            registerListener(listener3)
        }.apply {
            Assert.assertEquals(Listeners(listOf(listener1, listener2, listener3)), listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
            registerListener(listener3)
            unregisterListener(listener2)
        }.apply {
            Assert.assertEquals(Listeners(listOf(listener1, listener3)), listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(listener1)
            registerListener(listener2)
            registerListener(listener3)
            unregisterListener(listener2)
            unregisterListener(listener1)
        }.apply {
            Assert.assertEquals(listener3, listener)
            Assert.assertNull(target)
        }

        val imageView = TestListenerImageView(context1)

        ImageRequest(imageView, uri).apply {
            Assert.assertEquals(imageView.myListener, listener)
            Assert.assertEquals(ImageViewTarget(imageView), target)
        }
        ImageRequest(imageView, uri) {
            registerListener(listener1)
        }.apply {
            Assert.assertEquals(
                PairListener(first = listener1, second = imageView.myListener),
                listener
            )
            Assert.assertEquals(ImageViewTarget(imageView), target)
        }
        ImageRequest(imageView, uri) {
            registerListener(listener1)
            registerListener(listener2)
        }.apply {
            Assert.assertEquals(
                PairListener(
                    first = Listeners(listOf(listener1, listener2)),
                    second = imageView.myListener
                ), listener
            )
            Assert.assertEquals(ImageViewTarget(imageView), target)
        }

        ImageRequest(context1, uri) {
            registerListener(
                onStart = {},
                onCancel = {},
                onError = { _, _ -> },
                onSuccess = { _, _ -> })
        }.apply {
            Assert.assertTrue(listener is Listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onStart = {})
        }.apply {
            Assert.assertTrue(listener is Listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onCancel = {})
        }.apply {
            Assert.assertTrue(listener is Listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onError = { _, _ -> })
        }.apply {
            Assert.assertTrue(listener is Listener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerListener(onSuccess = { _, _ -> })
        }.apply {
            Assert.assertTrue(listener is Listener)
            Assert.assertNull(target)
        }
    }

    @Test
    fun testProgressListener() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        ImageRequest(context1, uri).apply {
            Assert.assertNull(progressListener)
            Assert.assertNull(target)
        }

        val listener1 = ProgressListener { _, _ -> }
        val listener2 = ProgressListener { _, _ -> }
        val listener3 = ProgressListener { _, _ -> }

        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
        }.apply {
            Assert.assertEquals(listener1, progressListener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
        }.apply {
            Assert.assertEquals(ProgressListeners(listOf(listener1, listener2)), progressListener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
            registerProgressListener(listener3)
        }.apply {
            Assert.assertEquals(
                ProgressListeners(listOf(listener1, listener2, listener3)),
                progressListener
            )
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
            registerProgressListener(listener3)
            unregisterProgressListener(listener2)
        }.apply {
            Assert.assertEquals(ProgressListeners(listOf(listener1, listener3)), progressListener)
            Assert.assertNull(target)
        }
        ImageRequest(context1, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
            registerProgressListener(listener3)
            unregisterProgressListener(listener2)
            unregisterProgressListener(listener1)
        }.apply {
            Assert.assertEquals(listener3, progressListener)
            Assert.assertNull(target)
        }

        val imageView = TestListenerImageView(context1)

        ImageRequest(imageView, uri).apply {
            Assert.assertEquals(imageView.myProgressListener, progressListener)
            Assert.assertEquals(ImageViewTarget(imageView), target)
        }
        ImageRequest(imageView, uri) {
            registerProgressListener(listener1)
        }.apply {
            Assert.assertEquals(
                PairProgressListener(
                    first = listener1,
                    second = imageView.myProgressListener
                ), progressListener
            )
            Assert.assertEquals(ImageViewTarget(imageView), target)
        }
        ImageRequest(imageView, uri) {
            registerProgressListener(listener1)
            registerProgressListener(listener2)
        }.apply {
            Assert.assertEquals(
                PairProgressListener(
                    first = ProgressListeners(
                        listOf(
                            listener1,
                            listener2
                        )
                    ), second = imageView.myProgressListener
                ), progressListener
            )
            Assert.assertEquals(ImageViewTarget(imageView), target)
        }
    }

    @Test
    fun testComponents() {
        val context = getTestContext()
        ImageRequest(context, MyImages.jpeg.uri).apply {
            Assert.assertNull(componentRegistry)
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            components {
                addFetcher(HttpUriFetcher.Factory())
                addFetcher(TestFetcher.Factory())
                addDecoder(BitmapFactoryDecoder.Factory())
                addDecoder(TestDecoder.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor())
            }
        }.apply {
            Assert.assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(HttpUriFetcher.Factory())
                    addFetcher(TestFetcher.Factory())
                    addDecoder(BitmapFactoryDecoder.Factory())
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
        val element1 = ImageRequest(context, MyImages.jpeg.uri)
        val element11 = ImageRequest(context, MyImages.jpeg.uri)
        val element2 = ImageRequest(context, MyImages.png.uri)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())

        val imageView = TestListenerImageView(context)
        val scopeActions = listOfNotNull<ScopeAction<ImageRequest.Builder>>(
            ScopeAction {
                registerListener(onStart = {})
            },
            ScopeAction {
                registerProgressListener() { _, _ -> }
            },
            ScopeAction {
                defaultOptions(ImageOptions() {
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
                setHttpHeader("from", "china")
                setHttpHeader("job", "Programmer")
                addHttpHeader("Host", "www.google.com")
            },
            ScopeAction {
                downloadCachePolicy(READ_ONLY)
            },
            ScopeAction {
                bitmapConfig(RGB_565)
            },
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                ScopeAction {
                    colorSpace(ADOBE_RGB)
                }
            } else null,
            ScopeAction {
                preferQualityOverSpeed(true)
            },
            ScopeAction {
                size(300, 200)
            },
            ScopeAction {
                size(ViewSizeResolver(imageView))
            },
            ScopeAction {
                precision(EXACTLY)
            },
            ScopeAction {
                precision(LongImagePrecisionDecider())
            },
            ScopeAction {
                scale(END_CROP)
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
                placeholder(
                    IconDrawableStateImage(
                        icon = drawable.ic_delete,
                        background = color.background_dark
                    )
                )
            },
            ScopeAction {
                placeholder(IntColorDrawableStateImage(Color.BLUE))
            },
            ScopeAction {
                placeholder(ResColorDrawableStateImage(color.background_dark))
            },
            ScopeAction {
                val drawable = context.resources.getEqualityDrawableCompat(drawable.ic_delete, null)
                placeholder(DrawableStateImage(drawable))
            },
            ScopeAction {
                placeholder(DrawableStateImage(drawable.ic_delete))
            },
            ScopeAction {
                placeholder(CurrentStateImage(drawable.ic_delete))
            },
            ScopeAction {
                placeholder(MemoryCacheStateImage("uri", IntColorDrawableStateImage(Color.BLUE)))
            },
            ScopeAction {
                placeholder(
                    ThumbnailMemoryCacheStateImage(
                        "uri",
                        IntColorDrawableStateImage(Color.BLUE)
                    )
                )
            },
            ScopeAction {
                error(DrawableStateImage(drawable.ic_delete))
            },
            ScopeAction {
                error(DrawableStateImage(drawable.ic_delete)) {
                    addState(UriInvalidCondition, IntColor(Color.BLUE))
                }
            },
            ScopeAction {
                transitionFactory(ViewCrossfadeTransition.Factory())
            },
            ScopeAction {
                transitionFactory(
                    ViewCrossfadeTransition.Factory(
                        fadeStart = false,
                        alwaysUse = true
                    )
                )
            },
            ScopeAction {
                disallowAnimatedImage(true)
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
            val request = requests.lastOrNull() ?: ImageRequest(context, MyImages.jpeg.uri)
            val builder = request.newBuilder()
            with(action) {
                builder.invoke()
            }
            val newRequest = builder.build()
            Assert.assertEquals(
                /* message = */ "itemIndex=$itemIndex, newRequest=$newRequest",
                /* expected = */ newRequest,
                /* actual = */ newRequest.newRequest()
            )
            Assert.assertEquals(
                /* message = */ "itemIndex=$itemIndex, newRequest=$newRequest",
                /* expected = */ newRequest.hashCode(),
                /* actual = */ newRequest.newRequest().hashCode()
            )
            requests.forEachIndexed { lastRequestIndex, lastRequest ->
                Assert.assertNotEquals(
                    /* message = */ "itemIndex=$itemIndex, lastRequestIndex=$lastRequestIndex, lastRequest=$lastRequest, newRequest=$newRequest",
                    /* unexpected = */ lastRequest,
                    /* actual = */ newRequest
                )
                // equals is not the same, hashCode may be the same
            }
            requests.add(newRequest)
        }
    }

    // TODO test mergeComponents
    // TODO test sizeMultiplier
    // TODO test crossfade
}