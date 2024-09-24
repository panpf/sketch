package com.github.panpf.sketch.core.common.test.resize

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.resizeOnDraw
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.TestResizeOnDrawImage
import com.github.panpf.sketch.test.utils.TestResizeOnDrawTarget
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ResizeOnDrawTest {

    @Test
    fun testResizeOnDraw() {
        val context = getTestContext()
        val image = FakeImage(100, 50)

        // size null
        assertSame(
            expected = image,
            actual = image.resizeOnDraw(
                request = ImageRequest(context, ResourceImages.jpeg.uri),
                size = null
            )
        )

        // size empty
        assertSame(
            expected = image,
            actual = image.resizeOnDraw(
                request = ImageRequest(context, ResourceImages.jpeg.uri),
                size = Size.Empty
            )
        )

        // resizeOnDraw false
        assertSame(
            expected = image,
            actual = image.resizeOnDraw(
                request = ImageRequest(context, ResourceImages.jpeg.uri).newRequest {
                    resizeOnDraw(false)
                },
                size = Size(100, 100)
            )
        )

        // target null
        assertSame(
            expected = image,
            actual = image.resizeOnDraw(
                request = ImageRequest(context, ResourceImages.jpeg.uri).newRequest {
                    resizeOnDraw(true)
                    target(null)
                },
                size = Size(100, 100)
            )
        )

        // target.getResizeOnDrawHelper() null
        assertSame(
            expected = image,
            actual = image.resizeOnDraw(
                request = ImageRequest(context, ResourceImages.jpeg.uri).newRequest {
                    resizeOnDraw(true)
                    target(TestTarget())
                },
                size = Size(100, 100)
            )
        )

        // success
        image.resizeOnDraw(
            request = ImageRequest(context, ResourceImages.jpeg.uri).newRequest {
                resizeOnDraw(true)
                target(TestResizeOnDrawTarget())
            },
            size = Size(100, 100)
        ).apply {
            assertNotSame(illegal = image, actual = this)
            assertTrue(this is TestResizeOnDrawImage)
        }
    }
}