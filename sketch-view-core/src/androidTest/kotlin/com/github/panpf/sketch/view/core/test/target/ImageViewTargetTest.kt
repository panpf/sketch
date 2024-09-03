package com.github.panpf.sketch.view.core.test.target

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

@RunWith(AndroidJUnit4::class)
class ImageViewTargetTest {

    // TODO test fitScale, drawable

//    @Test
//    fun testTarget() {
//        val context1 = getTestContext()
//        val uri = ResourceImages.jpeg.uri
//        val imageView = TestOptionsImageView(context1)
//
//        ImageRequest(context1, uri).apply {
//            assertNull(target)
//        }
//
//        ImageRequest(imageView, uri).apply {
//            assertEquals(ImageViewTarget(imageView), target)
//        }
//
//        imageView.updateImageOptions {
//            memoryCachePolicy(WRITE_ONLY)
//        }
//
//        ImageRequest(imageView, uri).apply {
//            assertEquals(ImageViewTarget(imageView), target)
//            assertEquals(WRITE_ONLY, memoryCachePolicy)
//        }
//
//        ImageRequest(imageView, uri) {
//            target(null)
//        }.apply {
//            assertNull(target)
//            assertEquals(ENABLED, memoryCachePolicy)
//        }
//
//        ImageRequest(imageView, uri) {
//            target(onStart = { _, _ -> }, onSuccess = { _, _ -> }, onError = { _, _ -> })
//        }.apply {
//            assertNotNull(target)
//            assertEquals(ENABLED, memoryCachePolicy)
//        }
//        ImageRequest(imageView, uri) {
//            target(onStart = { _, _ -> })
//        }.apply {
//            assertNotNull(target)
//            assertEquals(ENABLED, memoryCachePolicy)
//        }
//        ImageRequest(imageView, uri) {
//            target(onSuccess = { _, _ -> })
//        }.apply {
//            assertNotNull(target)
//            assertEquals(ENABLED, memoryCachePolicy)
//        }
//        ImageRequest(imageView, uri) {
//            target(onError = { _, _ -> })
//        }.apply {
//            assertNotNull(target)
//            assertEquals(ENABLED, memoryCachePolicy)
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

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
    }
}