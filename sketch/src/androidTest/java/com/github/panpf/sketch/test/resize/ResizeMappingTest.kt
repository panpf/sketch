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
        /* resize < imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 20, 40, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 14, 56)),
            calculateResizeMapping(50, 200, 40, 20, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 14, 56)),
            calculateResizeMapping(50, 200, 20, 40, LESS_PIXELS, START_CROP)
        )

        /* resize > imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 100, 150, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 150, 100, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 100, 150, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 150, 100, LESS_PIXELS, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingKeepAspectRatio() {
        /* resize < imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 125, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, KEEP_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 20, 40), Rect(0, 0, 20, 40)),
            calculateResizeMapping(200, 50, 20, 40, KEEP_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 40, 20), Rect(0, 0, 40, 20)),
            calculateResizeMapping(50, 200, 40, 20, KEEP_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 125), Rect(0, 0, 20, 40)),
            calculateResizeMapping(50, 200, 20, 40, KEEP_ASPECT_RATIO, START_CROP)
        )

        /* resize > imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 33, 50), Rect(0, 0, 33, 50)),
            calculateResizeMapping(200, 50, 100, 150, KEEP_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 75, 50), Rect(0, 0, 75, 50)),
            calculateResizeMapping(200, 50, 150, 100, KEEP_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 75), Rect(0, 0, 50, 75)),
            calculateResizeMapping(50, 200, 100, 150, KEEP_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 33), Rect(0, 0, 50, 33)),
            calculateResizeMapping(50, 200, 150, 100, KEEP_ASPECT_RATIO, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingExactly() {
        /* resize < imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 125, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 20, 40), Rect(0, 0, 20, 40)),
            calculateResizeMapping(200, 50, 20, 40, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 40, 20), Rect(0, 0, 40, 20)),
            calculateResizeMapping(50, 200, 40, 20, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 125), Rect(0, 0, 20, 40)),
            calculateResizeMapping(50, 200, 20, 40, EXACTLY, START_CROP)
        )

        /* resize > imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 33, 50), Rect(0, 0, 100, 150)),
            calculateResizeMapping(200, 50, 100, 150, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 75, 50), Rect(0, 0, 150, 100)),
            calculateResizeMapping(200, 50, 150, 100, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 75), Rect(0, 0, 100, 150)),
            calculateResizeMapping(50, 200, 100, 150, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 33), Rect(0, 0, 150, 100)),
            calculateResizeMapping(50, 200, 150, 100, EXACTLY, START_CROP)
        )
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

//    /**
//     * resizeWidth <= imageWidth && resizeHeight <= imageHeight
//     */
//    @Test
//    fun testCalculatorResizeMappingResizeSmall() {
//        // todo Refactoring， 宽高换为 200 50
//        /**
//         * resizeWidth > resizeHeight
//         */
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, KEEP_ASPECT_RATIO, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, CENTER_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, END_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, FILL, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, EXACTLY, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, CENTER_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, END_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 50, 20)),
//            calculateResizeMapping(100, 100, 50, 20, FILL, EXACTLY)
//        )
//
//        /**
//         * resizeWidth < resizeHeight
//         */
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, KEEP_ASPECT_RATIO, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, CENTER_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, END_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, FILL, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, EXACTLY, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, CENTER_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, END_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 20, 50)),
//            calculateResizeMapping(100, 100, 20, 50, FILL, EXACTLY)
//        )
//    }
//
//    /**
//     * resizeWidth >= imageWidth && resizeHeight >= imageHeight
//     */
//    @Test
//    fun testCalculatorResizeMappingResizeBig() {
//        /**
//         * resizeWidth > resizeHeight
//         */
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 100, 40)),
//            calculateResizeMapping(100, 100, 500, 200, KEEP_ASPECT_RATIO, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 100, 40)),
//            calculateResizeMapping(100, 100, 500, 200, CENTER_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 100, 40)),
//            calculateResizeMapping(100, 100, 500, 200, END_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 100, 40)),
//            calculateResizeMapping(100, 100, 500, 200, FILL, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 40), Rect(0, 0, 500, 200)),
//            calculateResizeMapping(100, 100, 500, 200, EXACTLY, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 30, 100, 70), Rect(0, 0, 500, 200)),
//            calculateResizeMapping(100, 100, 500, 200, CENTER_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 60, 100, 100), Rect(0, 0, 500, 200)),
//            calculateResizeMapping(100, 100, 500, 200, END_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 500, 200)),
//            calculateResizeMapping(100, 100, 500, 200, FILL, EXACTLY)
//        )
//
//        /**
//         * resizeWidth < resizeHeight
//         */
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 40, 100)),
//            calculateResizeMapping(100, 100, 200, 500, KEEP_ASPECT_RATIO, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 40, 100)),
//            calculateResizeMapping(100, 100, 200, 500, CENTER_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 40, 100)),
//            calculateResizeMapping(100, 100, 200, 500, END_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 40, 100)),
//            calculateResizeMapping(100, 100, 200, 500, FILL, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 40, 100), Rect(0, 0, 200, 500)),
//            calculateResizeMapping(100, 100, 200, 500, EXACTLY, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(30, 0, 70, 100), Rect(0, 0, 200, 500)),
//            calculateResizeMapping(100, 100, 200, 500, CENTER_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(60, 0, 100, 100), Rect(0, 0, 200, 500)),
//            calculateResizeMapping(100, 100, 200, 500, END_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 200, 500)),
//            calculateResizeMapping(100, 100, 200, 500, FILL, EXACTLY)
//        )
//    }
//
//    /**
//     * resizeWidth >= imageWidth && resizeHeight <= imageHeight
//     */
//    @Test
//    fun testCalculatorResizeMappingResizeWidthBig() {
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 4), Rect(0, 0, 100, 4)),
//            calculateResizeMapping(100, 100, 500, 20, KEEP_ASPECT_RATIO, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 48, 100, 52), Rect(0, 0, 100, 4)),
//            calculateResizeMapping(100, 100, 500, 20, CENTER_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 96, 100, 100), Rect(0, 0, 100, 4)),
//            calculateResizeMapping(100, 100, 500, 20, END_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 100, 4)),
//            calculateResizeMapping(100, 100, 500, 20, FILL, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 4), Rect(0, 0, 500, 20)),
//            calculateResizeMapping(100, 100, 500, 20, EXACTLY, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 48, 100, 52), Rect(0, 0, 500, 20)),
//            calculateResizeMapping(100, 100, 500, 20, CENTER_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 96, 100, 100), Rect(0, 0, 500, 20)),
//            calculateResizeMapping(100, 100, 500, 20, END_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 500, 20)),
//            calculateResizeMapping(100, 100, 500, 20, FILL, EXACTLY)
//        )
//    }
//
//    /**
//     * resizeWidth <= imageWidth && resizeHeight >= imageHeight
//     */
//    @Test
//    fun testCalculatorResizeMappingResizeHeightBig() {
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 4, 100), Rect(0, 0, 4, 100)),
//            calculateResizeMapping(100, 100, 20, 500, KEEP_ASPECT_RATIO, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(48, 0, 52, 100), Rect(0, 0, 4, 100)),
//            calculateResizeMapping(100, 100, 20, 500, CENTER_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(96, 0, 100, 100), Rect(0, 0, 4, 100)),
//            calculateResizeMapping(100, 100, 20, 500, END_CROP, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 4, 100)),
//            calculateResizeMapping(100, 100, 20, 500, FILL, KEEP_ASPECT_RATIO)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 4, 100), Rect(0, 0, 20, 500)),
//            calculateResizeMapping(100, 100, 20, 500, EXACTLY, START_CROP)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(48, 0, 52, 100), Rect(0, 0, 20, 500)),
//            calculateResizeMapping(100, 100, 20, 500, CENTER_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(96, 0, 100, 100), Rect(0, 0, 20, 500)),
//            calculateResizeMapping(100, 100, 20, 500, END_CROP, EXACTLY)
//        )
//        Assert.assertEquals(
//            ResizeMapping(Rect(0, 0, 100, 100), Rect(0, 0, 20, 500)),
//            calculateResizeMapping(100, 100, 20, 500, FILL, EXACTLY)
//        )
//    }
}