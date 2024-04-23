package com.github.panpf.sketch.view.core.test.util

import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.fitScale
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewCoreUtilsTest {

    @Test
    fun testFitScale() {
        val context = getTestContext()

        Assert.assertTrue(ImageView(context).apply {
            scaleType = ScaleType.FIT_START
        }.scaleType.fitScale)

        Assert.assertTrue(ImageView(context).apply {
            scaleType = ScaleType.FIT_CENTER
        }.scaleType.fitScale)

        Assert.assertTrue(ImageView(context).apply {
            scaleType = ScaleType.FIT_END
        }.scaleType.fitScale)

        Assert.assertFalse(ImageView(context).apply {
            scaleType = ScaleType.FIT_XY
        }.scaleType.fitScale)

        Assert.assertFalse(ImageView(context).apply {
            scaleType = ScaleType.CENTER_CROP
        }.scaleType.fitScale)

        Assert.assertFalse(ImageView(context).apply {
            scaleType = ScaleType.CENTER
        }.scaleType.fitScale)

        Assert.assertTrue(ImageView(context).apply {
            scaleType = ScaleType.CENTER_INSIDE
        }.scaleType.fitScale)

        Assert.assertFalse(ImageView(context).apply {
            scaleType = ScaleType.MATRIX
        }.scaleType.fitScale)
    }
}