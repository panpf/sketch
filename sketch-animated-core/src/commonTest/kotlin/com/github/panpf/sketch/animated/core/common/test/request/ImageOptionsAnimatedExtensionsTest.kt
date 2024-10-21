package com.github.panpf.sketch.animated.core.common.test.request

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.util.Rect
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class ImageOptionsAnimatedExtensionsTest {

    @Test
    fun testRepeatCount() = runTest {
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
    }

    @Test
    fun testOnAnimationStart() = runTest {
        val myAnimationStartCallback = {}

        ImageOptions().apply {
            assertNull(animationStartCallback)
        }
        ImageOptions {
            onAnimationStart(myAnimationStartCallback)
        }.apply {
            assertEquals(myAnimationStartCallback, animationStartCallback)
        }
    }

    @Test
    fun testOnAnimationEnd() = runTest {
        val myAnimationEndCallback = {}

        ImageOptions().apply {
            assertNull(animationEndCallback)
        }
        ImageOptions {
            onAnimationEnd(myAnimationEndCallback)
        }.apply {
            assertEquals(myAnimationEndCallback, animationEndCallback)
        }
    }

    @Test
    fun testAnimatedTransformation() = runTest {
        val myAnimatedTransformation = TranslucentAnimatedTransformation

        ImageOptions().apply {
            assertNull(animatedTransformation)
        }
        ImageOptions {
            animatedTransformation(myAnimatedTransformation)
        }.apply {
            assertEquals(myAnimatedTransformation, animatedTransformation)
        }
    }

    @Test
    fun testDisallowAnimatedImage() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(disallowAnimatedImage)
            }

            disallowAnimatedImage()
            build().apply {
                assertEquals(true, disallowAnimatedImage)
            }

            disallowAnimatedImage(false)
            build().apply {
                assertEquals(false, disallowAnimatedImage)
            }

            disallowAnimatedImage(null)
            build().apply {
                assertNull(disallowAnimatedImage)
            }
        }
    }

    private data object TranslucentAnimatedTransformation : AnimatedTransformation {
        override val key: String = "TranslucentAnimatedTransformation"

        override fun transform(canvas: Any, bounds: Rect): PixelOpacity {
            return PixelOpacity.TRANSLUCENT
        }
    }
}