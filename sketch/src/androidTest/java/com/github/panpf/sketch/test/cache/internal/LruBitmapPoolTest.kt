/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.test.cache.internal

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.formatFileSize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LruBitmapPoolTest {

    @Test
    fun testMaxSize() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            Assert.assertEquals("10MB", maxSize.formatFileSize())
        }

        LruBitmapPool(100L * 1024 * 1024).apply {
            Assert.assertEquals("100MB", maxSize.formatFileSize())
        }
    }

    @Test
    fun testAllowedConfigs() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            when {
                VERSION.SDK_INT >= 33 -> {
                    Assert.assertEquals(
                        "[null, ALPHA_8, RGB_565, ARGB_4444, ARGB_8888, RGBA_F16, HARDWARE, RGBA_1010102]",
                        allowedConfigs.toString()
                    )
                }
                VERSION.SDK_INT >= 26 -> {
                    Assert.assertEquals(
                        "[null, ALPHA_8, RGB_565, ARGB_4444, ARGB_8888, RGBA_F16, HARDWARE]",
                        allowedConfigs.toString()
                    )
                }
                VERSION.SDK_INT >= 19 -> {
                    Assert.assertEquals(
                        "[null, ALPHA_8, RGB_565, ARGB_4444, ARGB_8888]",
                        allowedConfigs.toString()
                    )
                }
                else -> {
                    Assert.assertEquals(
                        "[ALPHA_8, RGB_565, ARGB_4444, ARGB_8888]",
                        allowedConfigs.toString()
                    )
                }
            }
        }

        LruBitmapPool(
            100L * 1024 * 1024,
            allowedConfigs = setOf(RGB_565, ARGB_8888)
        ).apply {
            Assert.assertEquals(
                "[RGB_565, ARGB_8888]",
                allowedConfigs.toString()
            )
        }
    }

    @Test
    fun testSize() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())

            putBitmap(1)
            Assert.assertEquals("1MB", size.formatFileSize())

            putBitmap(2)
            Assert.assertEquals("3MB", size.formatFileSize())
        }
    }

    @Test
    fun testPut() {
        LruBitmapPool(10L * 1024 * 1024, allowedConfigs = setOf(ARGB_8888)).apply {
            logger = Logger()

            Assert.assertEquals("0B", size.formatFileSize())

            putBitmap(1)
            Assert.assertEquals("1MB", size.formatFileSize())

            putBitmap(2)
            Assert.assertEquals("3MB", size.formatFileSize())

            // bitmap.isRecycled
            Assert.assertFalse(put(Bitmap.createBitmap(10, 10, ARGB_8888).apply { recycle() }))
            Assert.assertEquals("3MB", size.formatFileSize())

            // !bitmap.isMutable
            val resources = getTestContext().resources
            Assert.assertFalse(put(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)))
            Assert.assertEquals("3MB", size.formatFileSize())

            // bitmapSize > maxSize
            Assert.assertFalse(put(Bitmap.createBitmap(1000, 3000, ARGB_8888)))
            Assert.assertEquals("3MB", size.formatFileSize())

            // !allowedConfigs.contains(bitmap.config)
            Assert.assertFalse(put(Bitmap.createBitmap(10, 10, RGB_565)))
            Assert.assertEquals("3MB", size.formatFileSize())

            // repeat put same bitmap
            val bitmap = Bitmap.createBitmap(10, 10, ARGB_8888)
            Assert.assertTrue(put(bitmap))
            Assert.assertTrue(put(bitmap))
            Assert.assertSame(bitmap, get(10, 10, ARGB_8888))
            Assert.assertNull(get(10, 10, ARGB_8888))
        }
    }

    @Test
    fun testGetDirty() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            logger = Logger()

            Assert.assertEquals("0B", size.formatFileSize())
            Assert.assertFalse(exist(100, 100, ARGB_8888))
            Assert.assertNull(getDirty(100, 100, ARGB_8888))

            put(Bitmap.createBitmap(100, 100, ARGB_8888).apply {
                setPixel(50, 50, Color.RED)
            })
            Assert.assertTrue(exist(100, 100, ARGB_8888))
            Assert.assertEquals("39.06KB", size.formatFileSize())
            getDirty(100, 100, ARGB_8888).apply {
                Assert.assertEquals(Color.RED, this!!.getPixel(50, 50))
            }
            Assert.assertEquals("0B", size.formatFileSize())
        }
    }

    @Test
    fun testGet() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            logger = Logger()

            Assert.assertEquals("0B", size.formatFileSize())
            Assert.assertNull(get(100, 100, ARGB_8888))
            Assert.assertFalse(exist(100, 100, ARGB_8888))

            put(Bitmap.createBitmap(100, 100, ARGB_8888).apply {
                setPixel(50, 50, Color.RED)
            })
            Assert.assertTrue(exist(100, 100, ARGB_8888))
            Assert.assertEquals("39.06KB", size.formatFileSize())
            get(100, 100, ARGB_8888).apply {
                Assert.assertEquals(0, this!!.getPixel(50, 50))
            }
            Assert.assertEquals("0B", size.formatFileSize())
        }
    }

    @Test
    fun testGetOrCreate() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            logger = Logger()

            Assert.assertEquals("0B", size.formatFileSize())
            Assert.assertFalse(exist(100, 100, ARGB_8888))
            Assert.assertNotNull(getOrCreate(100, 100, ARGB_8888))
            Assert.assertEquals("0B", size.formatFileSize())

            put(Bitmap.createBitmap(100, 100, ARGB_8888).apply {
                setPixel(50, 50, Color.RED)
            })
            Assert.assertTrue(exist(100, 100, ARGB_8888))
            Assert.assertEquals("39.06KB", size.formatFileSize())
            getOrCreate(100, 100, ARGB_8888).apply {
                Assert.assertEquals(0, this.getPixel(50, 50))
            }
            Assert.assertEquals("0B", size.formatFileSize())
        }
    }

    @Test
    fun testTrim() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            logger = Logger()

            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(1)
            putBitmap(2)
            putBitmap(3)
            putBitmap(4)
            Assert.assertEquals("10MB", size.formatFileSize())

            trim(ComponentCallbacks2.TRIM_MEMORY_MODERATE)
            Assert.assertEquals("0B", size.formatFileSize())
        }

        LruBitmapPool(10L * 1024 * 1024).apply {
            logger = Logger()

            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(1)
            putBitmap(2)
            putBitmap(3)
            putBitmap(4)
            Assert.assertEquals("10MB", size.formatFileSize())

            trim(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
            Assert.assertEquals("3MB", size.formatFileSize())
        }
    }

    @Test
    fun testClear() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            logger = Logger()

            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(1)
            putBitmap(2)
            putBitmap(3)
            putBitmap(4)
            Assert.assertEquals("10MB", size.formatFileSize())

            clear()
            Assert.assertEquals("0B", size.formatFileSize())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = LruBitmapPool(100)
        val element11 = LruBitmapPool(100)
        val element2 = LruBitmapPool(200)
        val element3 = LruBitmapPool(100, allowedConfigs = setOf(RGB_565, Bitmap.Config.ALPHA_8))

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        LruBitmapPool(
            10L * 1024 * 1024,
            allowedConfigs = setOf(RGB_565, ARGB_8888)
        ).apply {
            val strategy = if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                "SizeConfigStrategy"
            } else {
                "AttributeStrategy"
            }
            Assert.assertEquals(
                "LruBitmapPool(maxSize=10MB,strategy=${strategy},allowedConfigs=[RGB_565,ARGB_8888])",
                toString()
            )
        }
    }

    private fun LruBitmapPool.putBitmap(sizeMb: Int) {
        val bytes = sizeMb * 1024 * 1024
        val pixelCount = bytes / 4
        val width = 10
        val height = pixelCount / width
        put(Bitmap.createBitmap(width, height, ARGB_8888))
    }
}