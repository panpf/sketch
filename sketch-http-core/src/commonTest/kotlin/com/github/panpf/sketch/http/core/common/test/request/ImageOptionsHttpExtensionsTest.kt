package com.github.panpf.sketch.http.core.common.test.request

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.httpHeaders
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageOptionsHttpExtensionsTest {

    @Test
    fun testHttpHeaders() {
        ImageOptions.Builder().apply {
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

    @Test
    fun testMerge() {
        ImageOptions().apply {
            assertEquals(null, this.httpHeaders)
        }.merged(ImageOptions {
            httpHeaders(HttpHeaders {
                add("addKey", "addValue")
                set("setKey", "setValue")
            })
        }).apply {
            assertEquals(listOf("addValue"), this.httpHeaders?.getAdd("addKey"))
            assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }.merged(ImageOptions {
            httpHeaders(HttpHeaders {
                add("addKey", "addValue1")
                set("setKey", "setValue1")
            })
        }).apply {
            assertEquals(listOf("addValue", "addValue1"), this.httpHeaders?.getAdd("addKey"))
            assertEquals("setValue", this.httpHeaders?.getSet("setKey"))
        }
    }
}