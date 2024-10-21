package com.github.panpf.sketch.http.core.common.test.request

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.httpHeaders
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageRequestHttpExtensionsTest {

    @Test
    fun testHttpHeaders() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(httpHeaders)
            }

            /* httpHeaders() */
            httpHeaders(HttpHeaders())
            build().apply {
                assertEquals(HttpHeaders(), httpHeaders)
            }

            httpHeaders(HttpHeaders.Builder().set("key1", "value1").build())
            build().apply {
                assertEquals(1, httpHeaders?.size)
                assertEquals("value1", httpHeaders?.getSet("key1"))
            }

            httpHeaders(null)
            build().apply {
                assertNull(httpHeaders)
            }
        }
    }
}