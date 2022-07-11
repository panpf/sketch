package com.github.panpf.sketch.test.request

import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ACES
import android.graphics.ColorSpace.Named.BT709
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.DefaultSizeResolver
import com.github.panpf.sketch.resize.DisplaySizeResolver
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.ViewSizeResolver
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.sketch.resize.longImageScale
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadRequestTest {

    @Test
    fun testFun() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest(context1, uriString1).apply {
            Assert.assertSame(context1, this.context)
            Assert.assertEquals("asset://sample.jpeg", uriString)
            Assert.assertNull(this.listener)
            Assert.assertNull(this.progressListener)
            Assert.assertNull(this.target)
            Assert.assertSame(GlobalLifecycle, this.lifecycle)

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
            Assert.assertNull(this.resizeSize)
            Assert.assertEquals(
                DefaultSizeResolver(DisplaySizeResolver(context1)),
                this.resizeSizeResolver
            )
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), this.resizePrecisionDecider)
            Assert.assertEquals(FixedScaleDecider(CENTER_CROP), this.resizeScaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertFalse(this.disallowReuseBitmap)
            Assert.assertFalse(this.ignoreExifOrientation)
            Assert.assertEquals(ENABLED, this.resultCachePolicy)
            Assert.assertNull(this.placeholder)
            Assert.assertNull(this.error)
            Assert.assertNull(this.transition)
            Assert.assertFalse(this.disallowAnimatedImage)
            Assert.assertFalse(this.resizeApplyToDrawable)
            Assert.assertEquals(ENABLED, this.memoryCachePolicy)
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @Test
    fun testNewBuilder() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")

        DownloadRequest(context1, uriString1).newBuilder().build().apply {
            Assert.assertEquals(NETWORK, depth)
        }
        DownloadRequest(context1, uriString1).newBuilder {
            depth(LOCAL)
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
        }
        (DownloadRequest(context1, uriString1) as ImageRequest).newBuilder {
            depth(LOCAL)
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
        }

        DownloadRequest(context1, uriString1).newRequest().apply {
            Assert.assertEquals(NETWORK, depth)
        }
        DownloadRequest(context1, uriString1).newRequest {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
        }
        (DownloadRequest(context1, uriString1) as ImageRequest).newRequest {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
        }

        DownloadRequest(context1, uriString1).newDownloadBuilder().build().apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(listener)
            Assert.assertNull(progressListener)
        }
        DownloadRequest(context1, uriString1).newDownloadBuilder {
            depth(LOCAL)
            listener(
                onStart = { request: DownloadRequest ->

                },
                onCancel = { request: DownloadRequest ->

                },
                onError = { request: DownloadRequest, result: DownloadResult.Error ->

                },
                onSuccess = { request: DownloadRequest, result: DownloadResult.Success ->

                },
            )
            progressListener { request: DownloadRequest, totalLength: Long, completedLength: Long ->

            }
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNotNull(listener)
            Assert.assertNotNull(progressListener)
        }

        DownloadRequest(context1, uriString1).newDownloadRequest().apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(listener)
            Assert.assertNull(progressListener)
        }
        DownloadRequest(context1, uriString1).newDownloadRequest {
            depth(LOCAL)
            listener(
                onStart = { request: DownloadRequest ->

                },
                onCancel = { request: DownloadRequest ->

                },
                onError = { request: DownloadRequest, result: DownloadResult.Error ->

                },
                onSuccess = { request: DownloadRequest, result: DownloadResult.Success ->

                },
            )
            progressListener { request: DownloadRequest, totalLength: Long, completedLength: Long ->

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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest(context1, uriString1).apply {
            Assert.assertEquals(context1, context)
            Assert.assertNotEquals(context1, context.applicationContext)
        }
    }

    @Test
    fun testTarget() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")

        DownloadRequest(context1, uriString1).apply {
            Assert.assertNull(target)
        }

        DownloadRequest(context1, uriString1) {
            target(object : DownloadTarget {

            })
        }.apply {
            Assert.assertNotNull(target)
        }

        DownloadRequest(context1, uriString1) {
            target(null)
        }.apply {
            Assert.assertNull(target)
        }

        DownloadRequest(context1, uriString1) {
            target(onStart = {}, onSuccess = {}, onError = {})
        }.apply {
            Assert.assertNotNull(target)
        }
        DownloadRequest(context1, uriString1) {
            target(onStart = {})
        }.apply {
            Assert.assertNotNull(target)
        }
        DownloadRequest(context1, uriString1) {
            target(onSuccess = {})
        }.apply {
            Assert.assertNotNull(target)
        }
        DownloadRequest(context1, uriString1) {
            target(onError = {})
        }.apply {
            Assert.assertNotNull(target)
        }
    }

    @Test
    fun testLifecycle() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        var lifecycle1: Lifecycle? = null
        val lifecycleOwner = LifecycleOwner { lifecycle1!! }
        lifecycle1 = LifecycleRegistry(lifecycleOwner)

        DownloadRequest(context1, uriString1).apply {
            Assert.assertEquals(GlobalLifecycle, this.lifecycle)
        }

        DownloadRequest(context1, uriString1) {
            lifecycle(lifecycle1)
        }.apply {
            Assert.assertEquals(lifecycle1, this.lifecycle)
        }

        val activity = TestActivity::class.launchActivity().getActivitySync()

        DownloadRequest(activity, uriString1).apply {
            Assert.assertEquals(activity.lifecycle, this.lifecycle)
        }
    }

    @Test
    fun testKeyAndToString() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")

        DownloadRequest(context1, uriString1).apply {
            Assert.assertEquals(newKey(), key)
            Assert.assertEquals("DownloadRequest($key)", toString())
        }

        DownloadRequest(context1, uriString1) {
            resize(100, 100)
        }.apply {
            Assert.assertEquals(newKey(), key)
            Assert.assertEquals("DownloadRequest($key)", toString())
        }

        DownloadRequest(context1, uriString1) {
            memoryCachePolicy(WRITE_ONLY)
        }.apply {
            Assert.assertEquals(newKey(), key)
            Assert.assertEquals("DownloadRequest($key)", toString())
        }
    }

    @Test
    fun testCacheKey() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")

        DownloadRequest(context1, uriString1).apply {
            Assert.assertEquals(newCacheKey(), cacheKey)
        }

        DownloadRequest(context1, uriString1) {
            resize(100, 100)
        }.apply {
            Assert.assertEquals(newCacheKey(), cacheKey)
        }

        DownloadRequest(context1, uriString1) {
            bitmapConfig(ALPHA_8)
        }.apply {
            Assert.assertEquals(newCacheKey(), cacheKey)
        }
    }

    @Test
    fun testDefinedOptions() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")

        DownloadRequest(context1, uriString1).apply {
            Assert.assertEquals(ImageOptions(), definedOptions)
        }

        DownloadRequest(context1, uriString1) {
            resize(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }.apply {
            Assert.assertEquals(ImageOptions {
                resize(100, 50)
                addTransformations(CircleCropTransformation())
                crossfade()
            }, definedOptions)
        }
    }

    @Test
    fun testGlobalOptions() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")

        DownloadRequest(context1, uriString1).apply {
            Assert.assertNull(globalOptions)
        }

        val options = ImageOptions {
            resize(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }
        DownloadRequest(context1, uriString1) {
            global(options)
        }.apply {
            Assert.assertSame(options, globalOptions)
        }
    }

    @Test
    fun testMerge() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(NETWORK, depth)
                Assert.assertNull(parameters)
            }

            merge(ImageOptions {
                resize(100, 50)
                memoryCachePolicy(DISABLED)
                addTransformations(CircleCropTransformation())
                crossfade()
            })
            build().apply {
                Assert.assertEquals(Resize(100, 50, EXACTLY, CENTER_CROP), resize)
                Assert.assertEquals(DISABLED, memoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transition)
            }

            merge(ImageOptions {
                memoryCachePolicy(READ_ONLY)
            })
            build().apply {
                Assert.assertEquals(Resize(100, 50, EXACTLY, CENTER_CROP), resize)
                Assert.assertEquals(DISABLED, memoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transition)
            }
        }
    }

    @Test
    fun testDepth() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(parameters)
        }

        DownloadRequest(context1, uriString1) {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNull(parameters)
        }

        DownloadRequest(context1, uriString1) {
            depth(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(parameters)
        }
    }

    @Test
    fun testParameters() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(
                Resize(100, 100, SAME_ASPECT_RATIO, START_CROP)
            )
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(
                    FixedPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            resize(null)
            build().apply {
                Assert.assertNull(resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(
                Size(100, 100),
                longImageClipPrecision(SAME_ASPECT_RATIO),
                longImageScale(START_CROP, END_CROP)
            )
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(
                    LongImageClipPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
                Assert.assertEquals(LongImageScaleDecider(START_CROP, END_CROP), resizeScaleDecider)
            }

            resize(Size(100, 100), precision = longImageClipPrecision(SAME_ASPECT_RATIO))
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(
                    LongImageClipPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(Size(100, 100), scale = longImageScale(START_CROP, END_CROP))
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(LongImageScaleDecider(START_CROP, END_CROP), resizeScaleDecider)
            }

            resize(Size(200, 200), LESS_PIXELS, START_CROP)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            resize(Size(200, 200), precision = LESS_PIXELS)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(Size(200, 200), scale = START_CROP)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            resize(Size(300, 300))
            build().apply {
                Assert.assertEquals(Size(300, 300), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }


            resize(
                100,
                100,
                longImageClipPrecision(SAME_ASPECT_RATIO),
                longImageScale(START_CROP, END_CROP)
            )
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(
                    LongImageClipPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
                Assert.assertEquals(LongImageScaleDecider(START_CROP, END_CROP), resizeScaleDecider)
            }

            resize(100, 100, precision = longImageClipPrecision(SAME_ASPECT_RATIO))
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(
                    LongImageClipPrecisionDecider(SAME_ASPECT_RATIO),
                    resizePrecisionDecider
                )
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(100, 100, scale = longImageScale(START_CROP, END_CROP))
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(LongImageScaleDecider(START_CROP, END_CROP), resizeScaleDecider)
            }

            resize(200, 200, LESS_PIXELS, START_CROP)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            resize(200, 200, precision = LESS_PIXELS)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resize(200, 200, scale = START_CROP)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(START_CROP), resizeScaleDecider)
            }

            resize(300, 300)
            build().apply {
                Assert.assertEquals(Size(300, 300), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }
        }

        DownloadRequest(context1, uriString1).apply {
            Assert.assertNull(resize)
        }

        DownloadRequest(context1, uriString1) {
            resizeSize(0, 100)
        }.apply {
            Assert.assertNull(resize)
        }

        DownloadRequest(context1, uriString1) {
            resizeSize(100, 0)
        }.apply {
            Assert.assertNull(resize)
        }

        DownloadRequest(context1, uriString1) {
            resizeSize(100, 100)
        }.apply {
            Assert.assertNotNull(resize)
        }
    }

    @Test
    fun testResizeSize() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
            }

            resizeSize(Size(100, 100))
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
            }

            resizeSize(200, 200)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
            }

            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(EXACTLY), resizePrecisionDecider)
            }

            resizeSize(null)
            build().apply {
                Assert.assertNull(resizeSize)
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
            }
        }
    }

    @Test
    fun testResizeSizeResolver() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        val imageView = ImageView(context1)

        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(
                    DefaultSizeResolver(DisplaySizeResolver(context1)),
                    resizeSizeResolver
                )
            }

            resizeSizeResolver(ViewSizeResolver(imageView))
            build().apply {
                Assert.assertEquals(ViewSizeResolver(imageView), resizeSizeResolver)
            }

            resizeSizeResolver(null)
            build().apply {
                Assert.assertEquals(
                    DefaultSizeResolver(DisplaySizeResolver(context1)),
                    resizeSizeResolver
                )
            }
        }

        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(
                    DefaultSizeResolver(DisplaySizeResolver(context1)),
                    resizeSizeResolver
                )
            }

            resizeSize(100, 100)
            build().apply {
                Assert.assertNull(resizeSizeResolver)
            }

            resizeSize(null)
            build().apply {
                Assert.assertEquals(
                    DefaultSizeResolver(DisplaySizeResolver(context1)),
                    resizeSizeResolver
                )
            }
        }
    }

    @Test
    fun testResizePrecision() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
            }

            resizePrecision(longImageClipPrecision(EXACTLY))
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

        val request = DownloadRequest(context1, uriString1).apply {
            Assert.assertNull(resizeSize)
            Assert.assertEquals(
                DefaultSizeResolver(DisplaySizeResolver(context1)),
                resizeSizeResolver
            )
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }
        val size = runBlocking {
            request.resizeSizeResolver!!.size()
        }
        val request1 = request.newDownloadRequest {
            resizeSize(size)
        }.apply {
            Assert.assertNotNull(resizeSize)
            Assert.assertEquals(
                DefaultSizeResolver(DisplaySizeResolver(context1)),
                resizeSizeResolver
            )
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }
        request1.newDownloadRequest().apply {
            Assert.assertNotNull(resizeSize)
            Assert.assertEquals(
                DefaultSizeResolver(DisplaySizeResolver(context1)),
                resizeSizeResolver
            )
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }
    }

    @Test
    fun testResizeScale() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(FixedScaleDecider(CENTER_CROP), resizeScaleDecider)
            }

            resizeScale(longImageScale(START_CROP, END_CROP))
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
        }
    }

    @Test
    fun testTransformations() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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

            placeholder(android.R.drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    DrawableStateImage(android.R.drawable.bottom_bar),
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
    fun testError() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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

            error(android.R.drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                    error
                )
            }

            error(android.R.drawable.bottom_bar) {
                uriEmptyError(android.R.drawable.alert_dark_frame)
            }
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)){
                        uriEmptyError(android.R.drawable.alert_dark_frame)
                    },
                    error
                )
            }

            error()
            build().apply {
                Assert.assertNull(error)
            }

            error {
                uriEmptyError(android.R.drawable.btn_dialog)
            }
            build().apply {
                Assert.assertNotNull(error)
            }
        }
    }

    @Test
    fun testTransition() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(transition)
            }

            transition(CrossfadeTransition.Factory())
            build().apply {
                Assert.assertEquals(CrossfadeTransition.Factory(), transition)
            }

            transition(null)
            build().apply {
                Assert.assertNull(transition)
            }
        }
    }

    @Test
    fun testResizeApplyToDrawable() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
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
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(listener)
            }

            listener(onStart = {}, onCancel = {}, onError = { _, _ -> }, onSuccess = { _, _ -> })
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener is Listener<*, *, *>)
            }

            listener(onStart = {})
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener is Listener<*, *, *>)
            }

            listener(onCancel = {})
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener is Listener<*, *, *>)
            }

            listener(onError = { _, _ -> })
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener is Listener<*, *, *>)
            }

            listener(onSuccess = { _, _ -> })
            build().apply {
                Assert.assertNotNull(listener)
                Assert.assertTrue(listener is Listener<*, *, *>)
            }

            listener(null)
            build().apply {
                Assert.assertNull(listener)
            }
        }
    }

    @Test
    fun testProgressListener() {
        val context1 = getTestContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(progressListener)
            }

            progressListener { _, _, _ -> }
            build().apply {
                Assert.assertNotNull(progressListener)
                Assert.assertTrue(progressListener is ProgressListener<*>)
            }

            progressListener(null)
            build().apply {
                Assert.assertNull(progressListener)
            }
        }
    }
}