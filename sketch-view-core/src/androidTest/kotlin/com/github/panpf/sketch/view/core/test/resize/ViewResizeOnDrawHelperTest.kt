package com.github.panpf.sketch.view.core.test.resize

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.ViewResizeOnDrawHelper
import com.github.panpf.sketch.resize.resizeOnDraw
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.MediumImageViewTestActivity
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.view.core.test.target.TestViewTarget
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class ViewResizeOnDrawHelperTest {

    @Test
    fun test() {
        assertSame(
            expected = ViewResizeOnDrawHelper,
            actual = ViewResizeOnDrawHelper
        )
    }

    @Test
    fun testKey() {
        assertEquals(
            expected = "ViewResizeOnDrawHelper",
            actual = ViewResizeOnDrawHelper.key
        )
    }

    @Test
    fun testResize() {
        val context = getTestContext()
        val helper = ViewResizeOnDrawHelper

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
            },
            size = Size(200, 200),
            image = SizeColorDrawable(Color.RED, Size(300, 500)).asImage()
        ).apply {
            assertEquals(
                expected = SizeColorDrawable(Color.RED, Size(300, 500)).asImage(),
                actual = this
            )
        }

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                target(TestViewTarget())
            },
            size = Size(200, 200),
            image = SizeColorDrawable(Color.RED, Size(300, 500)).asImage()
        ).apply {
            assertEquals(
                expected = ResizeDrawable(
                    drawable = SizeColorDrawable(Color.RED, Size(300, 500)),
                    size = Size(200, 200),
                    scaleType = ImageView.ScaleType.FIT_CENTER
                ).asImage(),
                actual = this
            )
        }

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                target(TestViewTarget(scaleType = ImageView.ScaleType.FIT_XY))
            },
            size = Size(200, 200),
            image = SizeColorDrawable(Color.RED, Size(300, 1500)).asImage()
        ).apply {
            assertEquals(
                expected = ResizeDrawable(
                    drawable = SizeColorDrawable(Color.RED, Size(300, 1500)),
                    size = Size(200, 200),
                    scaleType = ImageView.ScaleType.FIT_XY
                ).asImage(),
                actual = this
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ViewResizeOnDrawHelper
        val element11 = ViewResizeOnDrawHelper

        assertSame(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ViewResizeOnDrawHelper",
            actual = ViewResizeOnDrawHelper.toString()
        )
    }

    @Test
    fun testResizeOnDraw() = runTest {
        val (_, sketch) = getTestContextAndSketch()
        MediumImageViewTestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val resources = activity.resources
            val imageView = activity.imageView

            val imageUri = ResourceImages.jpeg.uri
            val bitmapDrawable =
                BitmapDrawable(resources, Bitmap.createBitmap(100, 200, Bitmap.Config.RGB_565))

            val request = ImageRequest(imageView, imageUri)
            val bitmapDrawableImage = bitmapDrawable.asImage()
            assertSame(
                bitmapDrawableImage,
                bitmapDrawableImage.resizeOnDraw(request, null)
            )
            val request1 = ImageRequest(imageView, imageUri) {
                resizeOnDraw(true)
            }
            assertSame(
                bitmapDrawableImage,
                bitmapDrawableImage.resizeOnDraw(request1, null)
            )
            val request2 = ImageRequest(imageView, imageUri) {
                size(500, 300)
                precision(Precision.EXACTLY)
            }
            assertSame(
                bitmapDrawable,
                bitmapDrawableImage
                    .resizeOnDraw(request2, request2.toRequestContext(sketch).size)
                    .asDrawable()
            )
            val request3 = ImageRequest(imageView, imageUri) {
                resizeOnDraw(true)
                size(500, 300)
                precision(Precision.EXACTLY)
            }
            bitmapDrawableImage
                .resizeOnDraw(request3, request3.toRequestContext(sketch).size)
                .asDrawable()
                .let { it as ResizeDrawable }
                .apply {
                    assertSame(bitmapDrawable, drawable)
                    assertEquals(Size(500, 300), size)
                }

            val animDrawable = AnimatableDrawable(TestAnimatableDrawable(bitmapDrawable))
            animDrawable.asImage()
                .resizeOnDraw(request3, request3.toRequestContext(sketch).size)
                .asDrawable()
                .let { it as ResizeAnimatableDrawable }
                .apply {
                    assertSame(animDrawable, drawable)
                    assertEquals(Size(500, 300), size)
                }
        }
    }
}