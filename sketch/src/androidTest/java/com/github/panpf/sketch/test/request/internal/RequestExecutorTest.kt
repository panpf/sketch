@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.test.request.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.internal.newMemoryCacheKey
import com.github.panpf.sketch.decode.internal.newResultCacheDataKey
import com.github.panpf.sketch.decode.internal.samplingByTarget
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.RequestDepth.LOCAL
import com.github.panpf.sketch.request.RequestDepth.MEMORY
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getContext
import com.github.panpf.sketch.test.utils.getContextAndSketch
import com.github.panpf.sketch.test.utils.getSketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.ratio
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestExecutorTest {

    @Test
    fun testDepth() {
        val context = getContext()
        val sketch = getSketch {
            httpStack(TestHttpStack(context))
        }
        val imageUri = TestHttpStack.testUris.first().uriString

        // default
        sketch.diskCache.clear()
        sketch.memoryCache.clear()
        DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        // NETWORK
        sketch.diskCache.clear()
        sketch.memoryCache.clear()
        DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(NETWORK)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        // LOCAL
        sketch.diskCache.clear()
        sketch.memoryCache.clear()
        runBlocking {
            sketch.execute(DisplayRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
            })
        }
        sketch.memoryCache.clear()
        Assert.assertTrue(sketch.diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(LOCAL)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DISK_CACHE, dataFrom)
        }

        sketch.diskCache.clear()
        sketch.memoryCache.clear()
        DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(LOCAL)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Error>()!!.apply {
            Assert.assertTrue(exception is RequestDepthException)
        }

        // MEMORY
        sketch.memoryCache.clear()
        runBlocking {
            sketch.execute(DisplayRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
            })
        }
        DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        sketch.memoryCache.clear()
        DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            depth(MEMORY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Error>()!!.apply {
            Assert.assertTrue(exception is RequestDepthException)
        }
    }

    @Test
    fun testDownloadCachePolicy() {
        val context = getContext()
        val sketch = getSketch {
            httpStack(TestHttpStack(context))
        }
        val diskCache = sketch.diskCache
        val imageUri = TestHttpStack.testUris.first().uriString

        /* ENABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DISK_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.DISK_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(imageUri))
        DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            downloadCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }
    }

    @Test
    fun testBitmapConfig() {
        val context = getContext()
        val sketch = getSketch()

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ARGB_8888)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            @Suppress("DEPRECATION")
            bitmapConfig(ARGB_4444)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, bitmap.config)
                }
            }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ALPHA_8)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(ARGB_8888, bitmap.config)
            }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(RGB_565)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(RGB_565, bitmap.config)
            }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(RGBA_F16)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<DisplayResult.Success>()!!
                .drawable.asOrNull<BitmapDrawable>()!!
                .apply {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                bitmapConfig(HARDWARE)
            }.let { runBlocking { sketch.execute(it) } }
                .asOrNull<DisplayResult.Success>()!!
                .drawable.asOrNull<BitmapDrawable>()!!
                .apply {
                    Assert.assertEquals(HARDWARE, bitmap.config)
                }
        }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(RGB_565, bitmap.config)
            }
        DisplayRequest(context, TestAssets.SAMPLE_PNG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_4444, bitmap.config)
                }
            }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                }
            }
        DisplayRequest(context, TestAssets.SAMPLE_PNG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(RGBA_F16, bitmap.config)
                } else {
                    @Suppress("DEPRECATION")
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                }
            }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context = getContext()
        val sketch = getSketch()

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.SRGB).name,
                    bitmap.colorSpace!!.name
                )
            }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.get(ColorSpace.Named.ADOBE_RGB))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.ADOBE_RGB).name,
                    bitmap.colorSpace!!.name
                )
            }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.get(ColorSpace.Named.DISPLAY_P3))
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!
            .drawable.asOrNull<BitmapDrawable>()!!
            .apply {
                Assert.assertEquals(
                    ColorSpace.get(ColorSpace.Named.DISPLAY_P3).name,
                    bitmap.colorSpace!!.name
                )
            }
    }

    @Test
    fun testPreferQualityOverSpeed() {
        val context = getContext()
        val sketch = getSketch()

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is DisplayResult.Success)
        }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(true)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is DisplayResult.Success)
        }

        DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(false)
        }.let { runBlocking { sketch.execute(it) } }.apply {
            Assert.assertTrue(this is DisplayResult.Success)
        }
    }

    @Test
    fun testResize() {
        val (context, sketch) = getContextAndSketch()
        val imageUri = TestAssets.SAMPLE_JPEG_URI
        val imageSize = Size(1291, 1936)
        val displaySize = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }

        // default
        DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }
            .let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(imageSize.samplingByTarget(displaySize), drawable.intrinsicSize)
                Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            }

        // size: small, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val smallSize1 = Size(600, 500)
        DisplayRequest(context, imageUri) {
            resize(smallSize1, LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 484), drawable.intrinsicSize)
                Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(smallSize1, SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 269), drawable.intrinsicSize)
                Assert.assertEquals(smallSize1.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(smallSize1, EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(smallSize1, drawable.intrinsicSize)
            }

        val smallSize2 = Size(500, 600)
        DisplayRequest(context, imageUri) {
            resize(smallSize2, LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 484), drawable.intrinsicSize)
                Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(smallSize2, SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 388), drawable.intrinsicSize)
                Assert.assertEquals(smallSize2.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(smallSize2, EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(smallSize2, drawable.intrinsicSize)
            }

        // size: same, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val sameSize = Size(imageSize.width, imageSize.height)
        DisplayRequest(context, imageUri) {
            resize(sameSize, LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(sameSize, drawable.intrinsicSize)
            }
        DisplayRequest(context, imageUri) {
            resize(sameSize, SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(sameSize, drawable.intrinsicSize)
            }
        DisplayRequest(context, imageUri) {
            resize(sameSize, EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(sameSize, drawable.intrinsicSize)
            }

        // size: big, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val bigSize1 = Size(2500, 2100)
        DisplayRequest(context, imageUri) {
            resize(bigSize1, LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(imageSize, drawable.intrinsicSize)
                Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize1, SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(1291, 1084), drawable.intrinsicSize)
                Assert.assertEquals(bigSize1.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize1, EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize1, drawable.intrinsicSize)
            }

        val bigSize2 = Size(2100, 2500)
        DisplayRequest(context, imageUri) {
            resize(bigSize2, LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(imageSize, drawable.intrinsicSize)
                Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize2, SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(1291, 1537), drawable.intrinsicSize)
                Assert.assertEquals(bigSize2.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize2, EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize2, drawable.intrinsicSize)
            }

        val bigSize3 = Size(800, 2500)
        DisplayRequest(context, imageUri) {
            resize(bigSize3, LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(646, 968), drawable.intrinsicSize)
                Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize3, SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(620, 1936), drawable.intrinsicSize)
                Assert.assertEquals(bigSize3.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize3, EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize3, drawable.intrinsicSize)
            }

        val bigSize4 = Size(2500, 800)
        DisplayRequest(context, imageUri) {
            resize(bigSize4, LESS_PIXELS)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(646, 968), drawable.intrinsicSize)
                Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize4, SAME_ASPECT_RATIO)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(1291, 413), drawable.intrinsicSize)
                Assert.assertEquals(bigSize4.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(bigSize4, EXACTLY)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(bigSize4, drawable.intrinsicSize)
            }

        /* scale */
        val size = Size(600, 500)
        var sarStartCropBitmap: Bitmap?
        var sarCenterCropBitmap: Bitmap?
        var sarEndCropBitmap: Bitmap?
        var sarFillCropBitmap: Bitmap?
        DisplayRequest(context, imageUri) {
            resize(size, SAME_ASPECT_RATIO, START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                sarStartCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 269), drawable.intrinsicSize)
                Assert.assertEquals(size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(size, SAME_ASPECT_RATIO, CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                sarCenterCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 269), drawable.intrinsicSize)
                Assert.assertEquals(size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(size, SAME_ASPECT_RATIO, END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                sarEndCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 269), drawable.intrinsicSize)
                Assert.assertEquals(size.ratio, drawable.intrinsicSize.ratio)
            }
        DisplayRequest(context, imageUri) {
            resize(size, SAME_ASPECT_RATIO, FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                sarFillCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(Size(323, 269), drawable.intrinsicSize)
                Assert.assertEquals(size.ratio, drawable.intrinsicSize.ratio)
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
        DisplayRequest(context, imageUri) {
            resize(size, EXACTLY, START_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                exactlyStartCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, drawable.intrinsicSize)
            }
        DisplayRequest(context, imageUri) {
            resize(size, EXACTLY, CENTER_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                exactlyCenterCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, drawable.intrinsicSize)
            }
        DisplayRequest(context, imageUri) {
            resize(size, EXACTLY, END_CROP)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                exactlyEndCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, drawable.intrinsicSize)
            }
        DisplayRequest(context, imageUri) {
            resize(size, EXACTLY, FILL)
        }.let { runBlocking { sketch.execute(it) } }
            .asOrNull<DisplayResult.Success>()!!.apply {
                exactlyFillCropBitmap = drawable.asOrNull<BitmapDrawable>()!!.bitmap
                Assert.assertEquals(imageSize, imageInfo.size)
                Assert.assertEquals(size, drawable.intrinsicSize)
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
        // todo Write test cases
    }

    @Test
    fun testDisabledReuseBitmap() {
        // todo Write test cases
    }

    @Test
    fun testIgnoreExifOrientation() {
        // todo Write test cases
    }

    @Test
    fun testResultCachePolicy() {
        val context = getContext()
        val sketch = getSketch {
            httpStack(TestHttpStack(context))
        }
        val diskCache = sketch.diskCache
        val imageUri = newAssetUri("sample.jpeg")
        val request = DisplayRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resize(500, 500)
        }
        val resultCacheDataKey = request.newResultCacheDataKey()

        /* ENABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_DISK_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.RESULT_DISK_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        Assert.assertFalse(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(diskCache.exist(resultCacheDataKey))
        request.newDisplayRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testPlaceholderImage() {
        // todo Write test cases
    }

    @Test
    fun testErrorImage() {
        // todo Write test cases
    }

    @Test
    fun testTransition() {
        // todo Write test cases
    }

    @Test
    fun testDisabledAnimatedImage() {
        // todo Write test cases
    }

    @Test
    fun testResizeApplyToDrawable() {
        // todo Write test cases
    }

    @Test
    fun testMemoryCachePolicy() {
        val context = getContext()
        val sketch = getSketch {
            httpStack(TestHttpStack(context))
        }
        val memoryCache = sketch.memoryCache
        val imageUri = newAssetUri("sample.jpeg")
        val request = DisplayRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            resize(500, 500)
        }
        val memoryCacheDataKey = request.newMemoryCacheKey()

        /* ENABLED */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        /* DISABLED */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertFalse(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            runBlocking { sketch.execute(it) }
        }
        Assert.assertTrue(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        Assert.assertTrue(memoryCache.exist(memoryCacheDataKey))
        request.newDisplayRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            runBlocking { sketch.execute(it) }
        }.asOrNull<DisplayResult.Success>()!!.apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }
}