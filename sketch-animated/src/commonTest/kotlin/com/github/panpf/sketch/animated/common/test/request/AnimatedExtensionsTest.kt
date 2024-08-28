package com.github.panpf.sketch.animated.common.test.request

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class AnimatedExtensionsTest {

    @Test
    fun testRepeatCount() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.animGif.uri).apply {
            assertNull(repeatCount)
        }
        assertFails {
            ImageRequest(context, ResourceImages.animGif.uri) {
                this.repeatCount(-2)
            }
        }
        assertFails {
            ImageRequest(context, ResourceImages.animGif.uri) {
                repeatCount(-2)
            }
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            this.repeatCount(5)
        }.apply {
            assertEquals(5, repeatCount)
        }

        ImageRequest(context, ResourceImages.animGif.uri).apply {
            assertNull(repeatCount)
        }
        assertFails {
            ImageRequest(context, ResourceImages.animGif.uri) {
                this.repeatCount(-2)
            }
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(5)
        }.apply {
            assertEquals(5, repeatCount)
        }

        ImageOptions().apply {
            assertNull(repeatCount)
        }
        assertFails {
            ImageOptions {
                repeatCount(-2)
            }
        }
        ImageOptions {
            repeatCount(5)
        }.apply {
            assertEquals(5, repeatCount)
        }

        val key1 = ImageRequest(context, ResourceImages.animGif.uri).key
        val key2 = ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(5)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, ResourceImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(5)
        }.toRequestContext(sketch).cacheKey
        assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationStart() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimationStartCallback = {}

        ImageRequest(context, ResourceImages.animGif.uri).apply {
            assertNull(animationStartCallback)
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            this.onAnimationStart(myAnimationStartCallback)
        }.apply {
            assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        ImageRequest(context, ResourceImages.animGif.uri).apply {
            assertNull(animationStartCallback)
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.apply {
            assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        ImageOptions().apply {
            assertNull(animationStartCallback)
        }
        ImageOptions {
            onAnimationStart(myAnimationStartCallback)
        }.apply {
            assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        val key1 = ImageRequest(context, ResourceImages.animGif.uri).key
        val key2 = ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.key
        assertEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, ResourceImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationEnd() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimationEndCallback = {}

        ImageRequest(context, ResourceImages.animGif.uri).apply {
            assertNull(animationEndCallback)
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            this.onAnimationEnd(myAnimationEndCallback)
        }.apply {
            assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        ImageRequest(context, ResourceImages.animGif.uri).apply {
            assertNull(animationEndCallback)
        }
        ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.apply {
            assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        ImageOptions().apply {
            assertNull(animationEndCallback)
        }
        ImageOptions {
            onAnimationEnd(myAnimationEndCallback)
        }.apply {
            assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        val key1 = ImageRequest(context, ResourceImages.animGif.uri).key
        val key2 = ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.key
        assertEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, ResourceImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }
}