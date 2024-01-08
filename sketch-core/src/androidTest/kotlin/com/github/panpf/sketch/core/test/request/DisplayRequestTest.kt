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
package com.github.panpf.sketch.core.test.request

import android.R.color
import android.R.drawable
import android.content.Context
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ACES
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.graphics.ColorSpace.Named.BT709
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.DefaultLifecycleResolver
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.ViewLifecycleResolver
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.request.updateDisplayImageOptions
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.CurrentStateImage
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.stateimage.MemoryCacheStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.ThumbnailMemoryCacheStateImage
import com.github.panpf.sketch.target.ImageViewDisplayTarget
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestBitmapDecoder
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDrawableDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestListenerImageView
import com.github.panpf.sketch.test.utils.TestOptionsImageView
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayRequestTest {

    @Test
    fun testFun() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest(context1, uriString1).apply {
            Assert.assertSame(context1, this.context)
            Assert.assertEquals("asset://sample.jpeg", uriString)
            Assert.assertNull(this.listener)
            Assert.assertNull(this.progressListener)
            Assert.assertNull(this.target)
            Assert.assertEquals(
                DefaultLifecycleResolver(LifecycleResolver(GlobalLifecycle)),
                this.lifecycleResolver
            )

            Assert.assertEquals(NETWORK, this.depth)
            Assert.assertNull(this.parameters)
            Assert.assertNull(this.httpHeaders)
            Assert.assertEquals(ENABLED, this.downloadCachePolicy)
            Assert.assertNull(this.bitmapConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertNull(this.colorSpace)
            }
            @Suppress("DEPRECATION")
            Assert.assertFalse(this.preferQualityOverSpeed)
            Assert.assertEquals(DisplaySizeResolver(context1), this.resizeSizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), this.resizePrecisionDecider)
            Assert.assertEquals(FixedScaleDecider(CENTER_CROP), this.resizeScaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertFalse(this.disallowReuseBitmap)
            Assert.assertFalse(this.ignoreExifOrientation)
            Assert.assertEquals(ENABLED, this.resultCachePolicy)
            Assert.assertNull(this.placeholder)
            Assert.assertNull(this.uriEmpty)
            Assert.assertNull(this.error)
            Assert.assertNull(this.transitionFactory)
            Assert.assertFalse(this.disallowAnimatedImage)
            Assert.assertFalse(this.resizeApplyToDrawable)
            Assert.assertEquals(ENABLED, this.memoryCachePolicy)
        }

        val imageView1 = ImageView(context1)
        DisplayRequest(imageView1, uriString1).apply {
            Assert.assertSame(context1, this.context)
            Assert.assertEquals("asset://sample.jpeg", uriString)
            Assert.assertNull(this.listener)
            Assert.assertNull(this.progressListener)
            Assert.assertEquals(ImageViewDisplayTarget(imageView1), this.target)
            Assert.assertEquals(
                DefaultLifecycleResolver(ViewLifecycleResolver(imageView1)),
                this.lifecycleResolver
            )

            Assert.assertEquals(NETWORK, this.depth)
            Assert.assertNull(this.parameters)
            Assert.assertNull(this.httpHeaders)
            Assert.assertEquals(ENABLED, this.downloadCachePolicy)
            Assert.assertNull(this.bitmapConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertNull(this.colorSpace)
            }
            @Suppress("DEPRECATION")
            Assert.assertFalse(this.preferQualityOverSpeed)
            Assert.assertEquals(ViewSizeResolver(imageView1), this.resizeSizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), this.resizePrecisionDecider)
            Assert.assertEquals(FixedScaleDecider(CENTER_CROP), this.resizeScaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertFalse(this.disallowReuseBitmap)
            Assert.assertFalse(this.ignoreExifOrientation)
            Assert.assertEquals(ENABLED, this.resultCachePolicy)
            Assert.assertNull(this.placeholder)
            Assert.assertNull(this.uriEmpty)
            Assert.assertNull(this.error)
            Assert.assertNull(this.transitionFactory)
            Assert.assertFalse(this.disallowAnimatedImage)
            Assert.assertFalse(this.resizeApplyToDrawable)
            Assert.assertEquals(ENABLED, this.memoryCachePolicy)
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @Test
    fun testNewBuilder() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri

        DisplayRequest(context1, uriString1).newBuilder().build().apply {
            Assert.assertEquals(NETWORK, depth)
        }
        DisplayRequest(context1, uriString1).newBuilder {
            depth(LOCAL)
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
        }
        (DisplayRequest(context1, uriString1) as ImageRequest).newBuilder {
            depth(LOCAL)
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
        }

        DisplayRequest(context1, uriString1).newRequest().apply {
            Assert.assertEquals(NETWORK, depth)
        }
        DisplayRequest(context1, uriString1).newRequest {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
        }
        (DisplayRequest(context1, uriString1) as ImageRequest).newRequest {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
        }

        DisplayRequest(context1, uriString1).newDisplayBuilder().build().apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(listener)
            Assert.assertNull(progressListener)
        }
        DisplayRequest(context1, uriString1).newDisplayBuilder {
            depth(LOCAL)
            listener(
                onStart = { request: DisplayRequest ->

                },
                onCancel = { request: DisplayRequest ->

                },
                onError = { request: DisplayRequest, result: DisplayResult.Error ->

                },
                onSuccess = { request: DisplayRequest, result: DisplayResult.Success ->

                },
            )
            progressListener { request: DisplayRequest, totalLength: Long, completedLength: Long ->

            }
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNotNull(listener)
            Assert.assertNotNull(progressListener)
        }

        DisplayRequest(context1, uriString1).newDisplayRequest().apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(listener)
            Assert.assertNull(progressListener)
        }
        DisplayRequest(context1, uriString1).newDisplayRequest {
            depth(LOCAL)
            listener(
                onStart = { request: DisplayRequest ->

                },
                onCancel = { request: DisplayRequest ->

                },
                onError = { request: DisplayRequest, result: DisplayResult.Error ->

                },
                onSuccess = { request: DisplayRequest, result: DisplayResult.Success ->

                },
            )
            progressListener { request: DisplayRequest, totalLength: Long, completedLength: Long ->

            }
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNotNull(listener)
            Assert.assertNotNull(progressListener)
        }
    }

    @Test
    fun testContext() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest(context1, uriString1).apply {
            Assert.assertEquals(context1, context)
            Assert.assertNotEquals(context1, context.applicationContext)
        }
    }

    @Test
    fun testTarget() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        val imageView = TestOptionsImageView(context1)

        DisplayRequest(context1, uriString1).apply {
            Assert.assertNull(target)
        }

        DisplayRequest(imageView, uriString1).apply {
            Assert.assertEquals(ImageViewDisplayTarget(imageView), target)
        }

        imageView.updateDisplayImageOptions {
            memoryCachePolicy(WRITE_ONLY)
        }

        DisplayRequest(imageView, uriString1).apply {
            Assert.assertEquals(ImageViewDisplayTarget(imageView), target)
            Assert.assertEquals(WRITE_ONLY, memoryCachePolicy)
        }

        DisplayRequest(imageView, uriString1) {
            target(null)
        }.apply {
            Assert.assertNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }

        DisplayRequest(imageView, uriString1) {
            target(onStart = {}, onSuccess = {}, onError = {})
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
        DisplayRequest(imageView, uriString1) {
            target(onStart = {})
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
        DisplayRequest(imageView, uriString1) {
            target(onSuccess = {})
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
        DisplayRequest(imageView, uriString1) {
            target(onError = {})
        }.apply {
            Assert.assertNotNull(target)
            Assert.assertEquals(ENABLED, memoryCachePolicy)
        }
    }

    @Test
    fun testLifecycle() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        var lifecycle1: Lifecycle? = null
        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle
                get() = lifecycle1!!
        }
        lifecycle1 = LifecycleRegistry(lifecycleOwner)

        DisplayRequest(context1, uriString1).apply {
            Assert.assertEquals(
                DefaultLifecycleResolver(LifecycleResolver(GlobalLifecycle)),
                this.lifecycleResolver
            )
        }

        DisplayRequest(context1, uriString1) {
            lifecycle(lifecycle1)
        }.apply {
            Assert.assertEquals(LifecycleResolver(lifecycle1), this.lifecycleResolver)
        }

        val activity = TestActivity::class.launchActivity().getActivitySync()

        val imageView = TestOptionsImageView(activity)
        DisplayRequest(imageView, uriString1).apply {
            Assert.assertEquals(
                DefaultLifecycleResolver(ViewLifecycleResolver(imageView)),
                this.lifecycleResolver
            )
        }

        DisplayRequest(activity, uriString1).apply {
            Assert.assertEquals(
                DefaultLifecycleResolver(LifecycleResolver(activity.lifecycle)),
                this.lifecycleResolver
            )
        }
    }

    @Test
    fun testDefinedOptions() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri

        DisplayRequest(context1, uriString1).apply {
            Assert.assertEquals(ImageOptions(), definedOptions)
        }

        DisplayRequest(context1, uriString1) {
            resizeSize(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }.apply {
            Assert.assertEquals(ImageOptions {
                resizeSize(100, 50)
                addTransformations(CircleCropTransformation())
                crossfade()
            }, definedOptions)
        }
    }

    @Test
    fun testDefault() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri

        DisplayRequest(context1, uriString1).apply {
            Assert.assertNull(defaultOptions)
        }

        val options = ImageOptions {
            resizeSize(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }
        DisplayRequest(context1, uriString1) {
            default(options)
        }.apply {
            Assert.assertSame(options, defaultOptions)
        }
    }

    @Test
    fun testMerge() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(NETWORK, depth)
                Assert.assertNull(parameters)
            }

            merge(ImageOptions {
                resizeSize(100, 50)
                memoryCachePolicy(DISABLED)
                addTransformations(CircleCropTransformation())
                crossfade()
            })
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 50), resizeSizeResolver)
                Assert.assertEquals(DISABLED, memoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }

            merge(ImageOptions {
                memoryCachePolicy(READ_ONLY)
            })
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 50), resizeSizeResolver)
                Assert.assertEquals(DISABLED, memoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }
        }
    }

    @Test
    fun testDepth() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
        }

        DisplayRequest(context1, uriString1) {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNull(depthFrom)
        }

        DisplayRequest(context1, uriString1) {
            depth(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
        }

        DisplayRequest(context1, uriString1) {
            depth(LOCAL, null)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNull(depthFrom)
        }

        DisplayRequest(context1, uriString1) {
            depth(null, "TestDepthFrom")
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
        }

        DisplayRequest(context1, uriString1) {
            depth(LOCAL, "TestDepthFrom")
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertEquals("TestDepthFrom", depthFrom)
        }
    }

    @Test
    fun testParameters() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(parameters)
            }

            /* parameters() */
            parameters(Parameters())
            build().apply {
                Assert.assertNull(parameters)
            }

            parameters(Parameters.Builder().set("key1", "value1").build())
            build().apply {
                Assert.assertEquals(1, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
            }

            parameters(null)
            build().apply {
                Assert.assertNull(parameters)
            }

            /* setParameter(), removeParameter() */
            setParameter("key1", "value1")
            setParameter("key2", "value2", "value2")
            build().apply {
                Assert.assertEquals(2, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
                Assert.assertEquals("value2", parameters?.get("key2"))
            }

            setParameter("key2", "value2.1", null)
            build().apply {
                Assert.assertEquals(2, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
                Assert.assertEquals("value2.1", parameters?.get("key2"))
            }

            removeParameter("key2")
            build().apply {
                Assert.assertEquals(1, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
            }

            removeParameter("key1")
            build().apply {
                Assert.assertNull(parameters)
            }
        }
    }

    @Test
    fun testHttpHeaders() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(colorSpace)
            }

            colorSpace(ColorSpace.get(ACES))
            build().apply {
                Assert.assertEquals(ColorSpace.get(ACES), colorSpace)
            }

            colorSpace(ColorSpace.get(BT709))
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(definedOptions.resizeSizeResolver)
                Assert.assertNull(definedOptions.resizePrecisionDecider)
                Assert.assertNull(definedOptions.resizeScaleDecider)
                Assert.assertEquals(DisplaySizeResolver(context1), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.resizeSizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    definedOptions.resizePrecisionDecider
                )
                Assert.assertEquals(
                    FixedScaleDecider(START_CROP),
                    definedOptions.resizeScaleDecider
                )
                Assert.assertEquals(FixedSizeResolver(100, 100), resizeSizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            resize(100, 100)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.resizeSizeResolver)
                Assert.assertNull(definedOptions.resizePrecisionDecider)
                Assert.assertNull(definedOptions.resizeScaleDecider)
                Assert.assertEquals(FixedSizeResolver(100, 100), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, EXACTLY)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.resizeSizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(EXACTLY),
                    definedOptions.resizePrecisionDecider
                )
                Assert.assertNull(definedOptions.resizeScaleDecider)
                Assert.assertEquals(FixedSizeResolver(100, 100), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(100, 100, scale = END_CROP)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.resizeSizeResolver)
                Assert.assertNull(definedOptions.resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), definedOptions.resizeScaleDecider)
                Assert.assertEquals(FixedSizeResolver(100, 100), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), resizeScaleDecider)
            }

            resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                Assert.assertNull(definedOptions.resizeSizeResolver)
                Assert.assertNull(definedOptions.resizePrecisionDecider)
                Assert.assertNull(definedOptions.resizeScaleDecider)
                Assert.assertEquals(DisplaySizeResolver(context1), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.resizeSizeResolver
                )
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    definedOptions.resizePrecisionDecider
                )
                Assert.assertEquals(
                    FixedScaleDecider(START_CROP),
                    definedOptions.resizeScaleDecider
                )
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), resizeSizeResolver)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            resize(Size(100, 100))
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.resizeSizeResolver
                )
                Assert.assertNull(definedOptions.resizePrecisionDecider)
                Assert.assertNull(definedOptions.resizeScaleDecider)
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), EXACTLY)
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.resizeSizeResolver
                )
                Assert.assertEquals(
                    FixedPrecisionDecider(EXACTLY),
                    definedOptions.resizePrecisionDecider
                )
                Assert.assertNull(definedOptions.resizeScaleDecider)
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(Size(100, 100), scale = END_CROP)
            build().apply {
                Assert.assertEquals(
                    FixedSizeResolver(Size(100, 100)),
                    definedOptions.resizeSizeResolver
                )
                Assert.assertNull(definedOptions.resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), definedOptions.resizeScaleDecider)
                Assert.assertEquals(FixedSizeResolver(Size(100, 100)), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(END_CROP), resizeScaleDecider)
            }

            resize(Size(100, 100), SAME_ASPECT_RATIO, START_CROP)
            resize(null)
            build().apply {
                Assert.assertNull(definedOptions.resizeSizeResolver)
                Assert.assertNull(definedOptions.resizePrecisionDecider)
                Assert.assertNull(definedOptions.resizeScaleDecider)
                Assert.assertEquals(DisplaySizeResolver(context1), resizeSizeResolver)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }
        }
    }

    @Test
    fun testResizeSize() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(definedOptions.resizeSizeResolver)
                Assert.assertEquals(DisplaySizeResolver(context1), resizeSizeResolver)
            }

            resizeSize(Size(100, 100))
            build().apply {
                Assert.assertEquals(FixedSizeResolver(100, 100), definedOptions.resizeSizeResolver)
                Assert.assertEquals(FixedSizeResolver(100, 100), resizeSizeResolver)
            }

            resizeSize(200, 200)
            build().apply {
                Assert.assertEquals(FixedSizeResolver(200, 200), definedOptions.resizeSizeResolver)
                Assert.assertEquals(FixedSizeResolver(200, 200), resizeSizeResolver)
            }

            resizeSize(FixedSizeResolver(300, 200))
            build().apply {
                Assert.assertEquals(FixedSizeResolver(300, 200), definedOptions.resizeSizeResolver)
                Assert.assertEquals(FixedSizeResolver(300, 200), resizeSizeResolver)
            }

            resizeSize(null)
            build().apply {
                Assert.assertNull(definedOptions.resizeSizeResolver)
                Assert.assertEquals(DisplaySizeResolver(context1), resizeSizeResolver)
            }
        }
    }

    @Test
    fun testResizePrecision() {
        val (context, sketch) = getTestContextAndNewSketch()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context, uriString1).apply {
            build().apply {
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
            }

            resizePrecision(LongImageClipPrecisionDecider(EXACTLY))
            build().apply {
                Assert.assertEquals(LongImageClipPrecisionDecider(EXACTLY), resizePrecisionDecider)
            }

            resizePrecision(SAME_ASPECT_RATIO)
            build().apply {
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
            }

            resizePrecision(null)
            build().apply {
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
            }
        }

        val request = DisplayRequest(context, uriString1).apply {
            Assert.assertEquals(DisplaySizeResolver(context), resizeSizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }
        val size = runBlocking {
            DisplaySizeResolver(context).size()
        }
        val request1 = request.newDisplayRequest {
            resizeSize(size)
        }.apply {
            Assert.assertEquals(FixedSizeResolver(size), resizeSizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }
        request1.newDisplayRequest().apply {
            Assert.assertEquals(FixedSizeResolver(size), resizeSizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }

        request.apply {
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }
        runBlocking { sketch.execute(request) }.apply {
            Assert.assertEquals(
                FixedPrecisionDecider(LESS_PIXELS),
                this.request.resizePrecisionDecider
            )
        }
    }

    @Test
    fun testResizeScale() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resizeScale(LongImageScaleDecider(START_CROP, END_CROP))
            build().apply {
                Assert.assertEquals(LongImageScaleDecider(START_CROP, END_CROP), resizeScaleDecider)
            }

            resizeScale(FILL)
            build().apply {
                Assert.assertEquals(FixedScaleDecider(FILL), resizeScaleDecider)
            }

            resizeScale(null)
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            target(ImageView(context1))
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.CENTER_CROP
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.CENTER
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.CENTER_INSIDE
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.FIT_END
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(END_CROP), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.FIT_CENTER
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.FIT_START
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.FIT_XY
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(FILL), resizeScaleDecider)
            }

            target(ImageView(context1).apply {
                scaleType = ScaleType.MATRIX
            })
            build().apply {
                Assert.assertEquals(FixedScaleDecider(FILL), resizeScaleDecider)
            }
        }
    }

    @Test
    fun testTransformations() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
    fun testDisallowReuseBitmap() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertFalse(disallowReuseBitmap)
            }

            disallowReuseBitmap()
            build().apply {
                Assert.assertEquals(true, disallowReuseBitmap)
            }

            disallowReuseBitmap(false)
            build().apply {
                Assert.assertEquals(false, disallowReuseBitmap)
            }

            disallowReuseBitmap(null)
            build().apply {
                Assert.assertFalse(disallowReuseBitmap)
            }
        }
    }

    @Test
    fun testIgnoreExifOrientation() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertFalse(ignoreExifOrientation)
            }

            ignoreExifOrientation(true)
            build().apply {
                Assert.assertEquals(true, ignoreExifOrientation)
            }

            ignoreExifOrientation(false)
            build().apply {
                Assert.assertEquals(false, ignoreExifOrientation)
            }

            ignoreExifOrientation(null)
            build().apply {
                Assert.assertFalse(ignoreExifOrientation)
            }
        }
    }

    @Test
    fun testResultCachePolicy() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(placeholder)
            }

            placeholder(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(ColorStateImage(IntColor(Color.BLUE)), placeholder)
            }

            placeholder(ColorDrawable(Color.GREEN))
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
    fun testUriEmpty() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(uriEmpty)
            }

            uriEmpty(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(ColorStateImage(IntColor(Color.BLUE)), uriEmpty)
            }

            uriEmpty(ColorDrawable(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, uriEmpty is DrawableStateImage)
            }

            uriEmpty(drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    DrawableStateImage(drawable.bottom_bar),
                    uriEmpty
                )
            }

            uriEmpty(null)
            build().apply {
                Assert.assertNull(uriEmpty)
            }
        }
    }

    @Test
    fun testError() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(error)
            }

            error(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(ColorStateImage(IntColor(Color.BLUE))),
                    error
                )
            }

            error(ColorDrawable(Color.GREEN))
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
                uriEmptyError(drawable.alert_dark_frame)
            }
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(DrawableStateImage(drawable.bottom_bar)) {
                        uriEmptyError(drawable.alert_dark_frame)
                    },
                    error
                )
            }

            error()
            build().apply {
                Assert.assertNull(error)
            }

            error {
                uriEmptyError(drawable.btn_dialog)
            }
            build().apply {
                Assert.assertNotNull(error)
            }
        }
    }

    @Test
    fun testTransitionFactory() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(transitionFactory)
            }

            transitionFactory(CrossfadeTransition.Factory())
            build().apply {
                Assert.assertEquals(CrossfadeTransition.Factory(), transitionFactory)
            }

            transitionFactory(null)
            build().apply {
                Assert.assertNull(transitionFactory)
            }
        }
    }

    @Test
    fun testResizeApplyToDrawable() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertFalse(resizeApplyToDrawable)
            }

            resizeApplyToDrawable()
            build().apply {
                Assert.assertEquals(true, resizeApplyToDrawable)
            }

            resizeApplyToDrawable(false)
            build().apply {
                Assert.assertEquals(false, resizeApplyToDrawable)
            }

            resizeApplyToDrawable(null)
            build().apply {
                Assert.assertFalse(resizeApplyToDrawable)
            }
        }
    }

    @Test
    fun testMemoryCachePolicy() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(listener)
                Assert.assertNull(target)
            }

            listener(onStart = {}, onCancel = {}, onError = { _, _ -> }, onSuccess = { _, _ -> })
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener !is CombinedListener<*, *, *>)
            }
            build().newDisplayRequest().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener !is CombinedListener<*, *, *>)
            }

            listener(onStart = {})
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener !is CombinedListener<*, *, *>)
            }

            listener(onCancel = {})
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener !is CombinedListener<*, *, *>)
            }

            listener(onError = { _, _ -> })
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener !is CombinedListener<*, *, *>)
            }

            listener(onSuccess = { _, _ -> })
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener !is CombinedListener<*, *, *>)
            }

            listener(null)
            target(null)
            build().apply {
                Assert.assertNull(listener)
            }

            target(ImageView(context1))
            build().apply {
                Assert.assertNull(listener)
            }

            listener(onSuccess = { _, _ -> })
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener !is CombinedListener<*, *, *>)
            }

            listener(null)
            target(null)
            build().apply {
                Assert.assertNull(listener)
            }

            target(TestListenerImageView(context1))
            build().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNull(fromBuilderListener)
                Assert.assertNull(fromBuilderListeners)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }
            build().newDisplayRequest().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNull(fromBuilderListener)
                Assert.assertNull(fromBuilderListeners)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }

            listener(onSuccess = { _, _ -> })
            build().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNotNull(fromBuilderListener)
                Assert.assertNull(fromBuilderListeners)
                Assert.assertTrue(fromBuilderListener !is CombinedListener<*, *, *>)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }
            build().newDisplayRequest().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNotNull(fromBuilderListener)
                Assert.assertNull(fromBuilderListeners)
                Assert.assertTrue(fromBuilderListener !is CombinedListener<*, *, *>)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }

            val listener2 =
                object : Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error> {}
            val listener3 =
                object : Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error> {}
            addListener(listener2)
            addListener(listener3)
            addListener(listener2)
            build().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNotNull(fromBuilderListener)
                Assert.assertNotNull(fromBuilderListeners)
                Assert.assertTrue(fromBuilderListeners!!.size == 2)
                Assert.assertTrue(fromBuilderListener !is CombinedListener<*, *, *>)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }
            build().newDisplayRequest().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNotNull(fromBuilderListener)
                Assert.assertNotNull(fromBuilderListeners)
                Assert.assertTrue(fromBuilderListeners!!.size == 2)
                Assert.assertTrue(fromBuilderListener !is CombinedListener<*, *, *>)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }

            removeListener(listener2)
            build().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNotNull(fromBuilderListener)
                Assert.assertNotNull(fromBuilderListeners)
                Assert.assertTrue(fromBuilderListeners!!.size == 1)
                Assert.assertTrue(fromBuilderListener !is CombinedListener<*, *, *>)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }
            build().newDisplayRequest().listener!!.asOrThrow<CombinedListener<*, *, *>>().apply {
                Assert.assertNotNull(fromBuilderListener)
                Assert.assertNotNull(fromBuilderListeners)
                Assert.assertTrue(fromBuilderListeners!!.size == 1)
                Assert.assertTrue(fromBuilderListener !is CombinedListener<*, *, *>)
                Assert.assertNotNull(fromProviderListener)
                Assert.assertTrue(fromProviderListener !is CombinedListener<*, *, *>)
            }
        }
    }

    @Test
    fun testProgressListener() {
        val context1 = getTestContext()
        val uriString1 = AssetImages.jpeg.uri
        DisplayRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(progressListener)
                Assert.assertNull(target)
            }

            progressListener { _, _, _ -> }
            build().apply {
                Assert.assertNotNull(progressListener)
                Assert.assertTrue(progressListener !is CombinedProgressListener<*>)
            }
            build().newDisplayRequest().apply {
                Assert.assertNotNull(progressListener)
                Assert.assertTrue(progressListener !is CombinedProgressListener<*>)
            }

            progressListener(null)
            target(null)
            build().apply {
                Assert.assertNull(progressListener)
            }

            target(ImageView(context1))
            build().apply {
                Assert.assertNull(progressListener)
            }

            progressListener { _, _, _ -> }
            build().apply {
                Assert.assertNotNull(progressListener)
                Assert.assertTrue(progressListener !is CombinedProgressListener<*>)
            }

            progressListener(null)
            target(null)
            build().apply {
                Assert.assertNull(progressListener)
            }

            target(TestListenerImageView(context1))
            build().progressListener!!.asOrThrow<CombinedProgressListener<*>>().apply {
                Assert.assertNull(fromBuilderProgressListener)
                Assert.assertNull(fromBuilderProgressListeners)
                Assert.assertNotNull(fromProviderProgressListener)
                Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
            }
            build().newDisplayRequest().progressListener!!.asOrThrow<CombinedProgressListener<*>>()
                .apply {
                    Assert.assertNull(fromBuilderProgressListener)
                    Assert.assertNull(fromBuilderProgressListeners)
                    Assert.assertNotNull(fromProviderProgressListener)
                    Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
                }

            progressListener { _, _, _ -> }
            build().progressListener!!.asOrThrow<CombinedProgressListener<*>>().apply {
                Assert.assertNotNull(fromBuilderProgressListener)
                Assert.assertNull(fromBuilderProgressListeners)
                Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener<*>)
                Assert.assertNotNull(fromProviderProgressListener)
                Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
            }
            build().newDisplayRequest().progressListener!!.asOrThrow<CombinedProgressListener<*>>()
                .apply {
                    Assert.assertNotNull(fromBuilderProgressListener)
                    Assert.assertNull(fromBuilderProgressListeners)
                    Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener<*>)
                    Assert.assertNotNull(fromProviderProgressListener)
                    Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
                }

            val progressListener2 = ProgressListener<DisplayRequest> { _, _, _ -> }
            val progressListener3 = ProgressListener<DisplayRequest> { _, _, _ -> }
            addProgressListener(progressListener2)
            addProgressListener(progressListener3)
            addProgressListener(progressListener2)
            build().progressListener!!.asOrThrow<CombinedProgressListener<*>>().apply {
                Assert.assertNotNull(fromBuilderProgressListener)
                Assert.assertNotNull(fromBuilderProgressListeners)
                Assert.assertTrue(fromBuilderProgressListeners!!.size == 2)
                Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener<*>)
                Assert.assertNotNull(fromProviderProgressListener)
                Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
            }
            build().newDisplayRequest().progressListener!!.asOrThrow<CombinedProgressListener<*>>()
                .apply {
                    Assert.assertNotNull(fromBuilderProgressListener)
                    Assert.assertNotNull(fromBuilderProgressListeners)
                    Assert.assertTrue(fromBuilderProgressListeners!!.size == 2)
                    Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener<*>)
                    Assert.assertNotNull(fromProviderProgressListener)
                    Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
                }

            removeProgressListener(progressListener2)
            build().progressListener!!.asOrThrow<CombinedProgressListener<*>>().apply {
                Assert.assertNotNull(fromBuilderProgressListener)
                Assert.assertNotNull(fromBuilderProgressListeners)
                Assert.assertTrue(fromBuilderProgressListeners!!.size == 1)
                Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener<*>)
                Assert.assertNotNull(fromProviderProgressListener)
                Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
            }
            build().newDisplayRequest().progressListener!!.asOrThrow<CombinedProgressListener<*>>()
                .apply {
                    Assert.assertNotNull(fromBuilderProgressListener)
                    Assert.assertNotNull(fromBuilderProgressListeners)
                    Assert.assertTrue(fromBuilderProgressListeners!!.size == 1)
                    Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener<*>)
                    Assert.assertNotNull(fromProviderProgressListener)
                    Assert.assertTrue(fromProviderProgressListener !is CombinedProgressListener<*>)
                }
        }
    }

    @Test
    fun testComponents() {
        val context = getTestContext()
        DisplayRequest(context, AssetImages.jpeg.uri).apply {
            Assert.assertNull(componentRegistry)
        }

        DisplayRequest(context, AssetImages.jpeg.uri) {
            components {
                addFetcher(HttpUriFetcher.Factory())
                addFetcher(TestFetcher.Factory())
                addBitmapDecoder(DefaultBitmapDecoder.Factory())
                addBitmapDecoder(TestBitmapDecoder.Factory())
                addDrawableDecoder(DefaultDrawableDecoder.Factory())
                addDrawableDecoder(TestDrawableDecoder.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
            }
        }.apply {
            Assert.assertEquals(
                ComponentRegistry.Builder().apply {
                    addFetcher(HttpUriFetcher.Factory())
                    addFetcher(TestFetcher.Factory())
                    addBitmapDecoder(DefaultBitmapDecoder.Factory())
                    addBitmapDecoder(TestBitmapDecoder.Factory())
                    addDrawableDecoder(DefaultDrawableDecoder.Factory())
                    addDrawableDecoder(TestDrawableDecoder.Factory())
                    addRequestInterceptor(TestRequestInterceptor())
                    addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
                    addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
                }.build(),
                componentRegistry
            )
        }
    }

    @Test
    fun testUnknownImpl() {
        val context = getTestContext()
        assertThrow(UnsupportedOperationException::class) {
            MyImageRequestBuilder(context, AssetImages.jpeg.uri).build()
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = DisplayRequest(context, AssetImages.jpeg.uri)
        val element11 = DisplayRequest(context, AssetImages.jpeg.uri)
        val element2 = DisplayRequest(context, AssetImages.png.uri)

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
        DisplayRequest(context, AssetImages.jpeg.uri).apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            listener(onStart = {})
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            progressListener { _, _, _ -> }
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            target(imageView)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            default(ImageOptions())
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            depth(LOCAL, "test")
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            setParameter("type", "list")
            setParameter("big", "true")
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            setHttpHeader("from", "china")
            setHttpHeader("job", "Programmer")
            addHttpHeader("Host", "www.google.com")
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            downloadCachePolicy(READ_ONLY)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            bitmapConfig(RGB_565)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                colorSpace(ColorSpace.get(ADOBE_RGB))
            }
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resizeSize(300, 200)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resizeSize(ViewSizeResolver(imageView))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resizePrecision(EXACTLY)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resizePrecision(LongImageClipPrecisionDecider())
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resizeScale(END_CROP)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resizeScale(LongImageScaleDecider())
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            transformations(CircleCropTransformation(), BlurTransformation())
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            disallowReuseBitmap(true)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            ignoreExifOrientation(true)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resultCachePolicy(WRITE_ONLY)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            placeholder(IconStateImage(drawable.ic_delete) {
                resColorBackground(color.background_dark)
            })
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            placeholder(ColorStateImage(Color.BLUE))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            placeholder(ColorStateImage(ResColor(color.background_dark)))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            val drawable = ResourcesCompat.getDrawable(context.resources, drawable.ic_delete, null)
            placeholder(DrawableStateImage(drawable!!))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            placeholder(DrawableStateImage(drawable.ic_delete))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            placeholder(CurrentStateImage(drawable.ic_delete))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            placeholder(MemoryCacheStateImage("uri", ColorStateImage(Color.BLUE)))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            placeholder(ThumbnailMemoryCacheStateImage("uri", ColorStateImage(Color.BLUE)))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            error(DrawableStateImage(drawable.ic_delete)) {
                uriEmptyError(ColorStateImage(Color.BLUE))
            }
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            transitionFactory(CrossfadeTransition.Factory(fadeStart = false, alwaysUse = true))
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            disallowAnimatedImage(true)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            resizeApplyToDrawable(true)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }.newDisplayRequest {
            components {
                addFetcher(TestFetcher.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
                addDrawableDecoder(TestDrawableDecoder.Factory())
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
                addBitmapDecoder(TestBitmapDecoder.Factory())
            }
        }.apply {
            Assert.assertEquals(this, this.newDisplayRequest())
        }
    }

    class MyImageRequestBuilder(context: Context, uriString: String) :
        ImageRequest.Builder(context, uriString)
}