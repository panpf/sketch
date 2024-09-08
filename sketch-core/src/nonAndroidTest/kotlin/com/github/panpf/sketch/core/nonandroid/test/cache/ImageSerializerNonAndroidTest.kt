package com.github.panpf.sketch.core.nonandroid.test.cache

import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.cache.SkiaBitmapImageSerializer
import com.github.panpf.sketch.cache.createImageSerializer
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.produceFingerPrint
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageSerializerNonAndroidTest {

    @Test
    fun testCreateImageSerializer() {
        assertTrue(createImageSerializer() is SkiaBitmapImageSerializer)
    }

    @Test
    fun testSkiaBitmapImageSerializer() {
        val (context, sketch) = getTestContextAndSketch()
        val imageSerializer = SkiaBitmapImageSerializer

        val imageFile = ResourceImages.jpeg
        val request = ImageRequest(context, imageFile.uri)

        val imageFinger: String
        val image = imageFile.decode().asOrThrow<SkiaBitmapImage>().apply {
            assertEquals(expected = Size(1291, 1936), actual = size)
            imageFinger = produceFingerPrint(this.bitmap)
        }

        assertTrue(imageSerializer.supportImage(image))
        assertFalse(imageSerializer.supportImage(FakeImage(Size(100, 100))))

        val buffer = Buffer()
        assertEquals(expected = 0, actual = buffer.size)

        imageSerializer.compress(image, buffer)
        assertEquals(expected = 2101819, actual = buffer.size)

        val requestContext = request.toRequestContext(sketch, Size.Origin)
        val imageInfo = ImageInfo(image.size, "image/jpeg")
        val dataSource1 = ByteArrayDataSource(buffer.readByteArray(), LOCAL)

        val newImageFinger: String
        imageSerializer.decode(requestContext, imageInfo, dataSource1)
            .asOrThrow<SkiaBitmapImage>().apply {
                assertEquals(expected = Size(1291, 1936), actual = size)
                newImageFinger = produceFingerPrint(this.bitmap)
            }

        assertEquals(expected = imageFinger, actual = newImageFinger)
    }
}