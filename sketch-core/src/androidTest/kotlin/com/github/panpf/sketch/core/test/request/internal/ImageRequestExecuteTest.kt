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
package com.github.panpf.sketch.core.test.request.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.decode.internal.resultCacheDataKey
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.drawable.internal.ResizeDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DefaultLifecycleResolver
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DrawableImage
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.getBitmapOrThrow
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.internal.memoryCacheKey
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.test.singleton.request.execute
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.ProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.TestAssetFetcherFactory
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDisplayCountDisplayTarget
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestErrorBitmapDecoder
import com.github.panpf.sketch.test.utils.TestErrorDrawableDecoder
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.TestTransitionViewTarget
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.test.utils.ratio
import com.github.panpf.sketch.test.utils.samplingByTarget
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.target
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.getCircleCropTransformed
import com.github.panpf.sketch.transform.getRotateTransformed
import com.github.panpf.sketch.transform.getRoundedCornersTransformed
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
        val imageUri = TestHttpStack.testImages.first().uriString

        // default
        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            target(TestDisplayCountDisplayTarget())
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
            target(TestDisplayCountDisplayTarget())
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
                target(TestDisplayCountDisplayTarget())
            })
        }
        sketch.memoryCache.clear()
        Assert.assertTrue(sketch.downloadCache.exist(imageUri))
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(LOCAL)
            target(TestDisplayCountDisplayTarget())
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
            target(TestDisplayCountDisplayTarget())
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
                target(TestDisplayCountDisplayTarget())
            })
        }
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
            target(TestDisplayCountDisplayTarget())
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        sketch.memoryCache.clear()
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
            target(TestDisplayCountDisplayTarget())
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
        val imageUri = TestHttpStack.testImages.first().uriString

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

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ARGB_8888)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            @Suppress("DEPRECATION")
            bitmapConfig(ARGB_4444)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT > VERSION_CODES.M) {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, bitmap.config)
                }
            }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ALPHA_8)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(RGB_565)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(RGB_565, bitmap.config)
            }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageRequest(context, AssetImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(RGBA_F16)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<ImageResult.Success>()!!
                .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
                .apply {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageRequest(context, AssetImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(HARDWARE)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<ImageResult.Success>()!!
                .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
                .apply {
                    Assert.assertEquals(HARDWARE, bitmap.config)
                }
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(RGB_565, bitmap.config)
            }
        ImageRequest(context, AssetImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, bitmap.config)
                }
            }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                } else {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                }
            }
        ImageRequest(context, AssetImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                } else {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                }
            }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context = getTestContext()
        val sketch = newSketch()

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.SRGB).name,
                    bitmap.colorSpace!!.name
                )
            }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.get(ColorSpace.Named.ADOBE_RGB))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.ADOBE_RGB).name,
                    bitmap.colorSpace!!.name
                )
            }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.get(ColorSpace.Named.DISPLAY_P3))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.DISPLAY_P3).name,
                    bitmap.colorSpace!!.name
                )
            }
    }

    @Test
    fun testPreferQualityOverSpeed() {
        val context = getTestContext()
        val sketch = newSketch()

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(true)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(false)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }
    }

    @Test
    fun testResize() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageUri = AssetImages.jpeg.uri
        val imageSize = Size(1291, 1936)
        val displaySize = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }

        // default
        ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }
            .let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    samplingByTarget(imageSize, displaySize),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }

        // size: small, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val smallSize1 = Size(600, 500)
        ImageRequest(context, imageUri) {
            resizeSize(smallSize1)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(323, 484),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(smallSize1)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    smallSize1.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(smallSize1)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    smallSize1,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }

        val smallSize2 = Size(500, 600)
        ImageRequest(context, imageUri) {
            resizeSize(smallSize2)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(323, 484),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(smallSize2)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 387),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    smallSize2.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(smallSize2)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    smallSize2,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }

        // size: same, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val sameSize = Size(imageSize.width, imageSize.height)
        ImageRequest(context, imageUri) {
            resizeSize(sameSize)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    sameSize,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(sameSize)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    sameSize,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(sameSize)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    sameSize,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }

        // size: big, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val bigSize1 = Size(2500, 2100)
        ImageRequest(context, imageUri) {
            resizeSize(bigSize1)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    imageSize,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize1)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(1291, 1084),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    bigSize1.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize1)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize1,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }

        val bigSize2 = Size(2100, 2500)
        ImageRequest(context, imageUri) {
            resizeSize(bigSize2)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    imageSize,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize2)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(1291, 1537),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    bigSize2.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize2)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize2,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }

        val bigSize3 = Size(800, 2500)
        ImageRequest(context, imageUri) {
            resizeSize(bigSize3)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(646, 968),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize3)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(620, 1936),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    bigSize3.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize3)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize3,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }

        val bigSize4 = Size(2500, 800)
        ImageRequest(context, imageUri) {
            resizeSize(bigSize4)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(646, 968),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    imageInfo.size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize4)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(1291, 413),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    bigSize4.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(bigSize4)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    bigSize4,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
            }

        /* scale */
        val size = Size(600, 500)
        var sarStartCropBitmap: Bitmap?
        var sarCenterCropBitmap: Bitmap?
        var sarEndCropBitmap: Bitmap?
        var sarFillCropBitmap: Bitmap?
        ImageRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarStartCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarCenterCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarEndCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    Size(322, 268),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
                )
            }
        ImageRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarFillCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    if (VERSION.SDK_INT >= 24)
                        Size(323, 269) else Size(322, 268),
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize
                )
                Assert.assertEquals(
                    size.ratio,
                    image.asOrThrow<DrawableImage>().drawable.intrinsicSize.ratio
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
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyStartCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.asOrThrow<DrawableImage>().drawable.intrinsicSize)
            }
        ImageRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyCenterCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.asOrThrow<DrawableImage>().drawable.intrinsicSize)
            }
        ImageRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyEndCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.asOrThrow<DrawableImage>().drawable.intrinsicSize)
            }
        ImageRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyFillCropBitmap =
                    image.asOrThrow<DrawableImage>().drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, image.asOrThrow<DrawableImage>().drawable.intrinsicSize)
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
        val imageUri = AssetImages.jpeg.uri
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
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(Size(323, 484), Size(image.width, image.height))
                Assert.assertNull(
                    transformedList?.getRotateTransformed()
                )
            }
        request.newRequest {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
            addTransformations(RotateTransformation(90))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertEquals(Size(484, 323), Size(image.width, image.height))
                Assert.assertNotNull(
                    transformedList?.getRotateTransformed()
                )
            }

        request.newRequest {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
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
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
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
    fun testDisallowReuseBitmap() {
        val context = getTestContext()
        val sketch = newSketch()
        val bitmapPool = sketch.bitmapPool
        val imageUri = AssetImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }

        bitmapPool.put(Bitmap.createBitmap(323, 484, ARGB_8888))
        Assert.assertTrue(bitmapPool.exist(323, 484, ARGB_8888))

        request.newRequest {
            disallowReuseBitmap(true)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertTrue(bitmapPool.exist(323, 484, ARGB_8888))

        request.newRequest {
            disallowReuseBitmap(false)
        }.let { runBlocking { sketch.execute(it) } }
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            Assert.assertFalse(bitmapPool.exist(323, 484, ARGB_8888))
        } else {
            Assert.assertTrue(bitmapPool.exist(323, 484, ARGB_8888))
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(323, 484, ARGB_8888))
        request.newRequest {
            disallowReuseBitmap(null)
        }.let { runBlocking { sketch.execute(it) } }
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            Assert.assertFalse(bitmapPool.exist(323, 484, ARGB_8888))
        } else {
            Assert.assertTrue(bitmapPool.exist(323, 484, ARGB_8888))
        }

        bitmapPool.clear()
        Assert.assertTrue(bitmapPool.size == 0L)
        request.newRequest {
            disallowReuseBitmap(false)
            transformations(
                RoundedCornersTransformation(30f),
                CircleCropTransformation(),
                RotateTransformation(90),
            )
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertTrue(bitmapPool.size > 0L)

        bitmapPool.clear()
        Assert.assertTrue(bitmapPool.size == 0L)
        request.newRequest {
            disallowReuseBitmap(true)
            transformations(
                RoundedCornersTransformation(30f),
                CircleCropTransformation(),
                RotateTransformation(90),
            )
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertTrue(bitmapPool.size == 0L)
    }

    @Test
    fun testIgnoreExifOrientation() {
        val context = getTestContext()
        val sketch = newSketch()
        ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.clockHor.fileName
        ).files()
            .forEach {
                Assert.assertNotEquals(ExifInterface.ORIENTATION_UNDEFINED, it.exifOrientation)

                ImageRequest(context, it.file.path)
                    .let { runBlocking { sketch.execute(it) } }
                    .asOrNull<ImageResult.Success>()!!
                    .apply {
                        Assert.assertEquals(it.exifOrientation, imageInfo.exifOrientation)
                        Assert.assertEquals(Size(1500, 750), imageInfo.size)
                    }

                ImageRequest(context, it.file.path) {
                    ignoreExifOrientation(true)
                }.let { runBlocking { sketch.execute(it) } }
                    .asOrNull<ImageResult.Success>()!!
                    .apply {
                        Assert.assertEquals(
                            ExifInterface.ORIENTATION_UNDEFINED,
                            imageInfo.exifOrientation
                        )
                        if (it.exifOrientation == ExifInterface.ORIENTATION_ROTATE_90
                            || it.exifOrientation == ExifInterface.ORIENTATION_ROTATE_270
                            || it.exifOrientation == ExifInterface.ORIENTATION_TRANSVERSE
                            || it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE
                        ) {
                            Assert.assertEquals(
                                exifOrientationName(it.exifOrientation),
                                Size(750, 1500),
                                imageInfo.size
                            )
                        } else {
                            Assert.assertEquals(
                                exifOrientationName(it.exifOrientation),
                                Size(1500, 750),
                                imageInfo.size
                            )
                        }
                    }
            }

        ImageRequest(context, AssetImages.jpeg.uri)
            .let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!
            .apply {
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, imageInfo.exifOrientation)
                Assert.assertEquals(Size(1291, 1936), imageInfo.size)
            }
    }

    @Test
    fun testResultCachePolicy() {
        val context = getTestContext()
        val sketch = newSketch()
        val diskCache = sketch.resultCache
        val imageUri = AssetImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resizeSize(500, 500)
        }
        val resultCacheDataKey = request.toRequestContext(sketch).resultCacheDataKey

        /* ENABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<ImageResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testPlaceholder() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = AssetImages.jpeg.uri
        var onStartDrawable: Image?
        val request = ImageRequest(context, imageUri) {
            resizeSize(500, 500)
            target(object : Target {
                override val supportDisplayCount: Boolean = true
                override fun onStart(requestContext: RequestContext, placeholder: Image?) {
                    super.onStart(requestContext, placeholder)
                    onStartDrawable = placeholder
                }
            })
        }
        val memoryCacheKey = request.toRequestContext(sketch).memoryCacheKey
        val memoryCache = sketch.memoryCache
        val colorDrawable = ColorDrawable(Color.BLUE)

        memoryCache.clear()
        onStartDrawable = null
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest()
            .let { runBlocking { sketch.execute(it) } }
        Assert.assertNull(onStartDrawable)

        onStartDrawable = null
        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            placeholder(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNull(onStartDrawable)
        Assert.assertFalse(onStartDrawable?.asOrThrow<DrawableImage>()?.drawable === colorDrawable)

        onStartDrawable = null
        Assert.assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(DISABLED)
            placeholder(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNotNull(onStartDrawable)
        Assert.assertTrue(onStartDrawable?.asOrThrow<DrawableImage>()?.drawable === colorDrawable)
    }

    @Test
    fun testError() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = AssetImages.jpeg.uri
        var onErrorImage: Image?
        val request = ImageRequest(context, imageUri) {
            resizeSize(500, 500)
            target(
                onError = { _, image ->
                    onErrorImage = image
                }
            )
        }
        val errorRequest = ImageRequest(context, newAssetUri("error.jpeg")) {
            resizeSize(500, 500)
            target(
                onError = { _, image ->
                    onErrorImage = image
                }
            )
        }
        val colorDrawable = ColorDrawable(Color.BLUE)

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
        Assert.assertTrue(onErrorImage?.asOrThrow<DrawableImage>()?.drawable === colorDrawable)

        onErrorImage = null
        errorRequest.newRequest {
            placeholder(colorDrawable)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNotNull(onErrorImage)
        Assert.assertTrue(onErrorImage?.asOrThrow<DrawableImage>()?.drawable === colorDrawable)
    }

    @Test
    fun testTransition() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = AssetImages.jpeg.uri
        val testTarget = TestTransitionViewTarget()
        val request = ImageRequest(context, imageUri) {
            resizeSize(500, 500)
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
            transitionFactory(CrossfadeTransition.Factory())
        }.let { runBlocking { sketch.enqueue(it).job.join() } }
        Assert.assertFalse(testTarget.drawable!! is CrossfadeDrawable)

        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }.let { runBlocking { sketch.enqueue(it).job.join() } }
        Assert.assertTrue(testTarget.drawable!! is CrossfadeDrawable)
    }

    @Test
    fun testResizeApplyToDrawable() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = AssetImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            resizeSize(500, 500)
        }

        request.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertFalse(image.asOrThrow<DrawableImage>().drawable is ResizeDrawable)
            }

        request.newRequest {
            resizeApplyToDrawable(false)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertFalse(image.asOrThrow<DrawableImage>().drawable is ResizeDrawable)
            }

        request.newRequest {
            resizeApplyToDrawable(null)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertFalse(image.asOrThrow<DrawableImage>().drawable is ResizeDrawable)
            }

        request.newRequest {
            resizeApplyToDrawable(true)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<ImageResult.Success>()!!.apply {
                Assert.assertTrue(image.asOrThrow<DrawableImage>().drawable is ResizeDrawable)
            }
    }

    @Test
    fun testMemoryCachePolicy() {
        val context = getTestContext()
        val sketch = newSketch()
        val memoryCache = sketch.memoryCache
        val imageUri = AssetImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            resizeSize(500, 500)
            target(TestDisplayCountDisplayTarget())
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
        val imageUri = AssetImages.jpeg.uri
        val errorImageUri = AssetImages.jpeg.uri + ".fake"

        ListenerSupervisor().let { listenerSupervisor ->
            Assert.assertEquals(listOf<String>(), listenerSupervisor.callbackActionList)

            ImageRequest(context, imageUri) {
                listener(listenerSupervisor)
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
                listener(listenerSupervisor)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor.callbackActionList)
        }

        var deferred: Deferred<ImageResult>? = null
        val listenerSupervisor = ListenerSupervisor {
            deferred?.cancel()
        }
        ImageRequest(context, AssetImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            listener(listenerSupervisor)
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

            ImageRequest(context, testImage.uriString) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
                progressListener(listenerSupervisor)
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

        ImageRequest(context, AssetImages.jpeg.uri)
            .let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
                Assert.assertNull(request.parameters?.get("TestRequestInterceptor"))
            }
        ImageRequest(context, AssetImages.jpeg.uri) {
            components {
                addRequestInterceptor(TestRequestInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertEquals("true", request.parameters?.get("TestRequestInterceptor"))
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertFalse(transformedList?.contains("TestBitmapDecodeInterceptor") == true)
        }
        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            components {
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertTrue(transformedList?.contains("TestBitmapDecodeInterceptor") == true)
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertFalse(transformedList?.contains("TestDrawableDecodeInterceptor") == true)
        }
        ImageRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            components {
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<ImageResult.Success>().apply {
            Assert.assertTrue(transformedList?.contains("TestDrawableDecodeInterceptor") == true)
        }

        ImageRequest(context, AssetImages.jpeg.uri.replace("asset", "test")) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Error)
        }
        ImageRequest(context, AssetImages.jpeg.uri.replace("asset", "test")) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addFetcher(TestAssetFetcherFactory())
            }
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }
        ImageRequest(context, AssetImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addBitmapDecoder(TestErrorBitmapDecoder.Factory())
            }
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Error)
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }
        ImageRequest(context, AssetImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addDrawableDecoder(TestErrorDrawableDecoder.Factory())
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
            ImageRequest(context, AssetImages.jpeg.uri) {
                target(testTarget)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertNull(testTarget.startImage)
            Assert.assertNotNull(testTarget.successImage)
            Assert.assertNull(testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            ImageRequest(context, AssetImages.jpeg.uri + ".fake") {
                placeholder(ColorDrawable(Color.BLUE))
                error(android.R.drawable.btn_radio)
                target(testTarget)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertNotNull(testTarget.startImage)
            Assert.assertTrue(testTarget.startImage?.asOrThrow<DrawableImage>()?.drawable is ColorDrawable)
            Assert.assertNull(testTarget.successImage)
            Assert.assertNotNull(testTarget.errorImage)
            Assert.assertTrue(testTarget.errorImage?.asOrThrow<DrawableImage>()?.drawable is StateListDrawable)
        }

        TestTarget().let { testTarget ->
            var deferred: Deferred<ImageResult>? = null
            val listenerSupervisor = ListenerSupervisor {
                deferred?.cancel()
            }
            ImageRequest(context, AssetImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                listener(listenerSupervisor)
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
            ImageRequest(context, AssetImages.jpeg.uri + ".fake") {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                listener(listenerSupervisor)
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

        ImageRequest(context, AssetImages.jpeg.uri).let { request ->
            Assert.assertEquals(
                DefaultLifecycleResolver(LifecycleResolver(GlobalLifecycle)),
                request.lifecycleResolver
            )
            runBlocking {
                sketch.execute(request)
            }
        }.apply {
            Assert.assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, AssetImages.jpeg.uri) {
            lifecycle(myLifecycle)
        }.let { request ->
            Assert.assertEquals(LifecycleResolver(myLifecycle), request.lifecycleResolver)
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