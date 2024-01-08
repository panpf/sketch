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
package com.github.panpf.sketch.core.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.BitmapFactory
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.decode.internal.freeBitmap
import com.github.panpf.sketch.decode.internal.getOrCreate
import com.github.panpf.sketch.decode.internal.setInBitmap
import com.github.panpf.sketch.decode.internal.setInBitmapForRegion
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.formatFileSize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapPoolUtilsTest {

    @Test
    fun testSetInBitmap() {
        val bitmapPool = LruBitmapPool(10L * 1024 * 1024)

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                bitmapPool.setInBitmap(
                    options = this,
                    imageSize = Size(0, 100),
                    imageMimeType = "image/jpeg"
                )
            )
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
        }

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                bitmapPool.setInBitmap(
                    options = this,
                    imageSize = Size(100, 0),
                    imageMimeType = "image/jpeg"
                )
            )
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            bitmapPool.clear()
            BitmapFactory.Options().apply {
                inPreferredConfig = HARDWARE
                Assert.assertFalse(
                    bitmapPool.setInBitmap(
                        options = this,
                        imageSize = Size(100, 100),
                        imageMimeType = "image/jpeg"
                    )
                )
            }
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertTrue(
                bitmapPool.setInBitmap(
                    options = this,
                    imageSize = Size(100, 100),
                    imageMimeType = "image/jpeg"
                )
            )
            Assert.assertNotNull(inBitmap)
            Assert.assertTrue(inMutable)
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            inSampleSize = 2
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            if (VERSION.SDK_INT >= 19) {
                Assert.assertTrue(
                    bitmapPool.setInBitmap(
                        options = this,
                        imageSize = Size(200, 200),
                        imageMimeType = "image/jpeg"
                    )
                )
                Assert.assertNotNull(inBitmap)
                Assert.assertTrue(inMutable)
            } else {
                Assert.assertFalse(
                    bitmapPool.setInBitmap(
                        options = this,
                        imageSize = Size(200, 200),
                        imageMimeType = "image/jpeg"
                    )
                )
                Assert.assertNull(inBitmap)
                Assert.assertTrue(inMutable)
            }
        }

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                bitmapPool.setInBitmap(
                    options = this,
                    imageSize = Size(100, 100),
                    imageMimeType = "image/jpeg"
                )
            )
            Assert.assertNull(inBitmap)
            Assert.assertTrue(inMutable)
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertTrue(
                bitmapPool.setInBitmap(
                    options = this,
                    imageSize = Size(100, 100),
                    imageMimeType = "image/jpeg"
                )
            )
            Assert.assertNotNull(inBitmap)
            Assert.assertTrue(inMutable)
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            inSampleSize = 2
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertTrue(
                    bitmapPool.setInBitmap(
                        options = this,
                        imageSize = Size(200, 200),
                        imageMimeType = "image/jpeg"
                    )
                )
                Assert.assertNotNull(inBitmap)
                Assert.assertTrue(inMutable)
            } else {
                Assert.assertFalse(
                    bitmapPool.setInBitmap(
                        options = this,
                        imageSize = Size(200, 200),
                        imageMimeType = "image/jpeg"
                    )
                )
                Assert.assertNull(inBitmap)
                Assert.assertTrue(inMutable)
            }
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            inSampleSize = 2
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                bitmapPool.setInBitmap(
                    options = this,
                    imageSize = Size(300, 300),
                    imageMimeType = "image/jpeg"
                )
            )
            Assert.assertNull(inBitmap)
            Assert.assertTrue(inMutable)
        }
    }

    @Test
    fun testSetInBitmapForRegion() {
        val bitmapPool = LruBitmapPool(10L * 1024 * 1024)

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                bitmapPool.setInBitmapForRegion(
                    options = this,
                    regionSize = Size(0, 100),
                    imageMimeType = "image/jpeg",
                    imageSize = Size(500, 300)
                )
            )
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
        }

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                bitmapPool.setInBitmapForRegion(
                    options = this,
                    regionSize = Size(100, 0),
                    imageMimeType = "image/jpeg",
                    imageSize = Size(500, 300)
                )
            )
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            bitmapPool.clear()
            BitmapFactory.Options().apply {
                inPreferredConfig = HARDWARE
                Assert.assertNull(inBitmap)
                Assert.assertFalse(inMutable)
                Assert.assertFalse(
                    bitmapPool.setInBitmapForRegion(
                        options = this,
                        regionSize = Size(100, 100),
                        imageMimeType = "image/jpeg",
                        imageSize = Size(500, 300)
                    )
                )
                Assert.assertNull(inBitmap)
                Assert.assertFalse(inMutable)
            }
        }

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertTrue(
                bitmapPool.setInBitmapForRegion(
                    options = this,
                    regionSize = Size(100, 100),
                    imageMimeType = "image/jpeg",
                    imageSize = Size(500, 300)
                )
            )
            Assert.assertNotNull(inBitmap)
            Assert.assertFalse(inMutable)
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertTrue(
                bitmapPool.setInBitmapForRegion(
                    options = this,
                    regionSize = Size(100, 100),
                    imageMimeType = "image/jpeg",
                    imageSize = Size(500, 300)
                )
            )
            Assert.assertNotNull(inBitmap)
            Assert.assertFalse(inMutable)
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            inSampleSize = 2
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertTrue(
                bitmapPool.setInBitmapForRegion(
                    options = this,
                    regionSize = Size(200, 200),
                    imageMimeType = "image/jpeg",
                    imageSize = Size(500, 300)
                )
            )
            Assert.assertNotNull(inBitmap)
            Assert.assertFalse(inMutable)
        }

        bitmapPool.clear()
        bitmapPool.put(Bitmap.createBitmap(100, 100, ARGB_8888))
        BitmapFactory.Options().apply {
            inSampleSize = 2
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertTrue(
                bitmapPool.setInBitmapForRegion(
                    options = this,
                    regionSize = Size(300, 300),
                    imageMimeType = "image/jpeg",
                    imageSize = Size(500, 300)
                )
            )
            Assert.assertNotNull(inBitmap)
            Assert.assertFalse(inMutable)
        }
    }

    @Test
    fun testGetOrCreate() {
        val bitmapPool = LruBitmapPool(10L * 1024 * 1024)

        Assert.assertEquals("0B", bitmapPool.size.formatFileSize())
        Assert.assertFalse(bitmapPool.exist(100, 100, ARGB_8888))
        Assert.assertNotNull(bitmapPool.getOrCreate(100, 100, ARGB_8888))
        Assert.assertEquals("0B", bitmapPool.size.formatFileSize())

        val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
        bitmapPool.put(bitmap)
        Assert.assertTrue(bitmapPool.exist(100, 100, ARGB_8888))
        Assert.assertEquals("39.06KB", bitmapPool.size.formatFileSize())
        Assert.assertSame(bitmap, bitmapPool.getOrCreate(100, 100, ARGB_8888))
        Assert.assertEquals("0B", bitmapPool.size.formatFileSize())

        bitmapPool.put(bitmap)
        Assert.assertTrue(bitmapPool.exist(100, 100, ARGB_8888))
        Assert.assertEquals("39.06KB", bitmapPool.size.formatFileSize())
        Assert.assertNotSame(
            bitmap,
            bitmapPool.getOrCreate(100, 100, ARGB_8888, disallowReuseBitmap = true)
        )
        Assert.assertEquals("39.06KB", bitmapPool.size.formatFileSize())
    }

    @Test
    fun testFree() {
        val bitmapPool = LruBitmapPool(10L * 1024 * 1024)

        Assert.assertEquals(0, bitmapPool.size)

        bitmapPool.freeBitmap(null)
        Thread.sleep(100)
        Assert.assertEquals(0, bitmapPool.size)

        bitmapPool.freeBitmap(Bitmap.createBitmap(100, 100, ARGB_8888).apply { recycle() })
        Thread.sleep(100)
        Assert.assertEquals(0, bitmapPool.size)

        val resources = getTestContext().resources
        bitmapPool.freeBitmap(
            BitmapFactory.decodeResource(
                resources,
                com.github.panpf.sketch.test.utils.R.drawable.ic_launcher
            )
        )
        Thread.sleep(100)
        Assert.assertEquals(0, bitmapPool.size)

        bitmapPool.freeBitmap(Bitmap.createBitmap(100, 100, ARGB_8888))
        Thread.sleep(100)
        Assert.assertTrue(bitmapPool.size > 0)
    }
}