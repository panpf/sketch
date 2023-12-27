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
import android.graphics.ColorSpace
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
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.core.test.newSketch
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.decode.internal.resultCacheDataKey
import com.github.panpf.sketch.request.DefaultLifecycleResolver
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.memoryCacheKey
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.request.execute
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.LoadListenerSupervisor
import com.github.panpf.sketch.test.utils.LoadProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.TestAssetFetcherFactory
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestErrorBitmapDecoder
import com.github.panpf.sketch.test.utils.TestErrorDrawableDecoder
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestLoadTarget
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.ratio
import com.github.panpf.sketch.test.utils.samplingByTarget
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.getCircleCropTransformed
import com.github.panpf.sketch.transform.getRotateTransformed
import com.github.panpf.sketch.transform.getRoundedCornersTransformed
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
class LoadRequestExecuteTest {

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
        LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        // NETWORK
        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(NETWORK)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        // LOCAL
        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        runBlocking {
            sketch.execute(LoadRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
            })
        }
        sketch.memoryCache.clear()
        Assert.assertTrue(sketch.downloadCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(LOCAL)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
        }

        sketch.downloadCache.clear()
        sketch.memoryCache.clear()
        LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(LOCAL)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Error>()!!.apply {
            Assert.assertTrue(throwable is DepthException)
        }

        // MEMORY
        sketch.memoryCache.clear()
        runBlocking {
            sketch.execute(LoadRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
            })
        }
        LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
        }

        sketch.memoryCache.clear()
        LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Error>().apply {
            Assert.assertNull(this)
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
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(imageUri))
        LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }
    }

    @Test
    fun testBitmapConfig() {
        val context = getTestContext()
        val sketch = newSketch()

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ARGB_8888)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            @Suppress("DEPRECATION")
            bitmapConfig(ARGB_4444)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                if (VERSION.SDK_INT > VERSION_CODES.M) {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, bitmap.config)
                }
            }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ALPHA_8)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(RGB_565)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(RGB_565, bitmap.config)
            }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LoadRequest(context, AssetImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(RGBA_F16)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<LoadResult.Success>()!!
                .apply {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LoadRequest(context, AssetImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(HARDWARE)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<LoadResult.Success>()!!
                .apply {
                    Assert.assertEquals(HARDWARE, bitmap.config)
                }
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(RGB_565, bitmap.config)
            }
        LoadRequest(context, AssetImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, bitmap.config)
                }
            }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                } else {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                }
            }
        LoadRequest(context, AssetImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
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

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.SRGB).name,
                    bitmap.colorSpace!!.name
                )
            }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.get(ColorSpace.Named.ADOBE_RGB))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.ADOBE_RGB).name,
                    bitmap.colorSpace!!.name
                )
            }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.get(ColorSpace.Named.DISPLAY_P3))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
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

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(true)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(false)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is LoadResult.Success)
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
        LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }
            .let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(samplingByTarget(imageSize, displaySize), bitmap.size)
                Assert.assertEquals(imageInfo.size.ratio, bitmap.size.ratio)
            }

        // size: small, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val smallSize1 = Size(600, 500)
        LoadRequest(context, imageUri) {
            resizeSize(smallSize1)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 484), bitmap.size)
                Assert.assertEquals(imageInfo.size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(smallSize1)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(322, 268), bitmap.size)
                Assert.assertEquals(smallSize1.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(smallSize1)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(smallSize1, bitmap.size)
            }

        val smallSize2 = Size(500, 600)
        LoadRequest(context, imageUri) {
            resizeSize(smallSize2)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 484), bitmap.size)
                Assert.assertEquals(imageInfo.size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(smallSize2)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(322, 387), bitmap.size)
                Assert.assertEquals(smallSize2.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(smallSize2)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(smallSize2, bitmap.size)
            }

        // size: same, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val sameSize = Size(imageSize.width, imageSize.height)
        LoadRequest(context, imageUri) {
            resizeSize(sameSize)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(sameSize, bitmap.size)
            }
        LoadRequest(context, imageUri) {
            resizeSize(sameSize)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(sameSize, bitmap.size)
            }
        LoadRequest(context, imageUri) {
            resizeSize(sameSize)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(sameSize, bitmap.size)
            }

        // size: big, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val bigSize1 = Size(2500, 2100)
        LoadRequest(context, imageUri) {
            resizeSize(bigSize1)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(imageSize, bitmap.size)
                Assert.assertEquals(imageInfo.size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize1)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(1291, 1084), bitmap.size)
                Assert.assertEquals(bigSize1.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize1)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize1, bitmap.size)
            }

        val bigSize2 = Size(2100, 2500)
        LoadRequest(context, imageUri) {
            resizeSize(bigSize2)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(imageSize, bitmap.size)
                Assert.assertEquals(imageInfo.size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize2)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(1291, 1537), bitmap.size)
                Assert.assertEquals(bigSize2.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize2)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize2, bitmap.size)
            }

        val bigSize3 = Size(800, 2500)
        LoadRequest(context, imageUri) {
            resizeSize(bigSize3)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(646, 968), bitmap.size)
                Assert.assertEquals(imageInfo.size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize3)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(620, 1936), bitmap.size)
                Assert.assertEquals(bigSize3.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize3)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize3, bitmap.size)
            }

        val bigSize4 = Size(2500, 800)
        LoadRequest(context, imageUri) {
            resizeSize(bigSize4)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(646, 968), bitmap.size)
                Assert.assertEquals(imageInfo.size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize4)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(1291, 413), bitmap.size)
                Assert.assertEquals(bigSize4.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(bigSize4)
            resizePrecision(EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize4, bitmap.size)
            }

        /* scale */
        val size = Size(600, 500)
        var sarStartCropBitmap: Bitmap?
        var sarCenterCropBitmap: Bitmap?
        var sarEndCropBitmap: Bitmap?
        var sarFillCropBitmap: Bitmap?
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                sarStartCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(322, 268), bitmap.size)
                Assert.assertEquals(size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                sarCenterCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(322, 268), bitmap.size)
                Assert.assertEquals(size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                sarEndCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(322, 268), bitmap.size)
                Assert.assertEquals(size.ratio, bitmap.size.ratio)
            }
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                sarFillCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(
                    if (VERSION.SDK_INT >= 24)
                        Size(323, 269) else Size(322, 268),
                    bitmap.size
                )
                Assert.assertEquals(size.ratio, bitmap.size.ratio)
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
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                exactlyStartCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, bitmap.size)
            }
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                exactlyCenterCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, bitmap.size)
            }
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                exactlyEndCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, bitmap.size)
            }
        LoadRequest(context, imageUri) {
            resizeSize(size)
            resizePrecision(EXACTLY)
            resizeScale(FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!.apply {
                exactlyFillCropBitmap = bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, bitmap.size)
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
        val request = LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }

        request.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertTrue(transformedList?.all {
                    it.startsWith("ResizeTransformed") || it.startsWith("InSampledTransformed")
                } != false)
            }

        request.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertNotEquals(listOf(0, 0, 0, 0), bitmap.corners())
                Assert.assertNull(transformedList?.getRoundedCornersTransformed())
            }
        request.newLoadRequest {
            addTransformations(RoundedCornersTransformation(30f))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(listOf(0, 0, 0, 0), bitmap.corners())
                Assert.assertNotNull(transformedList?.getRoundedCornersTransformed())
            }

        request.newLoadRequest {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(Size(323, 484), bitmap.size)
                Assert.assertNull(transformedList?.getRotateTransformed())
            }
        request.newLoadRequest {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
            addTransformations(RotateTransformation(90))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(Size(484, 323), bitmap.size)
                Assert.assertNotNull(transformedList?.getRotateTransformed())
            }

        request.newLoadRequest {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(Size(323, 484), bitmap.size)
                Assert.assertNotEquals(listOf(0, 0, 0, 0), bitmap.corners())
                Assert.assertNull(transformedList?.getCircleCropTransformed())
            }
        request.newLoadRequest {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
            addTransformations(CircleCropTransformation())
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
            .apply {
                Assert.assertEquals(Size(323, 323), bitmap.size)
                Assert.assertEquals(listOf(0, 0, 0, 0), bitmap.corners())
                Assert.assertNotNull(transformedList?.getCircleCropTransformed())
            }
    }

    @Test
    fun testDisallowReuseBitmap() {
        val context = getTestContext()
        val sketch = newSketch()
        val bitmapPool = sketch.bitmapPool
        val imageUri = AssetImages.jpeg.uri
        val request = LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }

        bitmapPool.put(Bitmap.createBitmap(323, 484, ARGB_8888))
        Assert.assertNotNull(bitmapPool.get(323, 484, ARGB_8888))
        Assert.assertNull(bitmapPool.get(323, 484, ARGB_8888))

        bitmapPool.put(Bitmap.createBitmap(323, 484, ARGB_8888))
        request.newLoadRequest {
            disallowReuseBitmap(true)
        }.let { runBlocking { sketch.execute(it) } }
        Assert.assertNotNull(bitmapPool.get(323, 484, ARGB_8888))
        Assert.assertNull(bitmapPool.get(323, 484, ARGB_8888))

        bitmapPool.put(Bitmap.createBitmap(323, 484, ARGB_8888))
        request.newLoadRequest {
            disallowReuseBitmap(false)
        }.let { runBlocking { sketch.execute(it) } }
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            Assert.assertNull(bitmapPool.get(323, 484, ARGB_8888))
        } else {
            Assert.assertNotNull(bitmapPool.get(323, 484, ARGB_8888))
        }

        bitmapPool.put(Bitmap.createBitmap(323, 484, ARGB_8888))
        request.newLoadRequest {
            disallowReuseBitmap(null)
        }.let { runBlocking { sketch.execute(it) } }
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            Assert.assertNull(bitmapPool.get(323, 484, ARGB_8888))
        } else {
            Assert.assertNotNull(bitmapPool.get(323, 484, ARGB_8888))
        }
    }

    @Test
    fun testIgnoreExifOrientation() {
        val context = getTestContext()
        val sketch = newSketch()
        ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.clockHor.fileName
        ).files().forEach {
            Assert.assertNotEquals(ExifInterface.ORIENTATION_UNDEFINED, it.exifOrientation)

            LoadRequest(context, it.file.path)
                .let { runBlocking { sketch.execute(it) } }
                .asOrNull<LoadResult.Success>()!!
                .apply {
                    Assert.assertEquals(it.exifOrientation, imageInfo.exifOrientation)
                    Assert.assertEquals(Size(1500, 750), imageInfo.size)
                }

            LoadRequest(context, it.file.path) {
                ignoreExifOrientation(true)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<LoadResult.Success>()!!
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

        LoadRequest(context, AssetImages.jpeg.uri)
            .let { runBlocking { sketch.execute(it) } }
            .asOrNull<LoadResult.Success>()!!
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
        val request = LoadRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resizeSize(500, 500)
        }
        val resultCacheDataKey = request.toRequestContext().resultCacheDataKey

        /* ENABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newLoadRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testMemoryCachePolicy() {
        val context = getTestContext()
        val sketch = newSketch()
        val memoryCache = sketch.memoryCache
        val imageUri = AssetImages.jpeg.uri
        val request = LoadRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            resizeSize(500, 500)
        }
        val memoryCacheKey = request.toRequestContext().memoryCacheKey

        /* ENABLED */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* DISABLED */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* WRITE_ONLY */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))
        request.newLoadRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<LoadResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testListener() {
        val context = getTestContext()
        val sketch = newSketch()
        val imageUri = AssetImages.jpeg.uri
        val errorImageUri = AssetImages.jpeg.uri + ".fake"

        LoadListenerSupervisor().let { listenerSupervisor ->
            Assert.assertEquals(listOf<String>(), listenerSupervisor.callbackActionList)

            LoadRequest(context, imageUri) {
                listener(listenerSupervisor)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertEquals(
                listOf("onStart", "onSuccess"),
                listenerSupervisor.callbackActionList
            )
        }

        LoadListenerSupervisor().let { listenerSupervisor ->
            Assert.assertEquals(listOf<String>(), listenerSupervisor.callbackActionList)

            LoadRequest(context, errorImageUri) {
                listener(listenerSupervisor)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor.callbackActionList)
        }

        var deferred: Deferred<LoadResult>? = null
        val listenerSupervisor = LoadListenerSupervisor {
            deferred?.cancel()
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
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

        LoadProgressListenerSupervisor().let { listenerSupervisor ->
            Assert.assertEquals(listOf<String>(), listenerSupervisor.callbackActionList)

            LoadRequest(context, testImage.uriString) {
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

        LoadRequest(context, AssetImages.jpeg.uri)
            .let { runBlocking { it.execute() } }.asOrThrow<LoadResult.Success>().apply {
                Assert.assertNull(request.parameters?.get("TestRequestInterceptor"))
            }
        LoadRequest(context, AssetImages.jpeg.uri) {
            components {
                addRequestInterceptor(TestRequestInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<LoadResult.Success>().apply {
            Assert.assertEquals("true", request.parameters?.get("TestRequestInterceptor"))
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.asOrThrow<LoadResult.Success>().apply {
            Assert.assertFalse(transformedList?.contains("TestBitmapDecodeInterceptor") == true)
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            components {
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<LoadResult.Success>().apply {
            Assert.assertTrue(transformedList?.contains("TestBitmapDecodeInterceptor") == true)
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.asOrThrow<LoadResult.Success>().apply {
            Assert.assertFalse(transformedList?.contains("TestDrawableDecodeInterceptor") == true)
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            components {
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
            }
        }.let { runBlocking { it.execute() } }.asOrThrow<LoadResult.Success>().apply {
            Assert.assertFalse(transformedList?.contains("TestDrawableDecodeInterceptor") == true)
        }

        LoadRequest(context, AssetImages.jpeg.uri.replace("asset", "test")) {
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is LoadResult.Error)
        }
        LoadRequest(context, AssetImages.jpeg.uri.replace("asset", "test")) {
            resultCachePolicy(DISABLED)
            components {
                addFetcher(TestAssetFetcherFactory())
            }
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            components {
                addBitmapDecoder(TestErrorBitmapDecoder.Factory())
            }
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is LoadResult.Error)
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            components {
                addDrawableDecoder(TestErrorDrawableDecoder.Factory())
            }
        }.let { runBlocking { it.execute() } }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }
    }

    @Test
    fun testTarget() {
        val context = getTestContext()
        val sketch = newSketch()

        TestLoadTarget().let { testTarget ->
            Assert.assertNull(testTarget.start)
            Assert.assertNull(testTarget.successBitmap)
            Assert.assertNull(testTarget.throwable)
        }

        TestLoadTarget().let { testTarget ->
            LoadRequest(context, AssetImages.jpeg.uri) {
                target(testTarget)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertNotNull(testTarget.start)
            Assert.assertNotNull(testTarget.successBitmap)
            Assert.assertNull(testTarget.throwable)
        }

        TestLoadTarget().let { testTarget ->
            LoadRequest(context, AssetImages.jpeg.uri + ".fake") {
                target(testTarget)
            }.let { request ->
                runBlocking { sketch.execute(request) }
            }
            Assert.assertNotNull(testTarget.start)
            Assert.assertNull(testTarget.successBitmap)
            Assert.assertNotNull(testTarget.throwable)
        }

        TestLoadTarget().let { testTarget ->
            var deferred: Deferred<LoadResult>? = null
            val listenerSupervisor = LoadListenerSupervisor {
                deferred?.cancel()
            }
            LoadRequest(context, AssetImages.jpeg.uri) {
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
            Assert.assertNotNull(testTarget.start)
            Assert.assertNull(testTarget.successBitmap)
            Assert.assertNull(testTarget.throwable)
        }

        TestLoadTarget().let { testTarget ->
            var deferred: Deferred<LoadResult>? = null
            val listenerSupervisor = LoadListenerSupervisor {
                deferred?.cancel()
            }
            LoadRequest(context, AssetImages.jpeg.uri + ".fake") {
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
            Assert.assertNotNull(testTarget.start)
            Assert.assertNull(testTarget.successBitmap)
            Assert.assertNull(testTarget.throwable)
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

        LoadRequest(context, AssetImages.jpeg.uri).let { request ->
            Assert.assertEquals(
                DefaultLifecycleResolver(LifecycleResolver(GlobalLifecycle)),
                request.lifecycleResolver
            )
            runBlocking {
                sketch.execute(request)
            }
        }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
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
            Assert.assertTrue(this is LoadResult.Success)
        }
    }
}