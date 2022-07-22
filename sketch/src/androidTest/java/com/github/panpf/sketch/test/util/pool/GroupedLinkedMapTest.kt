package com.github.panpf.sketch.test.util.pool

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.pool.GroupedLinkedMap
import com.github.panpf.sketch.util.pool.Poolable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupedLinkedMapTest {
    // todo not yet implement

    @Test
    fun testPutGet() {
        GroupedLinkedMap<MyKey, Bitmap>().apply {
            val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
            val bitmap2 = Bitmap.createBitmap(200, 100, ARGB_8888)

            Assert.assertNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))

            put(bitmap.toKey(), bitmap)
            Assert.assertNotNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))

            put(bitmap2.toKey(), bitmap2)
            Assert.assertNull(get(bitmap.toKey()))
            Assert.assertNotNull(get(bitmap2.toKey()))

            Assert.assertNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))
        }
    }

    @Test
    fun testRemoveLast() {
        GroupedLinkedMap<MyKey, Bitmap>().apply {
            val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
            val bitmap2 = Bitmap.createBitmap(200, 100, ARGB_8888)
            put(bitmap.toKey(), bitmap)
            put(bitmap2.toKey(), bitmap2)
            removeLast()

            Assert.assertNotNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))
        }
    }

    @Test
    fun testToString() {
        GroupedLinkedMap<MyKey, Bitmap>().apply {
            val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
            put(bitmap.toKey(), bitmap)
            val bitmap2 = Bitmap.createBitmap(200, 100, ARGB_8888)
            put(bitmap2.toKey(), bitmap2)
            Assert.assertEquals(
                "GroupedLinkedMap( {MyKey(key=100x100(ARGB_8888)):1}, {MyKey(key=200x100(ARGB_8888)):1} )",
                toString()
            )
        }
    }

    private fun Bitmap.toKey(): MyKey = MyKey("${width}x${height}($config)")

    data class MyKey(val key: String) : Poolable {
        override fun offer() {

        }
    }
}