package com.github.panpf.sketch.core.android.test.resize

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asDrawableOrThrow
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.resizeOnDraw
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.MediumImageViewTestActivity
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable1
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class ResizeOnDrawAndroidTest {

    @Test
    fun testResizeOnDraw() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val resources = context.resources
        val activity = MediumImageViewTestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        val imageUri = ResourceImages.jpeg.uri
        val bitmapDrawable =
            BitmapDrawable(resources, Bitmap.createBitmap(100, 200, Bitmap.Config.RGB_565))

        val request = ImageRequest(imageView, imageUri)
        val bitmapDrawableImage = bitmapDrawable.asSketchImage()
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
                .asDrawableOrThrow()
        )
        val request3 = ImageRequest(imageView, imageUri) {
            resizeOnDraw(true)
            size(500, 300)
            precision(Precision.EXACTLY)
        }
        bitmapDrawableImage
            .resizeOnDraw(request3, request3.toRequestContext(sketch).size)
            .asDrawableOrThrow()
            .let { it as ResizeDrawable }
            .apply {
                assertSame(bitmapDrawable, drawable)
                assertEquals(Size(500, 300), size)
            }

        val animDrawable = AnimatableDrawable(TestAnimatableDrawable1(bitmapDrawable))
        animDrawable.asSketchImage()
            .resizeOnDraw(request3, request3.toRequestContext(sketch).size)
            .asDrawableOrThrow()
            .let { it as ResizeAnimatableDrawable }
            .apply {
                assertSame(animDrawable, drawable)
                assertEquals(Size(500, 300), size)
            }
    }
}