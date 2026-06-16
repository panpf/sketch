package com.github.panpf.sketch.core.android.test.android

import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ResourcesTest {

    @Test
    fun testBitmapDrawableBitmap() {
        val context = getTestContext()

        val drawable1 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        val drawable2 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable

        assertNotSame(drawable1, drawable2)
        assertSame(drawable1.bitmap, drawable2.bitmap)

        drawable2.bitmap.recycle()
        assertTrue(drawable1.bitmap.isRecycled)
        val drawable3 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        assertTrue(drawable3.bitmap.isRecycled)
    }

    @Test
    fun testBitmapDrawableMutate() {
        val context = getTestContext()

        val drawable1 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        val drawable2 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable

        assertNotSame(drawable1, drawable2)
        assertSame(drawable1.paint, drawable2.paint)
        if (VERSION.SDK_INT >= 19) {
            assertEquals(255, drawable1.alpha)
        } else {
            assertEquals(0, drawable1.alpha)
        }
        if (VERSION.SDK_INT >= 19) {
            assertEquals(255, drawable2.alpha)
        } else {
            assertEquals(0, drawable2.alpha)
        }

        val drawable3 = drawable1.mutate() as BitmapDrawable
        assertSame(drawable1, drawable3)
        assertSame(drawable1.paint, drawable3.paint)
        if (VERSION.SDK_INT >= 19) {
            assertEquals(255, drawable3.alpha)
        } else {
            assertEquals(0, drawable3.alpha)
        }

        drawable3.alpha = 100
        if (VERSION.SDK_INT >= 19) {
            assertEquals(100, drawable1.alpha)
        } else {
            assertEquals(0, drawable1.alpha)
        }
        if (VERSION.SDK_INT >= 19) {
            assertEquals(255, drawable2.alpha)
        } else {
            assertEquals(0, drawable2.alpha)
        }
        if (VERSION.SDK_INT >= 19) {
            assertEquals(100, drawable3.alpha)
        } else {
            assertEquals(0, drawable3.alpha)
        }

        val drawable4 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        if (VERSION.SDK_INT >= 19) {
            assertEquals(255, drawable4.alpha)
        } else {
            assertEquals(0, drawable4.alpha)
        }
    }
}