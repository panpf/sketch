package com.github.panpf.sketch.extensions.core.android.test.drawable

import com.github.panpf.sketch.drawable.ApkIconDrawableFetcher
import com.github.panpf.sketch.test.utils.getTestContext
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class ApkIconDrawableFetcherTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "ApkIconDrawableFetcher('/sdcard/sample.apk')",
            actual = ApkIconDrawableFetcher(File("/sdcard/sample.apk")).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        val apkFile = File(context.applicationInfo.publicSourceDir)
        assertNotNull(ApkIconDrawableFetcher(apkFile).getDrawable(context))
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ApkIconDrawableFetcher(File("/sdcard/sample.apk"))
        val element11 = ApkIconDrawableFetcher(File("/sdcard/sample.apk"))
        val element2 = ApkIconDrawableFetcher(File("/sdcard/sample2.apk"))

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ApkIconDrawableFetcher('/sdcard/sample.apk')",
            actual = ApkIconDrawableFetcher(File("/sdcard/sample.apk")).toString()
        )
    }
}