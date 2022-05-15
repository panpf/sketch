package com.github.panpf.sketch.test.cache.internal

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.getContext
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
                Build.VERSION.SDK_INT >= 26 -> {
                    Assert.assertEquals(
                        "[null, ALPHA_8, RGB_565, ARGB_4444, ARGB_8888, RGBA_F16, HARDWARE]",
                        allowedConfigs.toString()
                    )
                }
                Build.VERSION.SDK_INT >= 19 -> {
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
            Assert.assertEquals("0B", size.formatFileSize())

            putBitmap(1)
            Assert.assertEquals("1MB", size.formatFileSize())

            putBitmap(2)
            Assert.assertEquals("3MB", size.formatFileSize())

            // bitmap.isRecycled
            Assert.assertFalse(put(Bitmap.createBitmap(10, 10, ARGB_8888).apply { recycle() }))
            Assert.assertEquals("3MB", size.formatFileSize())

            // !bitmap.isMutable
            val resources = getContext().resources
            Assert.assertFalse(put(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)))
            Assert.assertEquals("3MB", size.formatFileSize())

            // bitmapSize > maxSize
            Assert.assertFalse(put(Bitmap.createBitmap(1000, 3000, ARGB_8888)))
            Assert.assertEquals("3MB", size.formatFileSize())

            // !allowedConfigs.contains(bitmap.config)
            Assert.assertFalse(put(Bitmap.createBitmap(10, 10, RGB_565)))
            Assert.assertEquals("3MB", size.formatFileSize())
        }
    }

    @Test
    fun testGetDirty() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            Assert.assertNull(getDirty(100, 100, ARGB_8888))

            put(Bitmap.createBitmap(100, 100, ARGB_8888).apply {
                setPixel(50, 50, Color.RED)
            })
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
            Assert.assertEquals("0B", size.formatFileSize())
            Assert.assertNull(get(100, 100, ARGB_8888))

            put(Bitmap.createBitmap(100, 100, ARGB_8888).apply {
                setPixel(50, 50, Color.RED)
            })
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
            Assert.assertEquals("0B", size.formatFileSize())
            Assert.assertNotNull(getOrCreate(100, 100, ARGB_8888))
            Assert.assertEquals("0B", size.formatFileSize())

            put(Bitmap.createBitmap(100, 100, ARGB_8888).apply {
                setPixel(50, 50, Color.RED)
            })
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
    fun testToString() {
        LruBitmapPool(
            10L * 1024 * 1024,
            allowedConfigs = setOf(RGB_565, ARGB_8888)
        ).apply {
            val strategy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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

    @Test
    fun testSetInBitmapForBitmapFactory() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            Assert.assertFalse(
                setInBitmapForBitmapFactory(BitmapFactory.Options(), 0, 100, "image/jpeg")
            )
            Assert.assertFalse(
                setInBitmapForBitmapFactory(BitmapFactory.Options(), 100, 0, "image/jpeg")
            )
            Assert.assertFalse(
                setInBitmapForBitmapFactory(BitmapFactory.Options(), 100, 100, null)
            )
            Assert.assertFalse(
                setInBitmapForBitmapFactory(BitmapFactory.Options(), 100, 100, "")
            )
            Assert.assertFalse(
                setInBitmapForBitmapFactory(BitmapFactory.Options(), 100, 100, "image/jpeg")
            )

            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertTrue(
                setInBitmapForBitmapFactory(BitmapFactory.Options(), 100, 100, "image/jpeg")
            )

            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertTrue(
                setInBitmapForBitmapFactory(
                    BitmapFactory.Options().apply { inSampleSize = 2 },
                    200,
                    200,
                    "image/jpeg"
                )
            )

            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertFalse(
                setInBitmapForBitmapFactory(
                    BitmapFactory.Options().apply { inSampleSize = 2 },
                    300,
                    300,
                    "image/jpeg"
                )
            )
        }
    }

    @Test
    fun testSetInBitmapForRegionDecoder() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            Assert.assertFalse(
                setInBitmapForRegionDecoder(BitmapFactory.Options(), 0, 100)
            )
            Assert.assertFalse(
                setInBitmapForRegionDecoder(BitmapFactory.Options(), 100, 0)
            )
            Assert.assertTrue(
                setInBitmapForRegionDecoder(BitmapFactory.Options(), 100, 100)
            )

            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertTrue(
                setInBitmapForRegionDecoder(BitmapFactory.Options(), 100, 100)
            )

            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertTrue(
                setInBitmapForRegionDecoder(
                    BitmapFactory.Options().apply { inSampleSize = 2 }, 200, 200,
                )
            )

            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertTrue(
                setInBitmapForRegionDecoder(
                    BitmapFactory.Options().apply { inSampleSize = 2 }, 300, 300,
                )
            )
        }
    }

    @Test
    fun testFree() {
        LruBitmapPool(10L * 1024 * 1024).apply {
            Assert.assertFalse(free(null))

            Assert.assertFalse(free(Bitmap.createBitmap(100, 100, ARGB_8888).apply { recycle() }))

            val resources = getContext().resources
            Assert.assertFalse(
                free(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_launcher
                    )
                )
            )

            Assert.assertTrue(free(Bitmap.createBitmap(100, 100, ARGB_8888)))
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