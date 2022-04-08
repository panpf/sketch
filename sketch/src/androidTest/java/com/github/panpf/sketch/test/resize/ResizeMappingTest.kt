package com.github.panpf.sketch.test.resize

import android.graphics.Rect
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.calculateResizeMapping
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeMappingTest {

    @Test
    fun testCalculatorResizeMappingLessPixels() {
        /* resize small */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 62, 15)),
            calculateResizeMapping(200, 50, 50, 20, START_CROP, LESS_PIXELS)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 62, 15)),
            calculateResizeMapping(200, 50, 20, 50, START_CROP, LESS_PIXELS)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 15, 62)),
            calculateResizeMapping(50, 200, 50, 20, START_CROP, LESS_PIXELS)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 15, 62)),
            calculateResizeMapping(50, 200, 20, 50, START_CROP, LESS_PIXELS)
        )

        /* resize big */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 100, 101, START_CROP, LESS_PIXELS)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 101, 100, START_CROP, LESS_PIXELS)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 100, 101, START_CROP, LESS_PIXELS)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 101, 100, START_CROP, LESS_PIXELS)
        )
    }

    @Test
    fun testCalculatorResizeMappingKeepAspectRatio() {
        TODO("Wait for the implementation")
    }

    @Test
    fun testCalculatorResizeMappingExactly() {
        TODO("Wait for the implementation")
    }

    @Test
    fun testCalculatorResizeMappingStartCrop() {
        TODO("Wait for the implementation")
    }

    @Test
    fun testCalculatorResizeMappingCenterCrop() {
        TODO("Wait for the implementation")
    }

    @Test
    fun testCalculatorResizeMappingEndCrop() {
        TODO("Wait for the implementation")
    }

    @Test
    fun testCalculatorResizeMappingFill() {
        TODO("Wait for the implementation")
    }

    /**
     * resizeWidth <= imageWidth && resizeHeight <= imageHeight
     */
    @Test
    fun testCalculatorResizeMappingResizeSmall() {
        // todo Refactoring， 宽高换为 200 50
        /**
         * resizeWidth > resizeHeight
         */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, START_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, CENTER_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, END_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, FILL, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, START_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, CENTER_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, END_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, FILL, EXACTLY)
        )

        /**
         * resizeWidth < resizeHeight
         */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, START_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, CENTER_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, END_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, FILL, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, START_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, CENTER_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, END_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, FILL, EXACTLY)
        )
    }

    /**
     * resizeWidth >= imageWidth && resizeHeight >= imageHeight
     */
    @Test
    fun testCalculatorResizeMappingResizeBig() {
        /**
         * resizeWidth > resizeHeight
         */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, START_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, CENTER_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, END_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, FILL, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, START_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, CENTER_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, END_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, FILL, EXACTLY)
        )

        /**
         * resizeWidth < resizeHeight
         */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, START_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, CENTER_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, END_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, FILL, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, START_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, CENTER_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, END_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, FILL, EXACTLY)
        )
    }

    /**
     * resizeWidth >= imageWidth && resizeHeight <= imageHeight
     */
    @Test
    fun testCalculatorResizeMappingResizeWidthBig() {
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 4), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, START_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 48, 100, 52), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, CENTER_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 96, 100, 100), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, END_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, FILL, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 4), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, START_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 48, 100, 52), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, CENTER_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 96, 100, 100), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, END_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, FILL, EXACTLY)
        )
    }

    /**
     * resizeWidth <= imageWidth && resizeHeight >= imageHeight
     */
    @Test
    fun testCalculatorResizeMappingResizeHeightBig() {
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 4, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, START_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(48, 0, 52, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, CENTER_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(96, 0, 100, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, END_CROP, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, FILL, KEEP_ASPECT_RATIO)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 4, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, START_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(48, 0, 52, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, CENTER_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(96, 0, 100, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, END_CROP, EXACTLY)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, FILL, EXACTLY)
        )
    }
}