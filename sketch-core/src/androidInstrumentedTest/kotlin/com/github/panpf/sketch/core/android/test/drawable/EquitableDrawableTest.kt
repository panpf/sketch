package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.drawable.ColorEquitableDrawable
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.drawable.getEquitableDrawable
import com.github.panpf.sketch.drawable.getEquitableDrawableCompat
import com.github.panpf.sketch.drawable.getEquitableDrawableCompatForDensity
import com.github.panpf.sketch.drawable.getEquitableDrawableForDensity
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.key
import com.github.panpf.sketch.util.toLogString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class EquitableDrawableTest {

    @Test
    fun testContextGetEquitableDrawable() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = context.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.getEquitableDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
        )
    }

    @Test
    fun testContextGetEquitableDrawableCompat() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy),
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
        )
    }

    @Test
    fun testResourcesGetEquitableDrawableCompat() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = ResourcesCompat.getDrawable(
                    /* res = */ context.resources,
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEquitableDrawableCompat(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                theme = null
            )
        )
    }

    @Test
    fun testResourcesGetEquitableDrawableCompatForDensity() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = ResourcesCompat.getDrawableForDensity(
                    /* res = */ context.resources,
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* density = */ 2,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEquitableDrawableCompatForDensity(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                density = 2,
                theme = null
            )
        )
    }

    @Test
    fun testResourcesGetEquitableDrawable() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = context.resources.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEquitableDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
        )
    }

    @Test
    fun testResourcesGetEquitableDrawableTheme() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = context.resources.getDrawable(
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEquitableDrawable(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                theme = null
            )
        )
    }

    @Test
    fun testResourcesGetEquitableDrawableForDensity() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = context.resources.getDrawableForDensity(
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* density = */ 2
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEquitableDrawableForDensity(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                density = 2,
            )
        )
    }

    @Test
    fun testResourcesGetEquitableDrawableForDensityTheme() {
        val context = getTestContext()
        assertEquals(
            expected = EquitableDrawable(
                drawable = context.resources.getDrawableForDensity(
                    /* id = */ com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                    /* density = */ 2,
                    /* theme = */ null
                )!!,
                equalityKey = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ),
            actual = context.resources.getEquitableDrawableForDensity(
                resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy,
                density = 2,
                theme = null
            )
        )
    }

    @Test
    fun testDrawableAsEquitable() {
        assertEquals(
            expected = EquitableDrawable(
                drawable = BitmapDrawable(null, AndroidBitmap(100, 100)),
                equalityKey = 100
            ),
            actual = BitmapDrawable(null, AndroidBitmap(100, 100)).asEquitable(100)
        )
    }

    @Test
    fun testColorDrawableAsEquitable() {
        assertEquals(
            expected = EquitableDrawable(
                drawable = ColorDrawable(TestColor.RED),
                equalityKey = 100
            ),
            actual = ColorDrawable(TestColor.RED).asEquitable(100)
        )
    }

    @Test
    fun testColorEquitableDrawable() {
        assertEquals(
            expected = EquitableDrawable(
                drawable = ColorDrawable(TestColor.RED),
                equalityKey = TestColor.RED
            ),
            actual = ColorEquitableDrawable(TestColor.RED)
        )
    }

    @Test
    fun testKey() {
        assertEquals(
            expected = ColorDrawable(TestColor.RED).key(TestColor.RED),
            actual = EquitableDrawable(
                drawable = ColorDrawable(TestColor.RED),
                equalityKey = TestColor.RED
            ).key
        )
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        EquitableDrawable(
            drawable = context.getDrawableCompat(android.R.drawable.ic_lock_lock),
            equalityKey = "key"
        ).apply {
            val mutateDrawable = mutate()
            assertSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_lock_lock).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        EquitableDrawable(
            drawable = TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.ic_lock_lock)),
            equalityKey = "key"
        ).apply {
            val mutateDrawable = mutate()
            assertNotSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_lock_lock).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = EquitableDrawable(
            drawable = ColorDrawable(TestColor.RED),
            equalityKey = TestColor.RED
        )
        val element11 = EquitableDrawable(
            drawable = ColorDrawable(TestColor.RED),
            equalityKey = TestColor.RED
        )
        val element2 = EquitableDrawable(
            drawable = ColorDrawable(TestColor.RED),
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
        val drawable = ColorDrawable(TestColor.RED)
        assertEquals(
            expected = "EquitableDrawable(drawable=${drawable.toLogString()}, equalityKey=${TestColor.RED})",
            actual = EquitableDrawable(
                drawable = drawable,
                equalityKey = TestColor.RED,
            ).toString()
        )
    }
}