package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asDrawableOrThrow
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.resizeOnDraw
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable1
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeOnDrawTest {

    @Test
    fun testResizeOnDraw() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val resources = context.resources

        val imageUri = MyImages.jpeg.uri
        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))

        val request = ImageRequest(context, imageUri)
        val bitmapDrawableImage = bitmapDrawable.asSketchImage()
        Assert.assertSame(
            bitmapDrawableImage,
            bitmapDrawableImage.resizeOnDraw(request, null)
        )
        val request1 = ImageRequest(context, imageUri) {
            resizeOnDraw(true)
        }
        Assert.assertSame(
            bitmapDrawableImage,
            bitmapDrawableImage.resizeOnDraw(request1, null)
        )
        val request2 = ImageRequest(context, imageUri) {
            size(500, 300)
            precision(EXACTLY)
        }
        Assert.assertSame(
            bitmapDrawable,
            bitmapDrawableImage
                .resizeOnDraw(request2, request2.toRequestContext(sketch).size)
                .asDrawableOrThrow()
        )
        val request3 = ImageRequest(context, imageUri) {
            resizeOnDraw(true)
            size(500, 300)
            precision(EXACTLY)
        }
        bitmapDrawableImage
            .resizeOnDraw(request3, request3.toRequestContext(sketch).size)
            .asDrawableOrThrow()
            .let { it as ResizeDrawable }
            .apply {
                Assert.assertSame(bitmapDrawable, drawable)
                Assert.assertEquals(Size(500, 300), size)
            }

        val animDrawable = AnimatableDrawable(TestAnimatableDrawable1(bitmapDrawable))
        animDrawable.asSketchImage()
            .resizeOnDraw(request3, request3.toRequestContext(sketch).size)
            .asDrawableOrThrow()
            .let { it as ResizeAnimatableDrawable }
            .apply {
                Assert.assertSame(animDrawable, drawable)
                Assert.assertEquals(Size(500, 300), size)
            }
    }
}