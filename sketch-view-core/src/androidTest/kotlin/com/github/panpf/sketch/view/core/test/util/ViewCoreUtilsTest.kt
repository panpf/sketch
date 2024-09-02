package com.github.panpf.sketch.view.core.test.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.findLeafChildDrawable
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewCoreUtilsTest {

    @Test
    fun testAnyAsOrNull() {
        // TODO test
    }

    @Test
    fun testAnyToHexString() {
        // TODO test
    }

    @Test
    fun testRequiredMainThread() {
        assertThrow(IllegalStateException::class) {
            requiredMainThread()
        }
        runBlocking(Dispatchers.Main) {
            requiredMainThread()
        }
    }

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

    @Test
    fun testFindLeafChildDrawable() {
        LayerDrawable(
            arrayOf(
                ColorDrawable(Color.BLUE),
                ColorDrawable(Color.RED),
                ColorDrawable(Color.GREEN)
            )
        ).findLeafChildDrawable().apply {
            Assert.assertEquals(Color.GREEN, this!!.asOrThrow<ColorDrawable>().color)
        }

        LayerDrawable(
            arrayOf(
                ColorDrawable(Color.RED),
                ColorDrawable(Color.GREEN),
                ColorDrawable(Color.BLUE),
            )
        ).findLeafChildDrawable().apply {
            Assert.assertEquals(Color.BLUE, this!!.asOrThrow<ColorDrawable>().color)
        }

        LayerDrawable(arrayOf()).findLeafChildDrawable().apply {
            Assert.assertEquals(null, this)
        }


        CrossfadeDrawable(
            ColorDrawable(Color.BLUE),
            ColorDrawable(Color.RED),
        ).findLeafChildDrawable().apply {
            Assert.assertEquals(Color.RED, this!!.asOrThrow<ColorDrawable>().color)
        }

        CrossfadeDrawable(
            ColorDrawable(Color.RED),
            ColorDrawable(Color.GREEN),
        ).findLeafChildDrawable().apply {
            Assert.assertEquals(Color.GREEN, this!!.asOrThrow<ColorDrawable>().color)
        }

        CrossfadeDrawable(null, null).findLeafChildDrawable().apply {
            Assert.assertEquals(null, this)
        }

        ColorDrawable(Color.GREEN).findLeafChildDrawable().apply {
            Assert.assertEquals(Color.GREEN, this!!.asOrThrow<ColorDrawable>().color)
        }

        ColorDrawable(Color.RED).findLeafChildDrawable().apply {
            Assert.assertEquals(Color.RED, this!!.asOrThrow<ColorDrawable>().color)
        }
    }

    @Test
    fun testCalculateBounds() {
        // TODO test
    }
}