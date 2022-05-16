package com.github.panpf.sketch.test.request

import android.R.drawable
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
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.RequestDepth.LOCAL
import com.github.panpf.sketch.request.RequestDepth.NETWORK
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
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.test.getContext
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.transform.BlurTransformation
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
class LoadRequestTest {

    @Test
    fun testFun() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest(context1, uriString1).apply {
            Assert.assertSame(context1, this.context)
            Assert.assertEquals("asset://sample.jpeg", uriString)
            Assert.assertNull(this.listener)
            Assert.assertNull(this.progressListener)
            Assert.assertNull(this.target)
            Assert.assertSame(GlobalLifecycle, this.lifecycle)

            Assert.assertEquals(NETWORK, this.depth)
            Assert.assertNull(this.parameters)
            Assert.assertNull(this.httpHeaders)
            Assert.assertEquals(ENABLED, this.downloadDiskCachePolicy)
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
            Assert.assertFalse(this.disabledReuseBitmap)
            Assert.assertFalse(this.ignoreExifOrientation)
            Assert.assertEquals(ENABLED, this.bitmapResultDiskCachePolicy)
            Assert.assertNull(this.placeholderImage)
            Assert.assertNull(this.errorImage)
            Assert.assertNull(this.transition)
            Assert.assertFalse(this.disabledAnimatedImage)
            Assert.assertFalse(this.resizeApplyToDrawable)
            Assert.assertEquals(ENABLED, this.bitmapMemoryCachePolicy)
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @Test
    fun testNewBuilder() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")

        LoadRequest(context1, uriString1).newBuilder {
            depth(LOCAL)
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
        }

        LoadRequest(context1, uriString1).newRequest {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
        }

        LoadRequest(context1, uriString1).newLoadBuilder {
            depth(LOCAL)
            listener(
                onStart = { request: LoadRequest ->

                },
                onCancel = { request: LoadRequest ->

                },
                onError = { request: LoadRequest, result: LoadResult.Error ->

                },
                onSuccess = { request: LoadRequest, result: LoadResult.Success ->

                },
            )
            progressListener { request: LoadRequest, totalLength: Long, completedLength: Long ->

            }
        }.build().apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNotNull(listener)
            Assert.assertNotNull(progressListener)
        }

        LoadRequest(context1, uriString1).newLoadRequest {
            depth(LOCAL)
            listener(
                onStart = { request: LoadRequest ->

                },
                onCancel = { request: LoadRequest ->

                },
                onError = { request: LoadRequest, result: LoadResult.Error ->

                },
                onSuccess = { request: LoadRequest, result: LoadResult.Success ->

                },
            )
            progressListener { request: LoadRequest, totalLength: Long, completedLength: Long ->

            }
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNotNull(listener)
            Assert.assertNotNull(progressListener)
        }
    }

    @Test
    fun testContext() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(context1, context)
            Assert.assertNotEquals(context1, context.applicationContext)
        }
    }

    @Test
    fun testTarget() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")

        LoadRequest(context1, uriString1).apply {
            Assert.assertNull(target)
        }

        LoadRequest(context1, uriString1) {
            target(object : LoadTarget {

            })
        }.apply {
            Assert.assertNotNull(target)
        }

        LoadRequest(context1, uriString1) {
            target(null)
        }.apply {
            Assert.assertNull(target)
        }
    }

    @Test
    fun testLifecycle() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        var lifecycle1: Lifecycle? = null
        val lifecycleOwner = LifecycleOwner { lifecycle1!! }
        lifecycle1 = LifecycleRegistry(lifecycleOwner)

        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(GlobalLifecycle, this.lifecycle)
        }

        LoadRequest(context1, uriString1) {
            lifecycle(lifecycle1)
        }.apply {
            Assert.assertEquals(lifecycle1, this.lifecycle)
        }

        val activity = TestActivity::class.launchActivity().getActivitySync()

        LoadRequest(activity, uriString1).apply {
            Assert.assertEquals(activity.lifecycle, this.lifecycle)
        }
    }

    @Test
    fun testKey() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")

        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(newKey(), key)
        }

        LoadRequest(context1, uriString1) {
            resize(100, 100)
        }.apply {
            Assert.assertEquals(newKey(), key)
        }

        LoadRequest(context1, uriString1) {
            bitmapMemoryCachePolicy(WRITE_ONLY)
        }.apply {
            Assert.assertEquals(newKey(), key)
        }
    }

    @Test
    fun testCacheKey() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")

        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(newCacheKey(), cacheKey)
        }

        LoadRequest(context1, uriString1) {
            resize(100, 100)
        }.apply {
            Assert.assertEquals(newCacheKey(), cacheKey)
        }

        LoadRequest(context1, uriString1) {
            bitmapConfig(ALPHA_8)
        }.apply {
            Assert.assertEquals(newCacheKey(), cacheKey)
        }
    }

    @Test
    fun testDefinedOptions() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")

        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(ImageOptions(), definedOptions)
        }

        LoadRequest(context1, uriString1) {
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")

        LoadRequest(context1, uriString1).apply {
            Assert.assertNull(globalOptions)
        }

        val options = ImageOptions {
            resize(100, 50)
            addTransformations(CircleCropTransformation())
            crossfade()
        }
        LoadRequest(context1, uriString1) {
            global(options)
        }.apply {
            Assert.assertSame(options, globalOptions)
        }
    }

    @Test
    fun testMerge() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(NETWORK, depth)
                Assert.assertNull(depthFrom)
                Assert.assertNull(parameters)
            }

            merge(ImageOptions {
                resize(100, 50)
                bitmapMemoryCachePolicy(DISABLED)
                addTransformations(CircleCropTransformation())
                crossfade()
            })
            build().apply {
                Assert.assertEquals(Resize(100, 50, EXACTLY, CENTER_CROP), resize)
                Assert.assertEquals(DISABLED, bitmapMemoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transition)
            }

            merge(ImageOptions {
                bitmapMemoryCachePolicy(READ_ONLY)
            })
            build().apply {
                Assert.assertEquals(Resize(100, 50, EXACTLY, CENTER_CROP), resize)
                Assert.assertEquals(DISABLED, bitmapMemoryCachePolicy)
                Assert.assertEquals(listOf(CircleCropTransformation()), transformations)
                Assert.assertEquals(CrossfadeTransition.Factory(), transition)
            }
        }
    }

    @Test
    fun testDepth() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        LoadRequest(context1, uriString1) {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        LoadRequest(context1, uriString1) {
            depth(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        LoadRequest(context1, uriString1) {
            depthFrom("testDepthFrom")
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertEquals("testDepthFrom", depthFrom)
            Assert.assertNotNull("testDepthFrom", parameters?.get(ImageRequest.REQUEST_DEPTH_FROM))
        }

        LoadRequest(context1, uriString1) {
            depthFrom(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }
    }

    @Test
    fun testParameters() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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
            setParameter("key2", "value2")
            build().apply {
                Assert.assertEquals(2, parameters?.size)
                Assert.assertEquals("value1", parameters?.get("key1"))
                Assert.assertEquals("value2", parameters?.get("key2"))
            }

            setParameter("key2", "value2.1")
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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
    fun testLoadDiskCachePolicy() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(ENABLED, downloadDiskCachePolicy)
            }

            downloadDiskCachePolicy(READ_ONLY)
            build().apply {
                Assert.assertEquals(READ_ONLY, downloadDiskCachePolicy)
            }

            downloadDiskCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, downloadDiskCachePolicy)
            }

            downloadDiskCachePolicy(null)
            build().apply {
                Assert.assertEquals(ENABLED, downloadDiskCachePolicy)
            }
        }
    }

    @Test
    fun testBitmapConfig() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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

            lowQualityBitmapConfig()
            build().apply {
                Assert.assertEquals(BitmapConfig.LOW_QUALITY, bitmapConfig)
            }

            middenQualityBitmapConfig()
            build().apply {
                Assert.assertEquals(BitmapConfig.MIDDEN_QUALITY, bitmapConfig)
            }

            highQualityBitmapConfig()
            build().apply {
                Assert.assertEquals(BitmapConfig.HIGH_QUALITY, bitmapConfig)
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

        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertFalse(preferQualityOverSpeed)
            }

            preferQualityOverSpeed(true)
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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

        LoadRequest(context1, uriString1).apply {
            Assert.assertNull(resize)
        }

        LoadRequest(context1, uriString1) {
            resizeSize(0, 100)
        }.apply {
            Assert.assertNull(resize)
        }

        LoadRequest(context1, uriString1) {
            resizeSize(100, 0)
        }.apply {
            Assert.assertNull(resize)
        }

        LoadRequest(context1, uriString1) {
            resizeSize(100, 100)
        }.apply {
            Assert.assertNotNull(resize)
        }
    }

    @Test
    fun testResizeSize() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        val imageView = ImageView(context1)

        LoadRequest.Builder(context1, uriString1).apply {
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

        LoadRequest.Builder(context1, uriString1).apply {
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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

        val request = LoadRequest(context1, uriString1).apply {
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
        val request1 = request.newLoadRequest() {
            resizeSize(size)
        }.apply {
            Assert.assertNotNull(resizeSize)
            Assert.assertEquals(
                DefaultSizeResolver(DisplaySizeResolver(context1)),
                resizeSizeResolver
            )
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), resizePrecisionDecider)
        }
        request1.newLoadRequest().apply {
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(transformations)
            }

            /* transformations() */
            transformations(listOf(CircleCropTransformation(), BlurTransformation()))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), BlurTransformation()),
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
            addTransformations(listOf(CircleCropTransformation(), BlurTransformation()))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), BlurTransformation()),
                    transformations
                )
            }
            addTransformations(listOf(CircleCropTransformation(), RotateTransformation(40)))
            build().apply {
                Assert.assertEquals(
                    listOf(
                        CircleCropTransformation(),
                        BlurTransformation(),
                        RotateTransformation(40)
                    ),
                    transformations
                )
            }
            removeTransformations(listOf(BlurTransformation()))
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(listOf(CircleCropTransformation(), RotateTransformation(40)))
            build().apply {
                Assert.assertNull(transformations)
            }

            /* addTransformations(vararg), removeTransformations(vararg) */
            addTransformations(CircleCropTransformation(), BlurTransformation())
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), BlurTransformation()),
                    transformations
                )
            }
            addTransformations(CircleCropTransformation(), RotateTransformation(40))
            build().apply {
                Assert.assertEquals(
                    listOf(
                        CircleCropTransformation(),
                        BlurTransformation(),
                        RotateTransformation(40)
                    ),
                    transformations
                )
            }
            removeTransformations(BlurTransformation())
            build().apply {
                Assert.assertEquals(
                    listOf(CircleCropTransformation(), RotateTransformation(40)),
                    transformations
                )
            }
            removeTransformations(CircleCropTransformation(), RotateTransformation(40))
            build().apply {
                Assert.assertNull(transformations)
            }
        }
    }

    @Test
    fun testDisabledReuseBitmap() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertFalse(disabledReuseBitmap)
            }

            disabledReuseBitmap(true)
            build().apply {
                Assert.assertEquals(true, disabledReuseBitmap)
            }

            disabledReuseBitmap(false)
            build().apply {
                Assert.assertEquals(false, disabledReuseBitmap)
            }

            disabledReuseBitmap(null)
            build().apply {
                Assert.assertFalse(disabledReuseBitmap)
            }
        }
    }

    @Test
    fun testIgnoreExifOrientation() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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
    fun testBitmapResultDiskCachePolicy() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(ENABLED, bitmapResultDiskCachePolicy)
            }

            bitmapResultDiskCachePolicy(READ_ONLY)
            build().apply {
                Assert.assertEquals(READ_ONLY, bitmapResultDiskCachePolicy)
            }

            bitmapResultDiskCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, bitmapResultDiskCachePolicy)
            }

            bitmapResultDiskCachePolicy(null)
            build().apply {
                Assert.assertEquals(ENABLED, bitmapResultDiskCachePolicy)
            }
        }
    }

    @Test
    fun testDisabledAnimatedImage() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertFalse(disabledAnimatedImage)
            }

            disabledAnimatedImage(true)
            build().apply {
                Assert.assertEquals(true, disabledAnimatedImage)
            }

            disabledAnimatedImage(false)
            build().apply {
                Assert.assertEquals(false, disabledAnimatedImage)
            }

            disabledAnimatedImage(null)
            build().apply {
                Assert.assertFalse(disabledAnimatedImage)
            }
        }
    }

    @Test
    fun testPlaceholderImage() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(placeholderImage)
            }

            placeholder(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(ColorStateImage(IntColor(Color.BLUE)), placeholderImage)
            }

            placeholder(ColorDrawable(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, placeholderImage is DrawableStateImage)
            }

            placeholder(android.R.drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    DrawableStateImage(drawable.bottom_bar),
                    placeholderImage
                )
            }

            placeholder(null)
            build().apply {
                Assert.assertNull(placeholderImage)
            }
        }
    }

    @Test
    fun testErrorImage() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertNull(errorImage)
            }

            error(ColorStateImage(IntColor(Color.BLUE)))
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage.Builder(ColorStateImage(IntColor(Color.BLUE))).build(),
                    errorImage
                )
            }

            error(ColorDrawable(Color.GREEN))
            build().apply {
                Assert.assertEquals(true, errorImage is ErrorStateImage)
            }

            error(android.R.drawable.bottom_bar)
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage.Builder(DrawableStateImage(drawable.bottom_bar))
                        .build(),
                    errorImage
                )
            }

            error(android.R.drawable.bottom_bar) {
                uriEmptyError(android.R.drawable.alert_dark_frame)
            }
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage.Builder(DrawableStateImage(drawable.bottom_bar))
                        .uriEmptyError(android.R.drawable.alert_dark_frame).build(),
                    errorImage
                )
            }

            error(null)
            build().apply {
                Assert.assertNull(errorImage)
            }
        }
    }

    @Test
    fun testTransition() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
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
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertFalse(resizeApplyToDrawable)
            }

            resizeApplyToDrawable(true)
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
    fun testBitmapMemoryCachePolicy() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest.Builder(context1, uriString1).apply {
            build().apply {
                Assert.assertEquals(ENABLED, bitmapMemoryCachePolicy)
            }

            bitmapMemoryCachePolicy(READ_ONLY)
            build().apply {
                Assert.assertEquals(READ_ONLY, bitmapMemoryCachePolicy)
            }

            bitmapMemoryCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, bitmapMemoryCachePolicy)
            }

            bitmapMemoryCachePolicy(null)
            build().apply {
                Assert.assertEquals(ENABLED, bitmapMemoryCachePolicy)
            }
        }
    }
}