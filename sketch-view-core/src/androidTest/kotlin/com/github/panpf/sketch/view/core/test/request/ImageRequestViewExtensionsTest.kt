package com.github.panpf.sketch.view.core.test.request

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ViewLifecycleResolver
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test

class ImageRequestViewExtensionsTest {

    @Test
    fun testFun() {
        val context1 = getTestContext()
        val uri = MyImages.jpeg.uri
        val imageView1 = ImageView(context1)
        ImageRequest(imageView1, uri).apply {
            Assert.assertSame(context1, this.context)
            Assert.assertEquals("asset://sample.jpeg", uri)
            Assert.assertNull(this.listener)
            Assert.assertNull(this.progressListener)
            Assert.assertEquals(ImageViewTarget(imageView1), this.target)
            Assert.assertEquals(ViewLifecycleResolver(imageView1), this.lifecycleResolver)

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
            Assert.assertEquals(ViewSizeResolver(imageView1), this.sizeResolver)
            Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), this.precisionDecider)
            Assert.assertEquals(FixedScaleDecider(CENTER_CROP), this.scaleDecider)
            Assert.assertNull(this.transformations)
            Assert.assertEquals(ENABLED, this.resultCachePolicy)
            Assert.assertNull(this.placeholder)
            Assert.assertNull(this.uriEmpty)
            Assert.assertNull(this.error)
            Assert.assertNull(this.transitionFactory)
            Assert.assertFalse(this.disallowAnimatedImage)
            Assert.assertNull(this.resizeOnDraw)
            Assert.assertEquals(ENABLED, this.memoryCachePolicy)
        }
    }

    // TODO target
}