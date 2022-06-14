package com.github.panpf.sketch.test.drawable

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.ResizeAnimatableDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeAnimatableDrawableTest {

    @Test
    fun test() {
        val context = getTestContext()
        val sketch = context.sketch

        val imageUri = newAssetUri("sample.jpeg")
        val animDrawable = SketchAnimatableDrawable(
            imageUri = imageUri,
            requestKey = imageUri,
            requestCacheKey = imageUri,
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            imageExifOrientation = 0,
            dataFrom = LOCAL,
            transformedList = null,
            animatableDrawable = ColorDrawable(Color.GREEN),
            animatableDrawableName = "TestDrawable",
        )
        ResizeAnimatableDrawable(sketch, animDrawable, Resize(100, 500)).apply {
            assertThrow(IllegalArgumentException::class) {
                start()
            }
            assertThrow(IllegalArgumentException::class) {
                stop()
            }
            assertThrow(IllegalArgumentException::class) {
                isRunning
            }

            val callback = object : AnimationCallback() {

            }
            Assert.assertFalse(unregisterAnimationCallback(callback))
            registerAnimationCallback(callback)
            Assert.assertTrue(unregisterAnimationCallback(callback))
            clearAnimationCallbacks()
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val sketch = context.sketch

        val imageUri = newAssetUri("sample.jpeg")
        val animDrawable = SketchAnimatableDrawable(
            imageUri = imageUri,
            requestKey = imageUri,
            requestCacheKey = imageUri,
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            imageExifOrientation = 0,
            dataFrom = LOCAL,
            transformedList = null,
            animatableDrawable = ColorDrawable(Color.GREEN),
            animatableDrawableName = "TestDrawable",
        )
        ResizeAnimatableDrawable(sketch, animDrawable, Resize(100, 500)).apply {
            Assert.assertEquals("ResizeAnimatableDrawable($animDrawable)", toString())
        }
    }
}