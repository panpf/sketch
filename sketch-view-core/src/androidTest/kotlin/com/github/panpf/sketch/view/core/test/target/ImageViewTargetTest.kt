package com.github.panpf.sketch.view.core.test.target

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.utils.TestOptionsImageView
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageViewTargetTest {

    // TODO test fitScale, drawable

//    @Test
//    fun testTarget() {
//        val context1 = getTestContext()
//        val uriString1 = MyImages.jpeg.uri
//        val imageView = TestOptionsImageView(context1)
//
//        ImageRequest(context1, uriString1).apply {
//            Assert.assertNull(target)
//        }
//
//        ImageRequest(imageView, uriString1).apply {
//            Assert.assertEquals(ImageViewTarget(imageView), target)
//        }
//
//        imageView.updateImageOptions {
//            memoryCachePolicy(WRITE_ONLY)
//        }
//
//        ImageRequest(imageView, uriString1).apply {
//            Assert.assertEquals(ImageViewTarget(imageView), target)
//            Assert.assertEquals(WRITE_ONLY, memoryCachePolicy)
//        }
//
//        ImageRequest(imageView, uriString1) {
//            target(null)
//        }.apply {
//            Assert.assertNull(target)
//            Assert.assertEquals(ENABLED, memoryCachePolicy)
//        }
//
//        ImageRequest(imageView, uriString1) {
//            target(onStart = { _, _ -> }, onSuccess = { _, _ -> }, onError = { _, _ -> })
//        }.apply {
//            Assert.assertNotNull(target)
//            Assert.assertEquals(ENABLED, memoryCachePolicy)
//        }
//        ImageRequest(imageView, uriString1) {
//            target(onStart = { _, _ -> })
//        }.apply {
//            Assert.assertNotNull(target)
//            Assert.assertEquals(ENABLED, memoryCachePolicy)
//        }
//        ImageRequest(imageView, uriString1) {
//            target(onSuccess = { _, _ -> })
//        }.apply {
//            Assert.assertNotNull(target)
//            Assert.assertEquals(ENABLED, memoryCachePolicy)
//        }
//        ImageRequest(imageView, uriString1) {
//            target(onError = { _, _ -> })
//        }.apply {
//            Assert.assertNotNull(target)
//            Assert.assertEquals(ENABLED, memoryCachePolicy)
//        }
//    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val imageView1 = ImageView(context)
        val imageView2 = ImageView(context)
        val element1 = ImageViewTarget(imageView1)
        val element11 = ImageViewTarget(imageView1)
        val element2 = ImageViewTarget(imageView2)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }
}