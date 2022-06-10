package com.github.panpf.sketch.extensions.test

import android.graphics.Bitmap.Config.RGB_565
import android.view.LayoutInflater
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage.UriEmptyMatcher
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchImageViewTest {

    @Test
    fun testAttrs() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_default, null, false) as SketchImageView).apply {
            Assert.assertNull(displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                bitmapConfig(RGB_565)
                crossfade(3000, true)
                depth(LOCAL)
                disallowReuseBitmap()
                downloadCachePolicy(WRITE_ONLY)
                ignoreExifOrientation()
                memoryCachePolicy(DISABLED)
                preferQualityOverSpeed()
                resizeApplyToDrawable()
                resize(354, 2789, SAME_ASPECT_RATIO, FILL)
                resultCachePolicy(READ_ONLY)
                transformations(RoundedCornersTransformation(200f))
            }, displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_state, null, false) as SketchImageView).apply {
            Assert.assertNotNull(displayImageOptions!!.placeholder)
            Assert.assertNotNull(displayImageOptions!!.error)
            Assert.assertNotNull((displayImageOptions!!.error as ErrorStateImage).matcherList.find { it is UriEmptyMatcher })
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_rotate, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                transformations(RotateTransformation(444))
            }, displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_circle, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                transformations(CircleCropTransformation(END_CROP))
            }, displayImageOptions)
        }
    }
}