package com.github.panpf.sketch.test.android

import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourcesTest {

    @Test
    fun test() {
        val context = getTestContext()

        val drawable1 =
            context.resources.getDrawable(android.R.drawable.ic_delete) as BitmapDrawable
        val drawable2 =
            context.resources.getDrawable(android.R.drawable.ic_delete) as BitmapDrawable

        Assert.assertNotSame(drawable1, drawable2)
        Assert.assertSame(drawable1.bitmap, drawable2.bitmap)

        drawable2.bitmap.recycle()
        Assert.assertTrue(drawable1.bitmap.isRecycled)
        val drawable3 =
            context.resources.getDrawable(android.R.drawable.ic_delete) as BitmapDrawable
        Assert.assertTrue(drawable3.bitmap.isRecycled)
    }
}