package com.github.panpf.sketch.test.decode

import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.sampling
import com.github.panpf.sketch.decode.internal.samplingForRegion
import com.github.panpf.sketch.test.utils.getContext
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapDecodeTest {

    // todo 测试 从 api 19 到 api 31 的结果

    @Test
    fun testBitmapFactoryMutable() {
        val context = getContext()
        val imageName = "sample.jpeg"

        val options = BitmapFactory.Options()
        Assert.assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        Assert.assertFalse(bitmap.isMutable)

        options.inMutable = true
        Assert.assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        Assert.assertTrue(bitmap1.isMutable)
    }

    @Test
    fun testBitmapRegionDecoderMutable() {
        val context = getContext()
        val imageName = "sample.jpeg"
        val imageSize = Size(1291, 1936)

        val options = BitmapFactory.Options()
        Assert.assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName)
            .run { BitmapRegionDecoder.newInstance(this, false) }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        Assert.assertFalse(bitmap.isMutable)

        options.inMutable = true
        Assert.assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName)
            .run { BitmapRegionDecoder.newInstance(this, false) }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        Assert.assertFalse(bitmap1.isMutable)
    }

    @Test
    fun testBitmapFactoryInSampleSize() {
        val context = getContext()
        val imageName = "sample.jpeg"
        val imageSize = Size(1291, 1936)

        val options = BitmapFactory.Options().apply {
            inSampleSize = 2
        }
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        Assert.assertEquals(imageSize.sampling(2), bitmap.size)
    }

    @Test
    fun testBitmapRegionDecoderInSampleSize() {
        val context = getContext()
        val imageName = "sample.jpeg"
        val imageSize = Size(1291, 1936)

        val options = BitmapFactory.Options().apply {
            inSampleSize = 2
        }
        val bitmap = context.assets.open(imageName)
            .run { BitmapRegionDecoder.newInstance(this, false) }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        Assert.assertEquals(imageSize.samplingForRegion(2), bitmap.size)
    }

    private fun <R> BitmapRegionDecoder.use(block: BitmapRegionDecoder.() -> R): R {
        try {
            return block(this)
        } finally {
            recycle()
        }
    }
}