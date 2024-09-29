package com.github.panpf.sketch.extensions.core.common.test.ability

import com.github.panpf.sketch.ability.getMimeTypeFromImageResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class MimeTypeLogoTest {

    @Test
    fun testGetMimeTypeFromImageResult() {
        val context = getTestContext()

        // Success, mimeType not empty
        assertEquals(
            expected = "image/bmp",
            actual = getMimeTypeFromImageResult(
                result = ImageResult.Success(
                    request = ImageRequest(context, "http://sample.com/test.webp"),
                    image = FakeImage(100, 100),
                    cacheKey = "cacheKey",
                    imageInfo = ImageInfo(100, 100, "image/bmp"),
                    dataFrom = DataFrom.LOCAL,
                    resize = Resize(200, 200),
                    transformeds = null,
                    extras = null
                ),
                uri = null
            )
        )

        // Success, mimeType empty, uri null
        assertEquals(
            expected = null,
            actual = getMimeTypeFromImageResult(
                result = ImageResult.Success(
                    request = ImageRequest(context, "http://sample.com/test.webp"),
                    image = FakeImage(100, 100),
                    cacheKey = "cacheKey",
                    imageInfo = ImageInfo(100, 100, ""),
                    dataFrom = DataFrom.LOCAL,
                    resize = Resize(200, 200),
                    transformeds = null,
                    extras = null
                ),
                uri = null
            )
        )

        // Success, mimeType empty, uri not null
        assertEquals(
            expected = "image/gif",
            actual = getMimeTypeFromImageResult(
                ImageResult.Success(
                    request = ImageRequest(context, "http://sample.com/test.webp"),
                    image = FakeImage(100, 100),
                    cacheKey = "cacheKey",
                    imageInfo = ImageInfo(100, 100, ""),
                    dataFrom = DataFrom.LOCAL,
                    resize = Resize(200, 200),
                    transformeds = null,
                    extras = null
                ),
                uri = "http://sample.com/test.gif"
            )
        )

        // Error, request uri not empty, uri null
        assertEquals(
            expected = "image/webp",
            actual = getMimeTypeFromImageResult(
                result = ImageResult.Error(
                    request = ImageRequest(context, "http://sample.com/test.webp"),
                    image = FakeImage(100, 100),
                    throwable = Exception("test")
                ),
                uri = null
            )
        )

        // Error, request uri empty, uri null
        assertEquals(
            expected = null,
            actual = getMimeTypeFromImageResult(
                result = ImageResult.Error(
                    request = ImageRequest(context, "http://sample.com/test"),
                    image = FakeImage(100, 100),
                    throwable = Exception("test")
                ),
                uri = null
            )
        )

        // Error, request uri empty, uri not null
        assertEquals(
            expected = "image/gif",
            actual = getMimeTypeFromImageResult(
                result = ImageResult.Error(
                    request = ImageRequest(context, "http://sample.com/test"),
                    image = FakeImage(100, 100),
                    throwable = Exception("test")
                ),
                uri = "http://sample.com/test.gif"
            )
        )

        // Result null, uri not null
        assertEquals(
            expected = "image/gif",
            actual = getMimeTypeFromImageResult(
                result = null,
                uri = "http://sample.com/test.gif"
            )
        )
        // Result null, uri null
        assertEquals(
            expected = null,
            actual = getMimeTypeFromImageResult(
                result = null,
                uri = null
            )
        )
    }
}