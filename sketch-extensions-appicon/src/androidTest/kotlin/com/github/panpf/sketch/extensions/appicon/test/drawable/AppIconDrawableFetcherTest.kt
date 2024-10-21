package com.github.panpf.sketch.extensions.appicon.test.drawable

import com.github.panpf.sketch.drawable.AppIconDrawableFetcher
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.versionCodeCompat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class AppIconDrawableFetcherTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "AppIconDrawable('com.github.panpf.sketch.sample',1101)",
            actual = AppIconDrawableFetcher("com.github.panpf.sketch.sample", 1101).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        val versionCode =
            context.packageManager.getPackageInfo(context.packageName, 0).versionCodeCompat
        assertNotNull(AppIconDrawableFetcher(context.packageName, versionCode).getDrawable(context))
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = AppIconDrawableFetcher("com.github.panpf.sketch.sample", 1101)
        val element11 = AppIconDrawableFetcher("com.github.panpf.sketch.sample", 1101)
        val element2 = AppIconDrawableFetcher("com.github.panpf.sketch.sample2", 1101)
        val element3 = AppIconDrawableFetcher("com.github.panpf.sketch.sample", 1102)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "AppIconDrawableFetcher(packageName='com.github.panpf.sketch.sample', versionCode=1101)",
            actual = AppIconDrawableFetcher("com.github.panpf.sketch.sample", 1101).toString()
        )
    }
}