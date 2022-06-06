package com.github.panpf.sketch.test.stateimage

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.drawable.internal.IconDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorFetcher
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.util.asOrNull
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconStateImageTest {

    @Test
    fun testGetDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val iconDrawable = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        val greenBgDrawable = ColorDrawable(Color.GREEN)

        IconStateImage(iconDrawable, greenBgDrawable).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertEquals(greenBgDrawable, bg)
            }
        }
        IconStateImage(iconDrawable, null as Drawable?).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertNull(bg)
            }
        }

        IconStateImage(iconDrawable, android.R.drawable.bottom_bar).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertTrue(bg is BitmapDrawable)
            }
        }
        IconStateImage(iconDrawable, null as Int?).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertNull(bg)
            }
        }

        IconStateImage(iconDrawable, IntColor(Color.BLUE)).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertEquals(Color.BLUE, (bg as ColorDrawable).color)
            }
        }
        IconStateImage(iconDrawable, null as ColorFetcher?).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertNull(bg)
            }
        }

        IconStateImage(iconDrawable).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertNull(bg)
            }
        }


        IconStateImage(android.R.drawable.ic_delete, greenBgDrawable).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertEquals(greenBgDrawable, bg)
            }
        }
        IconStateImage(android.R.drawable.ic_delete, null as Drawable?).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertNull(bg)
            }
        }

        IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertTrue(bg is BitmapDrawable)
            }
        }
        IconStateImage(android.R.drawable.ic_delete, null as Int?).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertNull(bg)
            }
        }

        IconStateImage(android.R.drawable.ic_delete, IntColor(Color.BLUE)).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertEquals(Color.BLUE, (bg as ColorDrawable).color)
            }
        }
        IconStateImage(android.R.drawable.ic_delete, null as ColorFetcher?).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertNull(bg)
            }
        }

        IconStateImage(android.R.drawable.ic_delete).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertNull(bg)
            }
        }
    }

    @Test
    fun testEquals() {
        val stateImage1 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar)
        val stateImage11 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar)

        val stateImage2 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_default)
        val stateImage21 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_default)

        val stateImage3 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_star)
        val stateImage31 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_star)

        Assert.assertNotSame(stateImage1, stateImage11)
        Assert.assertNotSame(stateImage2, stateImage21)
        Assert.assertNotSame(stateImage3, stateImage31)

        Assert.assertEquals(stateImage1, stateImage11)
        Assert.assertEquals(stateImage2, stateImage21)
        Assert.assertEquals(stateImage3, stateImage31)

        Assert.assertNotEquals(stateImage1, stateImage2)
        Assert.assertNotEquals(stateImage1, stateImage3)
        Assert.assertNotEquals(stateImage2, stateImage3)
    }

    @Test
    fun testHashCode() {
        val stateImage1 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar)
        val stateImage11 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar)

        val stateImage2 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_default)
        val stateImage21 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_default)

        val stateImage3 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_star)
        val stateImage31 =
            IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_star)

        Assert.assertEquals(stateImage1.hashCode(), stateImage11.hashCode())
        Assert.assertEquals(stateImage2.hashCode(), stateImage21.hashCode())
        Assert.assertEquals(stateImage3.hashCode(), stateImage31.hashCode())

        Assert.assertNotEquals(stateImage1.hashCode(), stateImage2.hashCode())
        Assert.assertNotEquals(stateImage1.hashCode(), stateImage3.hashCode())
        Assert.assertNotEquals(stateImage2.hashCode(), stateImage3.hashCode())
    }

    @Test
    fun testToString() {
        IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar).apply {
            Assert.assertEquals(
                "IconStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), bg=ResDrawable(${android.R.drawable.bottom_bar}))",
                toString()
            )
        }
        IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_default).apply {
            Assert.assertEquals(
                "IconStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), bg=ResDrawable(${android.R.drawable.btn_default}))",
                toString()
            )
        }
        IconStateImage(android.R.drawable.ic_delete).apply {
            Assert.assertEquals(
                "IconStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), bg=null)",
                toString()
            )
        }
    }
}