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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.sketch.resize.longImageScale
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.stateimage.newErrorStateImage
import com.github.panpf.sketch.test.utils.TestTransition
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageOptionsTest {

    @Test
    fun testFun() {
        ImageOptions().apply {
            Assert.assertNotNull(this)
            Assert.assertTrue(this.isEmpty())
        }
        ImageOptions {
            depth(LOCAL)
        }.apply {
            Assert.assertNotNull(this)
            Assert.assertFalse(this.isEmpty())
        }
    }

    @Test
    fun testIsEmpty() {
        ImageOptions().apply {
            Assert.assertTrue(this.isEmpty())
            Assert.assertNull(this.depth)
            Assert.assertNull(this.parameters)
            Assert.assertNull(this.httpHeaders)
            Assert.assertNull(this.downloadCachePolicy)
            Assert.assertNull(this.bitmapConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertNull(this.colorSpace)
            }
            @Suppress("DEPRECATION")
            Assert.assertNull(this.preferQualityOverSpeed)
            Assert.assertNull(this.resizeSize)
//            Assert.assertNull(this.resizeSizeResolver)
            Assert.assertNull(this.resizePrecisionDecider)
            Assert.assertNull(this.resizeScaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertNull(this.disabledReuseBitmap)
            Assert.assertNull(this.ignoreExifOrientation)
            Assert.assertNull(this.resultCachePolicy)
            Assert.assertNull(this.placeholderImage)
            Assert.assertNull(this.errorImage)
            Assert.assertNull(this.transition)
            Assert.assertNull(this.disabledAnimatedImage)
            Assert.assertNull(this.resizeApplyToDrawable)
            Assert.assertNull(this.memoryCachePolicy)
        }

        ImageOptions {
            depth(LOCAL)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertEquals(LOCAL, this.depth)
        }

        ImageOptions {
            setParameter("key", "value")
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.parameters)
        }

        ImageOptions {
            addHttpHeader("headerKey", "headerValue")
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.httpHeaders)
        }

        ImageOptions {
            downloadCachePolicy(READ_ONLY)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.downloadCachePolicy)
        }

        ImageOptions {
            bitmapConfig(ALPHA_8)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.bitmapConfig)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageOptions {
                colorSpace(ColorSpace.get(BT709))
            }.apply {
                Assert.assertFalse(this.isEmpty())
                Assert.assertNotNull(this.colorSpace)
            }
        }

        ImageOptions {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            @Suppress("DEPRECATION")
            Assert.assertNotNull(this.preferQualityOverSpeed)
        }

        ImageOptions {
            resizeSize(100, 100)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.resizeSize)
        }

//        ImageOptions {
//            resizeSizeResolver(DisplaySizeResolver(context))
//        }.apply {
//            Assert.assertFalse(this.isEmpty())
//            Assert.assertNotNull(this.resizeSizeResolver)
//        }

        ImageOptions {
            resizePrecision(EXACTLY)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.resizePrecisionDecider)
        }

        ImageOptions {
            resizeScale(CENTER_CROP)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.resizeScaleDecider)
        }

        ImageOptions {
            transformations(RoundedCornersTransformation())
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.transformations)
        }

        ImageOptions {
            disabledReuseBitmap(true)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.disabledReuseBitmap)
        }

        ImageOptions {
            ignoreExifOrientation(true)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.ignoreExifOrientation)
        }

        ImageOptions {
            resultCachePolicy(ENABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.resultCachePolicy)
        }

        ImageOptions {
            disabledAnimatedImage(false)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.disabledAnimatedImage)
        }

        ImageOptions {
            placeholder(ColorDrawable(Color.BLUE))
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.placeholderImage)
        }

        ImageOptions {
            error(ColorDrawable(Color.BLUE))
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.errorImage)
        }

        ImageOptions {
            transition(CrossfadeTransition.Factory())
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.transition)
        }

        ImageOptions {
            resizeApplyToDrawable(true)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.resizeApplyToDrawable)
        }

        ImageOptions {
            memoryCachePolicy(ENABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.memoryCachePolicy)
        }
    }


    @Test
    fun testNewBuilder() {
        ImageOptions().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newBuilder().build().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newBuilder {
            depth(NETWORK)
        }.build().apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(depth)
        }

        ImageOptions().newBuilder {
            downloadCachePolicy(DISABLED)
        }.build().apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(downloadCachePolicy)
        }
    }

    @Test
    fun testNewOptions() {
        ImageOptions().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newOptions().apply {
            Assert.assertTrue(this.isEmpty())
        }

        ImageOptions().newOptions {
            depth(NETWORK)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(depth)
        }

        ImageOptions().newOptions {
            downloadCachePolicy(DISABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(downloadCachePolicy)
        }

        val options = ImageOptions()
        Assert.assertTrue(options == options.newOptions())
        Assert.assertFalse(options == options.newOptions {
            downloadCachePolicy(DISABLED)
        })
        Assert.assertFalse(options === options.newOptions())
    }

    @Test
    fun testMerged() {
        val options = ImageOptions()
        Assert.assertTrue(options == options.merged(ImageOptions()))
        Assert.assertTrue(options != options.merged(ImageOptions {
            depth(MEMORY)
        }))
        Assert.assertFalse(options === options.merged(ImageOptions()))

        ImageOptions().apply {
            Assert.assertEquals(null, this.depth)
        }.merged(ImageOptions {
            depth(LOCAL)
        }).apply {
            Assert.assertEquals(LOCAL, this.depth)
        }.merged(ImageOptions {
            depth(NETWORK)
        }).apply {
            Assert.assertEquals(LOCAL, this.depth)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.parameters)
        }.merged(ImageOptions {
            setParameter("key", "value")
        }).apply {
            Assert.assertEquals("value", this.parameters?.get("key"))
        }.merged(ImageOptions {
            setParameter("key", "value1")
        }).apply {
            Assert.assertEquals("value", this.parameters?.get("key"))
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.httpHeaders)
        }.merged(ImageOptions {
            addHttpHeader("addKey", "addValue")
            setHttpHeader("setKey", "setValue")
        }).apply {
            Assert.assertEquals(listOf("addValue"), this.httpHeaders?.getAdd("addKey"))
            Assert.assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }.merged(ImageOptions {
            addHttpHeader("addKey", "addValue1")
            setHttpHeader("setKey", "setValue1")
        }).apply {
            Assert.assertEquals(listOf("addValue", "addValue1"), this.httpHeaders?.getAdd("addKey"))
            Assert.assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.downloadCachePolicy)
        }.merged(ImageOptions {
            downloadCachePolicy(DISABLED)
        }).apply {
            Assert.assertEquals(DISABLED, this.downloadCachePolicy)
        }.merged(ImageOptions {
            downloadCachePolicy(READ_ONLY)
        }).apply {
            Assert.assertEquals(DISABLED, this.downloadCachePolicy)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.bitmapConfig)
        }.merged(ImageOptions {
            bitmapConfig(ARGB_8888)
        }).apply {
            Assert.assertEquals(BitmapConfig(ARGB_8888), this.bitmapConfig)
        }.merged(ImageOptions {
            bitmapConfig(RGB_565)
        }).apply {
            Assert.assertEquals(BitmapConfig(ARGB_8888), this.bitmapConfig)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageOptions().apply {
                Assert.assertEquals(null, this.colorSpace)
            }.merged(ImageOptions {
                colorSpace(ColorSpace.get(BT709))
            }).apply {
                Assert.assertEquals(ColorSpace.get(BT709), this.colorSpace)
            }.merged(ImageOptions {
                colorSpace(ColorSpace.get(ACES))
            }).apply {
                Assert.assertEquals(ColorSpace.get(BT709), this.colorSpace)
            }
        }

        @Suppress("DEPRECATION")
        ImageOptions().apply {
            Assert.assertEquals(null, this.preferQualityOverSpeed)
        }.merged(ImageOptions {
            preferQualityOverSpeed(true)
        }).apply {
            Assert.assertEquals(true, this.preferQualityOverSpeed)
        }.merged(ImageOptions {
            preferQualityOverSpeed(false)
        }).apply {
            Assert.assertEquals(true, this.preferQualityOverSpeed)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.resizeSize)
        }.merged(ImageOptions {
            resizeSize(100, 100)
        }).apply {
            Assert.assertEquals(Size(100, 100), this.resizeSize)
        }.merged(ImageOptions {
            resizeSize(200, 200)
        }).apply {
            Assert.assertEquals(Size(100, 100), this.resizeSize)
        }

//        ImageOptions().apply {
//            Assert.assertEquals(null, this.resizeSizeResolver)
//        }.merged(ImageOptions {
//            resizeSizeResolver(DisplaySizeResolver(context))
//        }).apply {
//            Assert.assertEquals(DisplaySizeResolver(context), this.resizeSizeResolver)
//        }.merged(ImageOptions {
//            resizeSizeResolver(ViewSizeResolver(TextView(context)))
//        }).apply {
//            Assert.assertEquals(DisplaySizeResolver(context), this.resizeSizeResolver)
//        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.resizePrecisionDecider)
        }.merged(ImageOptions {
            resizePrecision(EXACTLY)
        }).apply {
            Assert.assertEquals(FixedPrecisionDecider(EXACTLY), this.resizePrecisionDecider)
        }.merged(ImageOptions {
            resizePrecision(LESS_PIXELS)
        }).apply {
            Assert.assertEquals(FixedPrecisionDecider(EXACTLY), this.resizePrecisionDecider)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.resizeScaleDecider)
        }.merged(ImageOptions {
            resizeScale(END_CROP)
        }).apply {
            Assert.assertEquals(FixedScaleDecider(END_CROP), this.resizeScaleDecider)
        }.merged(ImageOptions {
            resizeScale(FILL)
        }).apply {
            Assert.assertEquals(FixedScaleDecider(END_CROP), this.resizeScaleDecider)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.transformations)
        }.merged(ImageOptions {
            transformations(CircleCropTransformation(), BlurTransformation())
        }).apply {
            Assert.assertEquals(
                listOf(CircleCropTransformation(), BlurTransformation()),
                this.transformations
            )
        }.merged(ImageOptions {
            transformations(CircleCropTransformation(), RoundedCornersTransformation())
        }).apply {
            Assert.assertEquals(
                listOf(
                    CircleCropTransformation(),
                    BlurTransformation(),
                    RoundedCornersTransformation()
                ),
                this.transformations
            )
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.disabledReuseBitmap)
        }.merged(ImageOptions {
            disabledReuseBitmap(true)
        }).apply {
            Assert.assertEquals(true, this.disabledReuseBitmap)
        }.merged(ImageOptions {
            disabledReuseBitmap(false)
        }).apply {
            Assert.assertEquals(true, this.disabledReuseBitmap)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.ignoreExifOrientation)
        }.merged(ImageOptions {
            ignoreExifOrientation(true)
        }).apply {
            Assert.assertEquals(true, this.ignoreExifOrientation)
        }.merged(ImageOptions {
            ignoreExifOrientation(false)
        }).apply {
            Assert.assertEquals(true, this.ignoreExifOrientation)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.resultCachePolicy)
        }.merged(ImageOptions {
            resultCachePolicy(DISABLED)
        }).apply {
            Assert.assertEquals(DISABLED, this.resultCachePolicy)
        }.merged(ImageOptions {
            resultCachePolicy(READ_ONLY)
        }).apply {
            Assert.assertEquals(DISABLED, this.resultCachePolicy)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.disabledAnimatedImage)
        }.merged(ImageOptions {
            disabledAnimatedImage(true)
        }).apply {
            Assert.assertEquals(true, this.disabledAnimatedImage)
        }.merged(ImageOptions {
            disabledAnimatedImage(false)
        }).apply {
            Assert.assertEquals(true, this.disabledAnimatedImage)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.placeholderImage)
        }.merged(ImageOptions {
            placeholder(android.R.drawable.bottom_bar)
        }).apply {
            Assert.assertEquals(
                DrawableStateImage(android.R.drawable.bottom_bar),
                this.placeholderImage
            )
        }.merged(ImageOptions {
            placeholder(android.R.drawable.arrow_up_float)
        }).apply {
            Assert.assertEquals(
                DrawableStateImage(android.R.drawable.bottom_bar),
                this.placeholderImage
            )
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.errorImage)
        }.merged(ImageOptions {
            error(android.R.drawable.bottom_bar)
        }).apply {
            Assert.assertEquals(
                newErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                this.errorImage
            )
        }.merged(ImageOptions {
            error(android.R.drawable.arrow_up_float)
        }).apply {
            Assert.assertEquals(
                newErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                this.errorImage
            )
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.transition)
        }.merged(ImageOptions {
            transition(CrossfadeTransition.Factory())
        }).apply {
            Assert.assertEquals(CrossfadeTransition.Factory(), this.transition)
        }.merged(ImageOptions {
            transition(TestTransition.Factory())
        }).apply {
            Assert.assertEquals(CrossfadeTransition.Factory(), this.transition)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.resizeApplyToDrawable)
        }.merged(ImageOptions {
            resizeApplyToDrawable(true)
        }).apply {
            Assert.assertEquals(true, this.resizeApplyToDrawable)
        }.merged(ImageOptions {
            resizeApplyToDrawable(false)
        }).apply {
            Assert.assertEquals(true, this.resizeApplyToDrawable)
        }

        ImageOptions().apply {
            Assert.assertEquals(null, this.memoryCachePolicy)
        }.merged(ImageOptions {
            memoryCachePolicy(DISABLED)
        }).apply {
            Assert.assertEquals(DISABLED, this.memoryCachePolicy)
        }.merged(ImageOptions {
            memoryCachePolicy(READ_ONLY)
        }).apply {
            Assert.assertEquals(DISABLED, this.memoryCachePolicy)
        }
    }

    @Test
    fun testEqualsHashCodeToString() {
        val optionsList = buildList {
            ImageOptions()
                .apply { add(this) }.newOptions {
                    depth(LOCAL)
                }.apply { add(this) }.newOptions {
                    setParameter("key", "value")
                }.apply { add(this) }.newOptions {
                    setHttpHeader("key1", "value1")
                }.apply { add(this) }.newOptions {
                    downloadCachePolicy(WRITE_ONLY)
                }.apply { add(this) }.newOptions {
                    bitmapConfig(RGB_565)
                }.apply { add(this) }.let { options ->
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        options.newOptions {
                            colorSpace(ColorSpace.get(ACES))
                        }.apply { add(this) }
                    } else {
                        options
                    }
                }.newOptions {
                    @Suppress("DEPRECATION")
                    preferQualityOverSpeed(true)
                }.apply { add(this) }.newOptions {
                    resizeSize(100, 100)
//                }.apply { add(this) }.newOptions {
//                    resizeSizeResolver(DisplaySizeResolver(context))
                }.apply { add(this) }.newOptions {
                    resizePrecision(SAME_ASPECT_RATIO)
                }.apply { add(this) }.newOptions {
                    resizeScale(FILL)
                }.apply { add(this) }.newOptions {
                    transformations(BlurTransformation())
                }.apply { add(this) }.newOptions {
                    disabledReuseBitmap(false)
                }.apply { add(this) }.newOptions {
                    ignoreExifOrientation(true)
                }.apply { add(this) }.newOptions {
                    resultCachePolicy(READ_ONLY)
                }.apply { add(this) }.newOptions {
                    disabledAnimatedImage(true)
                }.apply { add(this) }.newOptions {
                    placeholder(android.R.drawable.bottom_bar)
                }.apply { add(this) }.newOptions {
                    error(android.R.drawable.btn_dialog)
                }.apply { add(this) }.newOptions {
                    transition(CrossfadeTransition.Factory())
                }.apply { add(this) }.newOptions {
                    resizeApplyToDrawable(true)
                }.apply { add(this) }.newOptions {
                    memoryCachePolicy(ENABLED)
                }.apply { add(this) }
        }

        optionsList.forEachIndexed { index, imageOptions ->
            optionsList.forEachIndexed { index1, imageOptions1 ->
                if (index != index1) {
                    Assert.assertNotEquals(imageOptions, imageOptions1)
                    Assert.assertNotEquals(imageOptions.hashCode(), imageOptions1.hashCode())
                    Assert.assertNotEquals(imageOptions.toString(), imageOptions1.toString())
                }
            }
        }

        val optionsList2 = optionsList.map { it.newOptions() }
        optionsList.forEachIndexed { index, imageOptions ->
            Assert.assertEquals(imageOptions, optionsList2[index])
            Assert.assertEquals(imageOptions.hashCode(), optionsList2[index].hashCode())
            Assert.assertEquals(imageOptions.toString(), optionsList2[index].toString())
        }
    }

    @Test
    fun testDepth() {
        ImageOptions().apply {
            Assert.assertNull(depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        ImageOptions {
            depth(null)
        }.apply {
            Assert.assertNull(depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        ImageOptions {
            depth(LOCAL)
        }.apply {
            Assert.assertEquals(LOCAL, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        ImageOptions {
            depthFrom(null)
        }.apply {
            Assert.assertNull(depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        ImageOptions {
            depthFrom("testDepthFrom")
        }.apply {
            Assert.assertNull(depth)
            Assert.assertEquals("testDepthFrom", depthFrom)
            Assert.assertNotNull("testDepthFrom", parameters?.get(ImageRequest.REQUEST_DEPTH_FROM))
        }
    }

    @Test
    fun testParameters() {
        ImageOptions.Builder().apply {
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
        ImageOptions.Builder().apply {
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
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(downloadCachePolicy)
            }

            downloadCachePolicy(ENABLED)
            build().apply {
                Assert.assertEquals(ENABLED, downloadCachePolicy)
            }

            downloadCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, downloadCachePolicy)
            }

            downloadCachePolicy(null)
            build().apply {
                Assert.assertNull(downloadCachePolicy)
            }
        }
    }

    @Test
    fun testBitmapConfig() {
        ImageOptions.Builder().apply {
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

        ImageOptions.Builder().apply {
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
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(preferQualityOverSpeed)
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
                Assert.assertNull(preferQualityOverSpeed)
            }
        }
    }

    @Test
    fun testResize() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resizeSize)
                Assert.assertNull(resizePrecisionDecider)
                Assert.assertNull(resizeScaleDecider)
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
    }

    @Test
    fun testResizeSize() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resizeSize)
            }

            resizeSize(Size(100, 100))
            build().apply {
                Assert.assertEquals(Size(100, 100), resizeSize)
            }

            resizeSize(200, 200)
            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
            }

            build().apply {
                Assert.assertEquals(Size(200, 200), resizeSize)
            }

            resizeSize(null)
            build().apply {
                Assert.assertNull(resizeSize)
            }
        }
    }

//    @Test
//    fun testResizeSizeResolver() {
//        val context = getContext()
//
//        ImageOptions.Builder().apply {
//            build().apply {
//                Assert.assertNull(resizeSizeResolver)
//            }
//
//            resizeSizeResolver(DisplaySizeResolver(context))
//            build().apply {
//                Assert.assertEquals(DisplaySizeResolver(context), resizeSizeResolver)
//            }
//
//            resizeSizeResolver(null)
//            build().apply {
//                Assert.assertNull(resizeSizeResolver)
//            }
//        }
//    }

    @Test
    fun testResizePrecision() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resizePrecisionDecider)
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
                Assert.assertNull(resizePrecisionDecider)
            }
        }
    }

    @Test
    fun testResizeScale() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resizeScaleDecider)
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
                Assert.assertNull(resizeScaleDecider)
            }
        }
    }

    @Test
    fun testTransformations() {
        ImageOptions.Builder().apply {
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
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(disabledReuseBitmap)
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
                Assert.assertNull(disabledReuseBitmap)
            }
        }
    }

    @Test
    fun testIgnoreExifOrientation() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(ignoreExifOrientation)
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
                Assert.assertNull(ignoreExifOrientation)
            }
        }
    }

    @Test
    fun testResultCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resultCachePolicy)
            }

            resultCachePolicy(ENABLED)
            build().apply {
                Assert.assertEquals(ENABLED, resultCachePolicy)
            }

            resultCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, resultCachePolicy)
            }

            resultCachePolicy(null)
            build().apply {
                Assert.assertNull(resultCachePolicy)
            }
        }
    }

    @Test
    fun testDisabledAnimatedImage() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(disabledAnimatedImage)
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
                Assert.assertNull(disabledAnimatedImage)
            }
        }
    }

    @Test
    fun testPlaceholderImage() {
        ImageOptions.Builder().apply {
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
                    DrawableStateImage(android.R.drawable.bottom_bar),
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
        ImageOptions.Builder().apply {
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
                    ErrorStateImage.Builder(DrawableStateImage(android.R.drawable.bottom_bar))
                        .build(),
                    errorImage
                )
            }

            error(android.R.drawable.bottom_bar) {
                uriEmptyError(android.R.drawable.alert_dark_frame)
            }
            build().apply {
                Assert.assertEquals(
                    ErrorStateImage.Builder(DrawableStateImage(android.R.drawable.bottom_bar))
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
        ImageOptions.Builder().apply {
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
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(resizeApplyToDrawable)
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
                Assert.assertNull(resizeApplyToDrawable)
            }
        }
    }

    @Test
    fun testMemoryCachePolicy() {
        ImageOptions.Builder().apply {
            build().apply {
                Assert.assertNull(memoryCachePolicy)
            }

            memoryCachePolicy(ENABLED)
            build().apply {
                Assert.assertEquals(ENABLED, memoryCachePolicy)
            }

            memoryCachePolicy(DISABLED)
            build().apply {
                Assert.assertEquals(DISABLED, memoryCachePolicy)
            }

            memoryCachePolicy(null)
            build().apply {
                Assert.assertNull(memoryCachePolicy)
            }
        }
    }
}