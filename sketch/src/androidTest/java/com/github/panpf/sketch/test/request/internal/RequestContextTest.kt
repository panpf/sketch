package com.github.panpf.sketch.test.request.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.cache.internal.defaultMemoryCacheBytes
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.getContext
import com.github.panpf.sketch.util.Logger
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestContextTest {

    @Test
    fun test() {
        val context = getContext()
        val logger = Logger()
        val bitmapPool = LruBitmapPool(context.defaultMemoryCacheBytes())
        val countDrawable = SketchCountBitmapDrawable(
            context.resources,
            CountBitmap(
                initBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
                imageUri = "imageUri",
                requestKey = "requestKey",
                requestCacheKey = "requestCacheKey",
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                imageExifOrientation = 0,
                transformedList = null,
                logger = logger,
                bitmapPool = bitmapPool
            ), NETWORK
        )
        val countDrawable1 = SketchCountBitmapDrawable(
            context.resources,
            CountBitmap(
                initBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
                imageUri = "imageUri1",
                requestKey = "requestKey1",
                requestCacheKey = "requestCacheKey1",
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                imageExifOrientation = 0,
                transformedList = null,
                logger = logger,
                bitmapPool = bitmapPool
            ), NETWORK
        )

        RequestContext().apply {
            assertThrow(IllegalStateException::class) {
                pendingCountDrawable(countDrawable, "test")
            }
            assertThrow(IllegalStateException::class) {
                completeCountDrawable("test")
            }

            runBlocking(Dispatchers.Main) {
                Assert.assertEquals(0, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())

                pendingCountDrawable(countDrawable, "test")
                Assert.assertEquals(1, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())

                pendingCountDrawable(countDrawable1, "test")
                Assert.assertEquals(0, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(1, countDrawable1.countBitmap.getPendingCount())

                pendingCountDrawable(countDrawable, "test")
                Assert.assertEquals(1, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())

                completeCountDrawable("test")
                Assert.assertEquals(0, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())
            }
        }
    }
}