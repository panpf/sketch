package com.github.panpf.sketch.test.transform

import android.graphics.Canvas
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Rect
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity.TRANSLUCENT
import com.github.panpf.sketch.transform.asPostProcessor
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimatedTransformationTest {

    @Test
    fun testAsPostProcessor() {
        val drawFilter1 = PaintFlagsDrawFilter(10, 8)
        val transformation = AnimatedTransformation {
            it.drawFilter = drawFilter1
            TRANSLUCENT
        }

        val canvas = Canvas()
        Assert.assertNull(canvas.drawFilter)

        val processor = transformation.asPostProcessor()
        processor.onPostProcess(canvas)
        Assert.assertEquals(drawFilter1, canvas.drawFilter)
    }
}