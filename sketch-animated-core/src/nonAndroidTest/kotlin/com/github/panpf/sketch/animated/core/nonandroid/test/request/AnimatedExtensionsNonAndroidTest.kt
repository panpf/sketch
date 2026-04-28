package com.github.panpf.sketch.animated.core.nonandroid.test.request

import com.github.panpf.sketch.request.ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnimatedExtensionsNonAndroidTest {

    @Test
    fun testCacheDecodeTimeoutFrame() = runTest {
        val context = getTestContext()

        ImageRequest(context, "http://sample/com/sample.jpeg").apply {
            assertNull(actual = cacheDecodeTimeoutFrame)
        }

        ImageRequest(context, "http://sample/com/sample.jpeg") {
            cacheDecodeTimeoutFrame()
        }.apply {
            assertTrue(actual = cacheDecodeTimeoutFrame!!)
            extras!!.entry(ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNull(this.cacheKey)
            }
        }

        ImageRequest(context, "http://sample/com/sample.jpeg") {
            cacheDecodeTimeoutFrame(true)
        }.apply {
            assertTrue(actual = cacheDecodeTimeoutFrame!!)
        }

        ImageRequest(context, "http://sample/com/sample.jpeg") {
            cacheDecodeTimeoutFrame(false)
        }.apply {
            assertFalse(actual = cacheDecodeTimeoutFrame!!)
        }

        ImageRequest(context, "http://sample/com/sample.jpeg") {
            cacheDecodeTimeoutFrame(true)
        }.apply {
            assertTrue(actual = cacheDecodeTimeoutFrame!!)
        }.newRequest {
            cacheDecodeTimeoutFrame(null)
        }.apply {
            assertNull(actual = cacheDecodeTimeoutFrame)
        }

        ImageOptions().apply {
            assertNull(actual = cacheDecodeTimeoutFrame)
        }

        ImageOptions {
            cacheDecodeTimeoutFrame()
        }.apply {
            assertTrue(actual = cacheDecodeTimeoutFrame!!)
            extras!!.entry(ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNull(this.cacheKey)
            }
        }

        ImageOptions {
            cacheDecodeTimeoutFrame(true)
        }.apply {
            assertTrue(actual = cacheDecodeTimeoutFrame!!)
        }

        ImageOptions {
            cacheDecodeTimeoutFrame(false)
        }.apply {
            assertFalse(actual = cacheDecodeTimeoutFrame!!)
        }

        ImageOptions {
            cacheDecodeTimeoutFrame(true)
        }.apply {
            assertTrue(actual = cacheDecodeTimeoutFrame!!)
        }.newOptions {
            cacheDecodeTimeoutFrame(null)
        }.apply {
            assertNull(actual = cacheDecodeTimeoutFrame)
        }
    }
}