package com.github.panpf.sketch.test.cache

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.CountDrawablePendingManager
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.cache.internal.defaultMemoryCacheBytes
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.test.utils.getContextAndSketch
import com.github.panpf.sketch.util.Logger
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountDrawablePendingManagerTest {

    @Test
    fun test() {
        val (context, _) = getContextAndSketch()
        val logger = Logger()
        val bitmapPool = LruBitmapPool(context.defaultMemoryCacheBytes())
        val countDrawable = SketchCountBitmapDrawable(
            context.resources,
            CountBitmap(
                initBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
                requestKey = "requestKey",
                imageUri = "imageUri",
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                exifOrientation = 0,
                transformedList = null,
                logger = logger,
                bitmapPool = bitmapPool
            ), NETWORK
        )
        val countDrawable1 = SketchCountBitmapDrawable(
            context.resources,
            CountBitmap(
                initBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
                requestKey = "requestKey1",
                imageUri = "imageUri1",
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                exifOrientation = 0,
                transformedList = null,
                logger = logger,
                bitmapPool = bitmapPool
            ), NETWORK
        )

        CountDrawablePendingManager(logger).apply {
            assertThrow(IllegalStateException::class) {
                mark("test", countDrawable.requestKey, countDrawable)
            }
            assertThrow(IllegalStateException::class) {
                complete("test", countDrawable1.requestKey)
            }

            runBlocking(Dispatchers.Main) {
                Assert.assertEquals(0, size)
                Assert.assertEquals(0, countDrawable.getPendingCount())
                Assert.assertEquals(0, countDrawable1.getPendingCount())

                mark("test", countDrawable.requestKey, countDrawable)
                Assert.assertEquals(1, size)
                Assert.assertEquals(1, countDrawable.getPendingCount())
                Assert.assertEquals(0, countDrawable1.getPendingCount())

                mark("test", countDrawable1.requestKey, countDrawable1)
                Assert.assertEquals(2, size)
                Assert.assertEquals(1, countDrawable.getPendingCount())
                Assert.assertEquals(1, countDrawable1.getPendingCount())

                mark("test", countDrawable.requestKey, countDrawable)
                Assert.assertEquals(2, size)
                Assert.assertEquals(2, countDrawable.getPendingCount())
                Assert.assertEquals(1, countDrawable1.getPendingCount())

                mark("test", countDrawable1.requestKey, countDrawable1)
                Assert.assertEquals(2, size)
                Assert.assertEquals(2, countDrawable.getPendingCount())
                Assert.assertEquals(2, countDrawable1.getPendingCount())

                complete("test", countDrawable.requestKey)
                Assert.assertEquals(2, size)
                Assert.assertEquals(1, countDrawable.getPendingCount())
                Assert.assertEquals(2, countDrawable1.getPendingCount())

                complete("test", countDrawable1.requestKey)
                Assert.assertEquals(2, size)
                Assert.assertEquals(1, countDrawable.getPendingCount())
                Assert.assertEquals(1, countDrawable1.getPendingCount())

                complete("test", countDrawable.requestKey)
                Assert.assertEquals(1, size)
                Assert.assertEquals(0, countDrawable.getPendingCount())
                Assert.assertEquals(1, countDrawable1.getPendingCount())

                complete("test", countDrawable1.requestKey)
                Assert.assertEquals(0, size)
                Assert.assertEquals(0, countDrawable.getPendingCount())
                Assert.assertEquals(0, countDrawable1.getPendingCount())
            }
        }
    }
}