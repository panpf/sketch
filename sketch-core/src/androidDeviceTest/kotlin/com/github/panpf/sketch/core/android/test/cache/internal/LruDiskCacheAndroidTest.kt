package com.github.panpf.sketch.core.android.test.cache.internal

import com.github.panpf.sketch.cache.internal.checkDiskCacheDirectory
import com.github.panpf.sketch.test.utils.getTestContext
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals

class LruDiskCacheAndroidTest {

    @Test
    fun testCheckDiskCacheDirectory() {
        val context = getTestContext()

        val dir1 = "/sdcard/panpf/sketch/cache1".toPath()
        assertEquals(
            expected = dir1,
            actual = checkDiskCacheDirectory(context, dir1)
        )

        val dir2 = "/sdcard/panpf/sketch/cache2".toPath()
        assertEquals(
            expected = dir2,
            actual = checkDiskCacheDirectory(context, dir2)
        )
    }
}