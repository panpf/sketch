@file:Suppress("DEPRECATION")

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
package com.github.panpf.sketch.core.android.test.request.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DefaultLifecycleResolver
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.request.GlobalTargetLifecycle
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.cache.memoryCacheKey
import com.github.panpf.sketch.cache.resultCacheKey
import com.github.panpf.sketch.size
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.lifecycle
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.target.RealTargetLifecycle
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.singleton.request.execute
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.MediumImageViewTestActivity
import com.github.panpf.sketch.test.utils.ProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.TestAssetFetcherFactory
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestCountTarget
import com.github.panpf.sketch.test.utils.TestErrorDecoder
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.TestTransitionViewTarget
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.test.utils.ratio
import com.github.panpf.sketch.test.utils.target
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.getCircleCropTransformed
import com.github.panpf.sketch.transform.getRotateTransformed
import com.github.panpf.sketch.transform.getRoundedCornersTransformed
import com.github.panpf.sketch.transition.ViewCrossfadeTransition
import com.github.panpf.sketch.util.ColorDrawableEqualizer
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageRequestExecuteTest {

    @Test
    fun testDepth() {
        val context = getTestContext()
        val sketch = newSketch {
            httpStack(TestHttpStack(context))
        }
        val imageUri = TestHttpStack.testImages.first().uri

        // default
        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            target(TestCountTarget())
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        // NETWORK
        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(NETWORK)
            target(TestCountTarget())
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        // LOCAL
        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        runBlocking {
            sketch.execute(ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                target(TestCountTarget())
            })
        }
        sketch.memoryCache.clear()
        Assert.assertTrue(sketch.downloadCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(LOCAL)
            target(TestCountTarget())
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
        }

        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(LOCAL)
            target(TestCountTarget())
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Error>()!!.apply {
            Assert.assertTrue(throwable is DepthException)
        }

        // MEMORY
        sketch.memoryCache.clear()
        runBlocking {
            sketch.execute(ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                target(TestCountTarget())
            })
        }
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
            target(TestCountTarget())
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        sketch.memoryCache.clear()
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
            target(TestCountTarget())
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Error>()!!.apply {
            Assert.assertTrue(throwable is DepthException)
        }
    }

    @Test
    fun testDownloadCachePolicy() {
        val context = getTestContext()
        val sketch = newSketch {
            httpStack(TestHttpStack(context))
        }
        val diskCache = sketch.downloadCache
        val imageUri = TestHttpStack.testImages.first().uri

        /* ENABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }
    }

    @Test
    fun testBitmapConfig() {
        val context = getTestContext()
        val sketch = newSketch()

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
            }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ARGB_8888)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
            }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            @Suppress("DEPRECATION")
            bitmapConfig(ARGB_4444)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                if (VERSION.SDK_INT > VERSION_CODES.M) {
                    Assert.assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, image.getBitmapOrThrow().config)
                }
            }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ALPHA_8)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
            }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(RGB_565)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(RGB_565, image.getBitmapOrThrow().config)
            }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageRequest(context, MyImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(RGBA_F16)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<ImageResult.Success>()!!.apply {
                    Assert.assertEquals(RGBA_F16, image.getBitmapOrThrow().config)
                }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageRequest(context, MyImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(HARDWARE)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<ImageResult.Success>()!!.apply {
                    Assert.assertEquals(HARDWARE, image.getBitmapOrThrow().config)
                }
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(RGB_565, image.getBitmapOrThrow().config)
            }
        ImageRequest(context, MyImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, image.getBitmapOrThrow().config)
                }
            }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(RGBA_F16, image.getBitmapOrThrow().config)
                } else {
                    Assert.assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
                }
            }
        ImageRequest(context, MyImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(RGBA_F16, image.getBitmapOrThrow().config)
                } else {
                    Assert.assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
                }
            }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context = getTestContext()
        val sketch = newSketch()

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.SRGB).name,
                    image.getBitmapOrThrow().colorSpace!!.name
                )
            }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.Named.ADOBE_RGB)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.ADOBE_RGB).name,
                    image.getBitmapOrThrow().colorSpace!!.name
                )
            }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.Named.DISPLAY_P3)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.DISPLAY_P3).name,
                    image.getBitmapOrThrow().colorSpace!!.name
                )
            }
    }

    @Test
    fun testPreferQualityOverSpeed() {
        val context = getTestContext()
        val sketch = newSketch()

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(true)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(false)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }
    }

    @Test
    fun testResize() {
        val (context, sketch) = getTestContextAndSketch()
        val imageUri = MyImages.jpeg.uri
        val imageSize = Size(1291, 1936)

        // default
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }
            .let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    imageSize,
                    image.size
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.size.ratio
                )
            }

        // size: small, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val smallSize1 = Size(600, 500)
        ImageRequest(context, imageUri) {
            size(smallSize1)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(323, 484),
                    image.size
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(smallSize1)
            precision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.size
                )
                Assert.assertEquals(
                    smallSize1.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(smallSize1)
            precision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    smallSize1,
                    image.size
                )
            }

        val smallSize2 = Size(500, 600)
        ImageRequest(context, imageUri) {
            size(smallSize2)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(323, 484),
                    image.size
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(smallSize2)
            precision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 387),
                    image.size
                )
                Assert.assertEquals(
                    smallSize2.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(smallSize2)
            precision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    smallSize2,
                    image.size
                )
            }

        // size: same, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val sameSize = Size(imageSize.width, imageSize.height)
        ImageRequest(context, imageUri) {
            size(sameSize)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    sameSize,
                    image.size
                )
            }
        ImageRequest(context, imageUri) {
            size(sameSize)
            precision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    sameSize,
                    image.size
                )
            }
        ImageRequest(context, imageUri) {
            size(sameSize)
            precision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    sameSize,
                    image.size
                )
            }

        // size: big, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val bigSize1 = Size(2500, 2100)
        ImageRequest(context, imageUri) {
            size(bigSize1)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    imageSize,
                    image.size
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize1)
            precision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(1291, 1084),
                    image.size
                )
                Assert.assertEquals(
                    bigSize1.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize1)
            precision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize1,
                    image.size
                )
            }

        val bigSize2 = Size(2100, 2500)
        ImageRequest(context, imageUri) {
            size(bigSize2)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    imageSize,
                    image.size
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize2)
            precision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(1291, 1537),
                    image.size
                )
                Assert.assertEquals(
                    bigSize2.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize2)
            precision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize2,
                    image.size
                )
            }

        val bigSize3 = Size(800, 2500)
        ImageRequest(context, imageUri) {
            size(bigSize3)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(646, 968),
                    image.size
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize3)
            precision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(620, 1936),
                    image.size
                )
                Assert.assertEquals(
                    bigSize3.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize3)
            precision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize3,
                    image.size
                )
            }

        val bigSize4 = Size(2500, 800)
        ImageRequest(context, imageUri) {
            size(bigSize4)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(646, 968),
                    image.size
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize4)
            precision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(1291, 413),
                    image.size
                )
                Assert.assertEquals(
                    bigSize4.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(bigSize4)
            precision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize4,
                    image.size
                )
            }

        /* scale */
        val size = Size(600, 500)
        var sarStartCropBitmap: Bitmap?
        var sarCenterCropBitmap: Bitmap?
        var sarEndCropBitmap: Bitmap?
        var sarFillCropBitmap: Bitmap?
        ImageRequest(context, imageUri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarStartCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.size
                )
                Assert.assertEquals(
                    size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarCenterCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.size
                )
                Assert.assertEquals(
                    size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarEndCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.size
                )
                Assert.assertEquals(
                    size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, imageUri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarFillCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    if (VERSION.SDK_INT >= 24)
                        Size(323, 269) else Size(322, 268),
                    image.size
                )
                Assert.assertEquals(
                    size.ratio,
                    image.size.ratio
                )
            }
        Assert.assertNotEquals(sarStartCropBitmap!!.corners(), sarCenterCropBitmap!!.corners())
        Assert.assertNotEquals(sarStartCropBitmap!!.corners(), sarEndCropBitmap!!.corners())
        Assert.assertNotEquals(sarStartCropBitmap!!.corners(), sarFillCropBitmap!!.corners())
        Assert.assertNotEquals(sarCenterCropBitmap!!.corners(), sarEndCropBitmap!!.corners())
        Assert.assertNotEquals(sarCenterCropBitmap!!.corners(), sarFillCropBitmap!!.corners())
        Assert.assertNotEquals(sarEndCropBitmap!!.corners(), sarFillCropBitmap!!.corners())

        var exactlyStartCropBitmap: Bitmap?
        var exactlyCenterCropBitmap: Bitmap?
        var exactlyEndCropBitmap: Bitmap?
        var exactlyFillCropBitmap: Bitmap?
        ImageRequest(context, imageUri) {
            size(size)
            precision(EXACTLY)
            scale(START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyStartCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.size)
            }
        ImageRequest(context, imageUri) {
            size(size)
            precision(EXACTLY)
            scale(CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyCenterCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.size)
            }
        ImageRequest(context, imageUri) {
            size(size)
            precision(EXACTLY)
            scale(END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyEndCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.size)
            }
        ImageRequest(context, imageUri) {
            size(size)
            precision(EXACTLY)
            scale(FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyFillCropBitmap = image.getBitmapOrThrow()
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.size)
            }
        Assert.assertNotEquals(
            exactlyStartCropBitmap!!.corners(),
            exactlyCenterCropBitmap!!.corners()
        )
        Assert.assertNotEquals(exactlyStartCropBitmap!!.corners(), exactlyEndCropBitmap!!.corners())
        Assert.assertNotEquals(
            exactlyStartCropBitmap!!.corners(),
            exactlyFillCropBitmap!!.corners()
        )
        Assert.assertNotEquals(
            exactlyCenterCropBitmap!!.corners(),
            exactlyEndCropBitmap!!.corners()
        )
        Assert.assertNotEquals(
            exactlyCenterCropBitmap!!.corners(),
            exactlyFillCropBitmap!!.corners()
        )
        Assert.assertNotEquals(exactlyEndCropBitmap!!.corners(), exactlyFillCropBitmap!!.corners())
    }

    @Test
    fun testTransformations() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = MyImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }

        request.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertTrue(transformedList?.all {
                    it.startsWith("ResizeTransformed") || it.startsWith("InSampledTransformed")
                } != false)
            }

        request.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertNotEquals(
                    listOf(0, 0, 0, 0),
                    image.getBitmapOrThrow().corners()
                )
                Assert.assertNull(
                    transformedList?.getRoundedCornersTransformed()
                )
            }
        request.newRequest {
            addTransformations(RoundedCornersTransformation(30f))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(
                    listOf(0, 0, 0, 0),
                    image.getBitmapOrThrow().corners()
                )
                Assert.assertNotNull(
                    transformedList?.getRoundedCornersTransformed()
                )
            }

        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(Size(323, 484), Size(image.width, image.height))
                Assert.assertNull(
                    transformedList?.getRotateTransformed()
                )
            }
        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
            addTransformations(RotateTransformation(90))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(Size(484, 323), Size(image.width, image.height))
                Assert.assertNotNull(
                    transformedList?.getRotateTransformed()
                )
            }

        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(Size(323, 484), Size(image.width, image.height))
                Assert.assertNotEquals(
                    listOf(0, 0, 0, 0),
                    image.getBitmapOrThrow().corners()
                )
                Assert.assertNull(
                    transformedList?.getCircleCropTransformed()
                )
            }
        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
            addTransformations(CircleCropTransformation())
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(Size(323, 323), Size(image.width, image.height))
                Assert.assertEquals(
                    listOf(0, 0, 0, 0),
                    image.getBitmapOrThrow().corners()
                )
                Assert.assertNotNull(
                    transformedList?.getCircleCropTransformed()
                )
            }
    }

    @Test
    fun testResultCachePolicy() = runTest {
        val context = getTestContext()
        val sketch = newSketch()
        val diskCache = sketch.resultCache
        val imageUri = MyImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            size(500, 500)
        }
        val resultCacheKey = request.toRequestContext(sketch).resultCacheKey

        /* ENABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testPlaceholder() = runTest {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = MyImages.jpeg.uri
        var onStartImage: Image?
        val request = ImageRequest(context, imageUri) {
            size(500, 500)
            target(onStart =  { _, placeholder: Image? ->
                onStartImage = placeholder
            }
            )
        }
        val memoryCacheKey = request.toRequestContext(sketch).memoryCacheKey
        val memoryCache = sketch.memoryCache
        val colorDrawable = ColorDrawableEqualizer(Color.BLUE)

        memoryCache.clear()
        onStartImage = null
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest()
            .let { runBlocking { sketch.execute(it) } }
        Assert.assertNull(onStartImage)

        onStartImage = null
        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            placeholder(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNull(onStartImage)
        Assert.assertFalse(onStartImage?.asOrThrow<AndroidDrawableImage>()?.drawable === colorDrawable.wrapped)

        onStartImage = null
        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(DISABLED)
            placeholder(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNotNull(onStartImage)
        Assert.assertTrue(onStartImage?.asOrThrow<AndroidDrawableImage>()?.drawable === colorDrawable.wrapped)
    }

    @Test
    fun testError() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = MyImages.jpeg.uri
        var onErrorImage: Image?
        val request = ImageRequest(context, imageUri) {
            size(500, 500)
            target(
                onError = { _, image ->
                    onErrorImage = image
                }
            )
        }
        val errorRequest = ImageRequest(context, newAssetUri("error.jpeg")) {
            size(500, 500)
            target(
                onError = { _, image ->
                    onErrorImage = image
                }
            )
        }
        val colorDrawable = ColorDrawableEqualizer(Color.BLUE)

        onErrorImage = null
        request.newRequest()
            .let { runBlocking { sketch.execute(it) } }
        Assert.assertNull(onErrorImage)

        onErrorImage = null
        request.newRequest {
            error(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNull(onErrorImage)

        onErrorImage = null
        errorRequest.newRequest()
            .let { runBlocking { sketch.execute(it) } }
        Assert.assertNull(onErrorImage)

        onErrorImage = null
        errorRequest.newRequest {
            error(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNotNull(onErrorImage)
        Assert.assertTrue(onErrorImage?.asOrThrow<AndroidDrawableImage>()?.drawable === colorDrawable.wrapped)

        onErrorImage = null
        errorRequest.newRequest {
            placeholder(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNotNull(onErrorImage)
        Assert.assertTrue(onErrorImage?.asOrThrow<AndroidDrawableImage>()?.drawable === colorDrawable.wrapped)
    }

    @Test
    fun testTransition() = runTest {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = MyImages.jpeg.uri
        val testTarget = TestTransitionViewTarget()
        val request = ImageRequest(context, imageUri) {
            size(500, 500)
            target(testTarget)
        }
        val memoryCache = sketch.memoryCache
        val memoryCacheKey = request.toRequestContext(sketch).memoryCacheKey

        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest()
            .let { runBlocking { sketch.enqueue(it).job.join() } }
        Assert.assertFalse(testTarget.drawable!! is CrossfadeDrawable)

        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            transitionFactory(ViewCrossfadeTransition.Factory())
        }.let { runBlocking { sketch.enqueue(it).job.join() } }
        Assert.assertFalse(testTarget.drawable!! is CrossfadeDrawable)

        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            transitionFactory(ViewCrossfadeTransition.Factory())
        }.let { runBlocking { sketch.enqueue(it).job.join() } }
        Assert.assertTrue(testTarget.drawable!! is CrossfadeDrawable)
    }

    @Test
    fun testResizeOnDraw() {
        val sketch = newSketch()
        val imageUri = MyImages.jpeg.uri
        val activity = MediumImageViewTestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView
        val request = ImageRequest(imageView, imageUri) {
            size(500, 500)
        }

        request.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertTrue(this.image is AndroidBitmapImage)
            }

        request.newRequest {
            resizeOnDraw(false)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertTrue(this.image is AndroidBitmapImage)
            }

        request.newRequest {
            resizeOnDraw(null)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertTrue(this.image is AndroidBitmapImage)
            }

        request.newRequest {
            resizeOnDraw(true)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertTrue(image.asOrThrow<AndroidDrawableImage>().drawable is ResizeDrawable)
            }
    }

    @Test
    fun testMemoryCachePolicy() = runTest {
        val context = getTestContext()
        val sketch = newSketch()
        val memoryCache = sketch.memoryCache
        val imageUri = MyImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            size(500, 500)
            target(TestCountTarget())
        }
        val memoryCacheKey = request.toRequestContext(sketch).memoryCacheKey

        /* ENABLED */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        /* DISABLED */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testListener() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = MyImages.jpeg.uri
        val errorImageUri = MyImages.jpeg.uri + ".fake"

        ListenerSupervisor().let { listenerSupervisor ->
            Assert.assertEquals(listOf<String>(), listenerSupervisor.callbackActionList)

            ImageRequest(context, imageUri) {
                registerListener(listenerSupervisor)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertEquals(
                listOf("onStart", "onSuccess"),
                listenerSupervisor.callbackActionList
            )
        }

        ListenerSupervisor().let { listenerSupervisor ->
            Assert.assertEquals(listOf<String>(), listenerSupervisor.callbackActionList)

            ImageRequest(context, errorImageUri) {
                registerListener(listenerSupervisor)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor.callbackActionList)
        }

        var deferred: Deferred<ImageResult>? = null
        val listenerSupervisor = ListenerSupervisor {
            deferred?.cancel()
        }
        ImageRequest(context, MyImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            registerListener(listenerSupervisor)
        }.let { request ->
            runBlocking {
                deferred = async {
                    sketch.execute(request)
                }
                deferred?.join()
            }
        }
        Assert.assertEquals(listOf("onStart", "onCancel"), listenerSupervisor.callbackActionList)
    }

    @Test
    fun testProgressListener() {
        val context = getTestContext()
        val sketch = newSketch {
            httpStack(TestHttpStack(context, 20))
        }
        val testImage = TestHttpStack.testImages.first()

        ProgressListenerSupervisor().let { listenerSupervisor ->
            Assert.assertEquals(listOf<String>(), listenerSupervisor.callbackActionList)

            ImageRequest(context, testImage.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
                registerProgressListener(listenerSupervisor)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }

            Assert.assertTrue(listenerSupervisor.callbackActionList.size > 1)
            listenerSupervisor.callbackActionList.forEachIndexed { index, _ ->
                if (index > 0) {
                    Assert.assertTrue(listenerSupervisor.callbackActionList[index - 1].toLong() < listenerSupervisor.callbackActionList[index].toLong())
                }
            }
            Assert.assertEquals(
                testImage.contentLength,
                listenerSupervisor.callbackActionList.last().toLong()
            )
        }
    }

    @Test
    fun testComponents() {
        val context = getTestContext()

        ImageRequest(context, MyImages.jpeg.uri)
            .let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
                Assert.assertNull(request.parameters?.get("TestRequestInterceptor"))
            }
        ImageRequest(context, MyImages.jpeg.uri) {
            components {
                addRequestInterceptor(TestRequestInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertEquals("true", request.parameters?.get("TestRequestInterceptor"))
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertFalse(transformedList?.contains("TestDecodeInterceptor") == true)
        }
        ImageRequest(context, MyImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            components {
                addDecodeInterceptor(TestDecodeInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertTrue(transformedList?.contains("TestDecodeInterceptor") == true)
        }

        ImageRequest(context, MyImages.jpeg.uri.replace("asset", "test")) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Error)
        }
        ImageRequest(context, MyImages.jpeg.uri.replace("asset", "test")) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addFetcher(TestAssetFetcherFactory())
            }
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }
        ImageRequest(context, MyImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addDecoder(TestErrorDecoder.Factory())
            }
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Error)
        }
    }

    @Test
    fun testTarget() {
        val context = getTestContext()
        val sketch = newSketch()

        TestTarget().let { testTarget ->
            Assert.assertNull(testTarget.startImage)
            Assert.assertNull(testTarget.successImage)
            Assert.assertNull(testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            ImageRequest(context, MyImages.jpeg.uri) {
                target(testTarget)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertNull(testTarget.startImage)
            Assert.assertNotNull(testTarget.successImage)
            Assert.assertNull(testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            ImageRequest(context, MyImages.jpeg.uri + ".fake") {
                placeholder(ColorDrawableEqualizer(Color.BLUE))
                error(android.R.drawable.btn_radio)
                target(testTarget)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertNotNull(testTarget.startImage)
            Assert.assertTrue(testTarget.startImage?.asOrThrow<AndroidDrawableImage>()?.drawable is ColorDrawable)
            Assert.assertNull(testTarget.successImage)
            Assert.assertNotNull(testTarget.errorImage)
            Assert.assertTrue(testTarget.errorImage?.asOrThrow<AndroidDrawableImage>()?.drawable is StateListDrawable)
        }

        TestTarget().let { testTarget ->
            var deferred: Deferred<ImageResult>? = null
            val listenerSupervisor = ListenerSupervisor {
                deferred?.cancel()
            }
            ImageRequest(context, MyImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                registerListener(listenerSupervisor)
                target(testTarget)
            }.let { request ->
                runBlocking {
                    deferred = async {
                        sketch.execute(request)
                    }
                    deferred?.join()
                }
            }
            Assert.assertNull(testTarget.startImage)
            Assert.assertNull(testTarget.successImage)
            Assert.assertNull(testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            var deferred: Deferred<ImageResult>? = null
            val listenerSupervisor = ListenerSupervisor {
                deferred?.cancel()
            }
            ImageRequest(context, MyImages.jpeg.uri + ".fake") {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                registerListener(listenerSupervisor)
                error(android.R.drawable.btn_radio)
                target(testTarget)
            }.let { request ->
                runBlocking {
                    deferred = async {
                        sketch.execute(request)
                    }
                    deferred?.join()
                }
            }
            Assert.assertNull(testTarget.startImage)
            Assert.assertNull(testTarget.successImage)
            Assert.assertNull(testTarget.errorImage)
        }
    }

    @Test
    fun testLifecycle() {
        val context = getTestContext()
        val sketch = newSketch()
        val lifecycleOwner = object : LifecycleOwner {
            override var lifecycle: Lifecycle = LifecycleRegistry(this)
        }
        val myLifecycle = lifecycleOwner.lifecycle as LifecycleRegistry
        runBlocking(Dispatchers.Main) {
            myLifecycle.currentState = CREATED
        }

        ImageRequest(context, MyImages.jpeg.uri).let { request ->
            Assert.assertEquals(
                DefaultLifecycleResolver(LifecycleResolver(GlobalTargetLifecycle)),
                request.lifecycleResolver
            )
            runBlocking {
                sketch.execute(request)
            }
        }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            lifecycle(myLifecycle)
        }.let { request ->
            Assert.assertEquals(LifecycleResolver(RealTargetLifecycle(myLifecycle)), request.lifecycleResolver)
            runBlocking {
                val deferred = async {
                    sketch.execute(request)
                }
                delay(2000)
                if (!deferred.isCompleted) {
                    withContext(Dispatchers.Main) {
                        myLifecycle.currentState = STARTED
                    }
                }
                delay(2000)
                deferred.await()
            }
        }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }
    }
}