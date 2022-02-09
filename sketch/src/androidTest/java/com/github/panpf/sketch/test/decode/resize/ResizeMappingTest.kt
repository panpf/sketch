package com.github.panpf.sketch.test.decode.resize

import android.graphics.Rect
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.resize.ResizeMapping
import com.github.panpf.sketch.decode.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.decode.resize.Scale.END_CROP
import com.github.panpf.sketch.decode.resize.Scale.FILL
import com.github.panpf.sketch.decode.resize.Scale.START_CROP
import com.github.panpf.sketch.decode.resize.calculateResizeMapping
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeMappingTest {

    /**
     * resizeWidth <= imageWidth && resizeHeight <= imageHeight
     */
    @Test
    fun testCalculatorResizeMappingResizeSmall() {
        /**
         * resizeWidth > resizeHeight
         */
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 0, 100, 40), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, START_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 30, 100, 70), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, CENTER_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 60, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, END_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 0, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, FILL, false)
        )
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 0, 100, 40), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, START_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 30, 100, 70), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, CENTER_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 60, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, END_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(50, 20, Rect(0, 0, 100, 100), Rect(0, 0, 50, 20)),
            calculateResizeMapping(100, 100, 50, 20, FILL, true)
        )

        /**
         * resizeWidth < resizeHeight
         */
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(0, 0, 40, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, START_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(30, 0, 70, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, CENTER_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(60, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, END_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(0, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, FILL, false)
        )
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(0, 0, 40, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, START_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(30, 0, 70, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, CENTER_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(60, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, END_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(20, 50, Rect(0, 0, 100, 100), Rect(0, 0, 20, 50)),
            calculateResizeMapping(100, 100, 20, 50, FILL, true)
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
            ResizeMapping(100, 40, Rect(0, 0, 100, 40), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, START_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(100, 40, Rect(0, 30, 100, 70), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, CENTER_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(100, 40, Rect(0, 60, 100, 100), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, END_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(100, 40, Rect(0, 0, 100, 100), Rect(0, 0, 100, 40)),
            calculateResizeMapping(100, 100, 500, 200, FILL, false)
        )
        Assert.assertEquals(
            ResizeMapping(500, 200, Rect(0, 0, 100, 40), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, START_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(500, 200, Rect(0, 30, 100, 70), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, CENTER_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(500, 200, Rect(0, 60, 100, 100), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, END_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(500, 200, Rect(0, 0, 100, 100), Rect(0, 0, 500, 200)),
            calculateResizeMapping(100, 100, 500, 200, FILL, true)
        )

        /**
         * resizeWidth < resizeHeight
         */
        Assert.assertEquals(
            ResizeMapping(40, 100, Rect(0, 0, 40, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, START_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(40, 100, Rect(30, 0, 70, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, CENTER_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(40, 100, Rect(60, 0, 100, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, END_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(40, 100, Rect(0, 0, 100, 100), Rect(0, 0, 40, 100)),
            calculateResizeMapping(100, 100, 200, 500, FILL, false)
        )
        Assert.assertEquals(
            ResizeMapping(200, 500, Rect(0, 0, 40, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, START_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(200, 500, Rect(30, 0, 70, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, CENTER_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(200, 500, Rect(60, 0, 100, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, END_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(200, 500, Rect(0, 0, 100, 100), Rect(0, 0, 200, 500)),
            calculateResizeMapping(100, 100, 200, 500, FILL, true)
        )
    }

    /**
     * resizeWidth >= imageWidth && resizeHeight <= imageHeight
     */
    @Test
    fun testCalculatorResizeMappingResizeWidthBig() {
        Assert.assertEquals(
            ResizeMapping(100, 4, Rect(0, 0, 100, 4), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, START_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(100, 4, Rect(0, 48, 100, 52), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, CENTER_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(100, 4, Rect(0, 96, 100, 100), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, END_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(100, 4, Rect(0, 0, 100, 100), Rect(0, 0, 100, 4)),
            calculateResizeMapping(100, 100, 500, 20, FILL, false)
        )
        Assert.assertEquals(
            ResizeMapping(500, 20, Rect(0, 0, 100, 4), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, START_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(500, 20, Rect(0, 48, 100, 52), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, CENTER_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(500, 20, Rect(0, 96, 100, 100), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, END_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(500, 20, Rect(0, 0, 100, 100), Rect(0, 0, 500, 20)),
            calculateResizeMapping(100, 100, 500, 20, FILL, true)
        )
    }

    /**
     * resizeWidth <= imageWidth && resizeHeight >= imageHeight
     */
    @Test
    fun testCalculatorResizeMappingResizeHeightBig() {
        Assert.assertEquals(
            ResizeMapping(4, 100, Rect(0, 0, 4, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, START_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(4, 100, Rect(48, 0, 52, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, CENTER_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(4, 100, Rect(96, 0, 100, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, END_CROP, false)
        )
        Assert.assertEquals(
            ResizeMapping(4, 100, Rect(0, 0, 100, 100), Rect(0, 0, 4, 100)),
            calculateResizeMapping(100, 100, 20, 500, FILL, false)
        )
        Assert.assertEquals(
            ResizeMapping(20, 500, Rect(0, 0, 4, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, START_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(20, 500, Rect(48, 0, 52, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, CENTER_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(20, 500, Rect(96, 0, 100, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, END_CROP, true)
        )
        Assert.assertEquals(
            ResizeMapping(20, 500, Rect(0, 0, 100, 100), Rect(0, 0, 20, 500)),
            calculateResizeMapping(100, 100, 20, 500, FILL, true)
        )
    }
}