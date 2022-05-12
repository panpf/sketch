package com.github.panpf.sketch.test.request

import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.BT709
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsBuilder
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.RequestDepth.LOCAL
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.resize.DisplaySizeResolver
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.test.context
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
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

        ImageOptionsBuilder().apply {
            Assert.assertNotNull(this)
            Assert.assertTrue(this.build().isEmpty())
        }
        ImageOptionsBuilder {
            depth(LOCAL)
        }.apply {
            Assert.assertNotNull(this)
            Assert.assertFalse(this.build().isEmpty())
        }
    }

    @Test
    fun testIsEmpty() {
        val context = context()

        ImageOptions().apply {
            Assert.assertTrue(this.isEmpty())
            Assert.assertNull(this.depth)
            Assert.assertNull(this.parameters)
            Assert.assertNull(this.httpHeaders)
            Assert.assertNull(this.downloadDiskCachePolicy)
            Assert.assertNull(this.bitmapConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertNull(this.colorSpace)
            }
            @Suppress("DEPRECATION")
            Assert.assertNull(this.preferQualityOverSpeed)
            Assert.assertNull(this.resizeSize)
            Assert.assertNull(this.resizeSizeResolver)
            Assert.assertNull(this.resizePrecisionDecider)
            Assert.assertNull(this.resizeScaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertNull(this.disabledReuseBitmap)
            Assert.assertNull(this.ignoreExifOrientation)
            Assert.assertNull(this.bitmapResultDiskCachePolicy)
            Assert.assertNull(this.disabledAnimatedImage)
            Assert.assertNull(this.placeholderImage)
            Assert.assertNull(this.errorImage)
            Assert.assertNull(this.transition)
            Assert.assertNull(this.resizeApplyToDrawable)
            Assert.assertNull(this.bitmapMemoryCachePolicy)
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
            downloadDiskCachePolicy(READ_ONLY)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.downloadDiskCachePolicy)
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

        ImageOptions {
            resizeSize(DisplaySizeResolver(context))
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.resizeSizeResolver)
        }

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
            bitmapResultDiskCachePolicy(ENABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.bitmapResultDiskCachePolicy)
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
            bitmapMemoryCachePolicy(ENABLED)
        }.apply {
            Assert.assertFalse(this.isEmpty())
            Assert.assertNotNull(this.bitmapMemoryCachePolicy)
        }
    }


    @Test
    fun testNewBuilder() {
        // todo Write test cases

    }

    @Test
    fun testNewOptions() {
        // todo Write test cases

    }

    @Test
    fun testMerge() {
        // todo Write test cases

    }

    @Test
    fun testEquals() {
        // todo Write test cases

    }

    @Test
    fun testHashCode() {
        // todo Write test cases

    }

    @Test
    fun testToString() {
        // todo Write test cases

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
            depth(NETWORK)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
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

        ImageOptions {
            depthFrom("testDepthFrom2")
        }.apply {
            Assert.assertNull(depth)
            Assert.assertEquals("testDepthFrom2", depthFrom)
            Assert.assertNotNull("testDepthFrom2", parameters?.get(ImageRequest.REQUEST_DEPTH_FROM))
        }
    }

    @Test
    fun testParameters() {
        ImageOptions().apply {
            Assert.assertNull(parameters)
        }

        ImageOptions {
            parameters(Parameters())
        }.apply {
            Assert.assertNull(parameters)
        }

        ImageOptions {
            parameters(Parameters.Builder().set("key1", "value1").build())
        }.apply {
            Assert.assertEquals(1, parameters?.size)
            Assert.assertEquals("value1", parameters?.get("key1"))
        }

        ImageOptions {
            parameters(Parameters.Builder().set("key1", "value1").set("key2", "value2").build())
        }.apply {
            Assert.assertEquals(2, parameters?.size)
            Assert.assertEquals("value1", parameters?.get("key1"))
            Assert.assertEquals("value2", parameters?.get("key2"))
        }

        ImageOptions {
            setParameter("key3", "value3")
        }.apply {
            Assert.assertEquals(1, parameters?.size)
            Assert.assertEquals("value3", parameters?.get("key3"))
        }

        ImageOptions {
            setParameter("key3", "value3")
            setParameter("key3", "value3.1")
        }.apply {
            Assert.assertEquals(1, parameters?.size)
            Assert.assertEquals("value3.1", parameters?.get("key3"))
        }

        ImageOptions {
            setParameter("key3", "value3")
            setParameter("key3", "value3.1")
            setParameter("key4", "value4")
        }.apply {
            Assert.assertEquals(2, parameters?.size)
            Assert.assertEquals("value3.1", parameters?.get("key3"))
            Assert.assertEquals("value4", parameters?.get("key4"))
        }

        ImageOptions {
            setParameter("key3", "value3")
            setParameter("key3", "value3.1")
            setParameter("key4", "value4")
            removeParameter("key3")
        }.apply {
            Assert.assertEquals(1, parameters?.size)
            Assert.assertNull(parameters?.get("key3"))
            Assert.assertEquals("value4", parameters?.get("key4"))
        }

        ImageOptions {
            setParameter("key3", "value3")
            setParameter("key3", "value3.1")
            setParameter("key4", "value4")
            removeParameter("key3")
            removeParameter("key4")
        }.apply {
            Assert.assertNull(parameters)
        }
    }

    @Test
    fun testHttpHeaders() {
        // todo Write test cases

    }

    @Test
    fun testDownloadDiskCachePolicy() {
        // todo Write test cases

    }

    @Test
    fun testBitmapConfig() {
        // todo Write test cases

    }

    @Test
    fun testColorSpace() {
        // todo Write test cases

    }

    @Test
    fun testPreferQualityOverSpeed() {
        // todo Write test cases

    }

    @Test
    fun testResize() {
        // todo Write test cases

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
    fun testBitmapResultDiskCachePolicy() {
        // todo Write test cases

    }

    @Test
    fun testDisabledAnimatedImage() {
        // todo Write test cases

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
    fun testResizeApplyToDrawable() {
        // todo Write test cases

    }

    @Test
    fun testBitmapMemoryCachePolicy() {
        // todo Write test cases

    }
}