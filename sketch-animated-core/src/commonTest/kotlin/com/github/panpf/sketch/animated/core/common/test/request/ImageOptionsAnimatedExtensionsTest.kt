package com.github.panpf.sketch.animated.core.common.test.request

import com.github.panpf.sketch.request.ANIMATED_TRANSFORMATION_KEY
import com.github.panpf.sketch.request.ANIMATION_END_CALLBACK_KEY
import com.github.panpf.sketch.request.ANIMATION_REPEAT_COUNT_KEY
import com.github.panpf.sketch.request.ANIMATION_START_CALLBACK_KEY
import com.github.panpf.sketch.request.DISALLOW_ANIMATED_IMAGE_KEY
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
import kotlin.test.assertNotNull
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
            extras!!.entry(ANIMATION_REPEAT_COUNT_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNotNull(this.cacheKey)
            }
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
            extras!!.entry(ANIMATION_START_CALLBACK_KEY)!!.apply {
                assertNull(this.requestKey)
                assertNull(this.cacheKey)
            }
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
            extras!!.entry(ANIMATION_END_CALLBACK_KEY)!!.apply {
                assertNull(this.requestKey)
                assertNull(this.cacheKey)
            }
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
            extras!!.entry(ANIMATED_TRANSFORMATION_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNull(this.cacheKey)
            }
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
                extras!!.entry(DISALLOW_ANIMATED_IMAGE_KEY)!!.apply {
                    assertNotNull(this.requestKey)
                    assertNotNull(this.cacheKey)
                }
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