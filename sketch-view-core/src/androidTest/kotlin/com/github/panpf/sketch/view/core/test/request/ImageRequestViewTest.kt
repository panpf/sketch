package com.github.panpf.sketch.view.core.test.request

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ViewLifecycleResolver
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class ImageRequestViewTest {

    @Test
    fun testImageRequest() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        val imageView1 = ImageView(context1)
        ImageRequest(imageView1, uri).apply {
            assertNotEquals(context1, this.context)
            assertEquals("file:///android_asset/sample.jpeg", uri)
            assertNull(this.listener)
            assertNull(this.progressListener)
            assertEquals(ImageViewTarget(imageView1), this.target)
            assertEquals(ViewLifecycleResolver(imageView1), this.lifecycleResolver)

            assertEquals(NETWORK, this.depthHolder.depth)
            assertNull(this.extras)
            assertNull(this.httpHeaders)
            assertEquals(ENABLED, this.downloadCachePolicy)
            assertNull(this.bitmapConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertNull(this.colorSpace)
            }
            @Suppress("DEPRECATION")
            assertFalse(this.preferQualityOverSpeed)
            assertEquals(ViewSizeResolver(imageView1), this.sizeResolver)
            assertEquals(FixedPrecisionDecider(LESS_PIXELS), this.precisionDecider)
            assertEquals(FixedScaleDecider(CENTER_CROP), this.scaleDecider)
            assertNull(this.transformations)
            assertEquals(ENABLED, this.resultCachePolicy)
            assertNull(this.placeholder)
            assertNull(this.fallback)
            assertNull(this.error)
            assertNull(this.transitionFactory)
            assertFalse(this.disallowAnimatedImage)
            assertNull(this.resizeOnDraw)
            assertEquals(ENABLED, this.memoryCachePolicy)
        }
    }

    @Test
    fun testTarget() {
        // TODO test
    }

    @Test
    fun testSizeWithView() {
        // TODO test
    }
}