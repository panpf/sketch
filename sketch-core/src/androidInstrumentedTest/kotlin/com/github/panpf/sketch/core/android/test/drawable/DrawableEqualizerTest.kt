package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.drawable.ColorDrawableEqualizer
import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.drawable.asEquality
import com.github.panpf.sketch.drawable.getEqualityDrawable
import com.github.panpf.sketch.drawable.getEqualityDrawableCompat
import com.github.panpf.sketch.drawable.getEqualityDrawableCompatForDensity
import com.github.panpf.sketch.drawable.getEqualityDrawableForDensity
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.getDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.key
import com.github.panpf.sketch.util.toLogString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DrawableEqualizerTest {

    @Test
    fun testContextGetEqualityDrawable() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = context.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.getEqualityDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
        )
    }

    @Test
    fun testContextGetEqualityDrawableCompat() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy),
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.getEqualityDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
        )
    }

    @Test
    fun testResourcesGetEqualityDrawableCompat() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = ResourcesCompat.getDrawable(
                    /* res = */ context.resources,
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEqualityDrawableCompat(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                theme = null
            )
        )
    }

    @Test
    fun testResourcesGetEqualityDrawableCompatForDensity() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = ResourcesCompat.getDrawableForDensity(
                    /* res = */ context.resources,
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* density = */ 2,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEqualityDrawableCompatForDensity(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                density = 2,
                theme = null
            )
        )
    }

    @Test
    fun testResourcesGetEqualityDrawable() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = context.resources.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEqualityDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
        )
    }

    @Test
    fun testResourcesGetEqualityDrawableTheme() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = context.resources.getDrawable(
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEqualityDrawable(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                theme = null
            )
        )
    }

    @Test
    fun testResourcesGetEqualityDrawableForDensity() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = context.resources.getDrawableForDensity(
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* density = */ 2
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEqualityDrawableForDensity(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                density = 2
            )
        )
    }

    @Test
    fun testResourcesGetEqualityDrawableForDensityTheme() {
        val context = getTestContext()
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = context.resources.getDrawableForDensity(
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* density = */ 2,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEqualityDrawableForDensity(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                density = 2,
                theme = null
            )
        )
    }

    @Test
    fun testDrawableAsEquality() {
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = BitmapDrawable(null, AndroidBitmap(100, 100)),
                equalityKey = 100
            ),
            actual = BitmapDrawable(null, AndroidBitmap(100, 100)).asEquality(100)
        )
    }

    @Test
    fun testColorDrawableAsEquality() {
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = ColorDrawable(TestColor.RED),
                equalityKey = 100
            ),
            actual = ColorDrawable(TestColor.RED).asEquality(100)
        )
    }

    @Test
    fun testColorDrawableEqualizer() {
        assertEquals(
            expected = DrawableEqualizer(
                wrapped = ColorDrawable(TestColor.RED),
                equalityKey = TestColor.RED
            ),
            actual = ColorDrawableEqualizer(TestColor.RED)
        )
    }

    @Test
    fun testKey() {
        assertEquals(
            expected = "DrawableEqualizer('${key(TestColor.RED)}')",
            actual = DrawableEqualizer(
                wrapped = ColorDrawable(TestColor.RED),
                equalityKey = TestColor.RED
            ).key
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = DrawableEqualizer(
            wrapped = ColorDrawable(TestColor.RED),
            equalityKey = TestColor.RED
        )
        val element11 = DrawableEqualizer(
            wrapped = ColorDrawable(TestColor.RED),
            equalityKey = TestColor.RED
        )
        val element2 = DrawableEqualizer(
            wrapped = ColorDrawable(TestColor.RED),
            equalityKey = TestColor.CYAN
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val wrapped = ColorDrawable(TestColor.RED)
        assertEquals(
            expected = "DrawableEqualizer(wrapped=${wrapped.toLogString()}, equalityKey=${TestColor.RED})",
            actual = DrawableEqualizer(
                wrapped = wrapped,
                equalityKey = TestColor.RED,
            ).toString()
        )
    }
}