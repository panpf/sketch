package com.github.panpf.sketch.view.core.test.request

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ViewLifecycleResolver
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.sizeWithView
import com.github.panpf.sketch.request.target
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
        val uri = ComposeResImageFiles.jpeg.uri
        val imageView1 = ImageView(context1)
        ImageRequest(imageView1, uri).apply {
            assertNotEquals(context1, this.context)
            assertEquals(
                "file:///compose_resource/composeResources/com.github.panpf.sketch.images/files/sample.jpeg",
                uri
            )
            assertNull(this.listener)
            assertNull(this.progressListener)
            assertEquals(ImageViewTarget(imageView1), this.target)
            assertEquals(ViewLifecycleResolver(imageView1), this.lifecycleResolver)

            assertEquals(NETWORK, this.depthHolder.depth)
            assertNull(this.extras)
            assertEquals(ENABLED, this.downloadCachePolicy)
            assertNull(this.colorType)
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
            assertNull(this.resizeOnDraw)
            assertEquals(ENABLED, this.memoryCachePolicy)
        }
    }

    @Test
    fun testTarget() {
        val context = getTestContext()
        val imageView = ImageView(context)
        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            target(imageView)
        }.apply {
            assertEquals(
                expected = ImageViewTarget(imageView),
                actual = this.target
            )
        }
    }

    @Test
    fun testSizeWithView() {
        val context = getTestContext()
        val imageView = ImageView(context)

        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            sizeWithView(imageView)
        }.apply {
            assertEquals(
                expected = ViewSizeResolver(imageView, subtractPadding = true),
                actual = this.sizeResolver
            )
        }

        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            sizeWithView(imageView, subtractPadding = false)
        }.apply {
            assertEquals(
                expected = ViewSizeResolver(imageView, subtractPadding = false),
                actual = this.sizeResolver
            )
        }
    }
}