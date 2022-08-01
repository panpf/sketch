package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.BitmapFactory
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.decode.internal.freeBitmap
import com.github.panpf.sketch.decode.internal.setInBitmap
import com.github.panpf.sketch.decode.internal.setInBitmapForRegion
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InBitmapUtilsTest {

    @Test
    fun testSetInBitmap() {
        val bitmapPool = LruBitmapPool(10L * 1024 * 1024)
        val logger = Logger()

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                setInBitmap(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                setInBitmap(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                    setInBitmap(
                        bitmapPool = bitmapPool,
                        logger = logger,
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
                setInBitmap(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                    setInBitmap(
                        bitmapPool = bitmapPool,
                        logger = logger,
                        options = this,
                        imageSize = Size(200, 200),
                        imageMimeType = "image/jpeg"
                    )
                )
                Assert.assertNotNull(inBitmap)
                Assert.assertTrue(inMutable)
            } else {
                Assert.assertFalse(
                    setInBitmap(
                        bitmapPool = bitmapPool,
                        logger = logger,
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
                setInBitmap(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                setInBitmap(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                    setInBitmap(
                        bitmapPool = bitmapPool,
                        logger = logger,
                        options = this,
                        imageSize = Size(200, 200),
                        imageMimeType = "image/jpeg"
                    )
                )
                Assert.assertNotNull(inBitmap)
                Assert.assertTrue(inMutable)
            } else {
                Assert.assertFalse(
                    setInBitmap(
                        bitmapPool = bitmapPool,
                        logger = logger,
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
                setInBitmap(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
        val logger = Logger()

        bitmapPool.clear()
        BitmapFactory.Options().apply {
            Assert.assertNull(inBitmap)
            Assert.assertFalse(inMutable)
            Assert.assertFalse(
                setInBitmapForRegion(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                setInBitmapForRegion(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                    setInBitmapForRegion(
                        bitmapPool = bitmapPool,
                        logger = logger,
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
                setInBitmapForRegion(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                setInBitmapForRegion(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                setInBitmapForRegion(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
                setInBitmapForRegion(
                    bitmapPool = bitmapPool,
                    logger = logger,
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
    fun testFree() {
        val bitmapPool = LruBitmapPool(10L * 1024 * 1024)
        val logger = Logger()

        Assert.assertFalse(freeBitmap(bitmapPool, logger, null))

        Assert.assertFalse(
            freeBitmap(
                bitmapPool,
                logger,
                Bitmap.createBitmap(100, 100, ARGB_8888).apply { recycle() }
            )
        )

        val resources = getTestContext().resources
        Assert.assertFalse(
            freeBitmap(
                bitmapPool,
                logger,
                BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)
            )
        )

        Assert.assertTrue(freeBitmap(bitmapPool, logger, Bitmap.createBitmap(100, 100, ARGB_8888)))
    }
}