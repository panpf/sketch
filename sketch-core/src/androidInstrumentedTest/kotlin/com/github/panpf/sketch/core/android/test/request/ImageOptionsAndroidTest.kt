package com.github.panpf.sketch.core.android.test.request

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.FixedColorType
import com.github.panpf.sketch.drawable.ColorEquitableDrawable
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.fallback
import com.github.panpf.sketch.request.isNotEmpty
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.sizeWithDisplay
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.screenSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageOptionsAndroidTest {

    @Test
    fun testSizeWithDisplay() {
        val context = getTestContext()
        ImageOptions().apply {
            assertEquals(
                expected = null,
                actual = sizeResolver
            )
        }

        ImageOptions {
            sizeWithDisplay(context)
        }.apply {
            assertEquals(
                expected = FixedSizeResolver(context.screenSize()),
                actual = sizeResolver
            )
        }
    }

    @Test
    fun testPlaceholder() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(placeholder)
            }

            placeholder(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                assertEquals(IntColorDrawableStateImage(Color.BLUE), placeholder)
            }

            placeholder(ColorEquitableDrawable(Color.GREEN))
            build().apply {
                assertEquals(true, placeholder is DrawableStateImage)
            }

            placeholder(android.R.drawable.ic_lock_lock)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.ic_lock_lock),
                    placeholder
                )
            }

            placeholder(IntColor(TestColor.RED))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(IntColor(TestColor.RED)),
                    placeholder
                )
            }

            placeholder(ResColor(android.R.drawable.ic_lock_lock))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(ResColor(android.R.drawable.ic_lock_lock)),
                    placeholder
                )
            }

            placeholder(null)
            build().apply {
                assertNull(placeholder)
            }
        }
    }

    @Test
    fun testFallback() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(fallback)
            }

            fallback(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                assertEquals(IntColorDrawableStateImage(Color.BLUE), fallback)
            }

            fallback(ColorEquitableDrawable(Color.GREEN))
            build().apply {
                assertEquals(true, fallback is DrawableStateImage)
            }

            fallback(android.R.drawable.ic_lock_lock)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.ic_lock_lock),
                    fallback
                )
            }

            fallback(IntColor(TestColor.RED))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(IntColor(TestColor.RED)),
                    fallback
                )
            }

            fallback(ResColor(android.R.drawable.ic_lock_lock))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(ResColor(android.R.drawable.ic_lock_lock)),
                    fallback
                )
            }

            fallback(null)
            build().apply {
                assertNull(fallback)
            }
        }
    }

    @Test
    fun testError() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(error)
            }

            error(ColorEquitableDrawable(Color.GREEN))
            build().apply {
                assertEquals(
                    DrawableStateImage(ColorEquitableDrawable(Color.GREEN)),
                    error
                )
            }

            error(android.R.drawable.ic_lock_lock)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.ic_lock_lock),
                    error
                )
            }

            error(IntColor(TestColor.RED))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(IntColor(TestColor.RED)),
                    error
                )
            }

            error(ResColor(android.R.drawable.ic_lock_lock))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(ResColor(android.R.drawable.ic_lock_lock)),
                    error
                )
            }

            error(null)
            build().apply {
                assertNull(error)
            }
        }
    }

    @Test
    fun testColorType() {
        ImageOptions {
            colorType(Bitmap.Config.ARGB_8888)
        }.apply {
            assertEquals(FixedColorType(Bitmap.Config.ARGB_8888.name), colorType)
        }
    }

    @Test
    fun testColorSpace() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        ImageOptions {
            colorSpace(ColorSpace.Named.LINEAR_SRGB)
        }.apply {
            assertEquals(BitmapColorSpace("LINEAR_SRGB"), colorSpace)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testPreferQualityOverSpeed() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(preferQualityOverSpeed)
            }

            preferQualityOverSpeed()
            build().apply {
                assertEquals(true, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(false)
            build().apply {
                assertNull(preferQualityOverSpeed)
            }

            preferQualityOverSpeed(null)
            build().apply {
                assertNull(preferQualityOverSpeed)
            }
        }
    }

    @Test
    fun testIsEmpty() {
        ImageOptions().apply {
            assertTrue(this.isEmpty())
            assertFalse(this.isNotEmpty())
            @Suppress("DEPRECATION")
            assertNull(this.preferQualityOverSpeed)
        }

        ImageOptions {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            @Suppress("DEPRECATION")
            (assertNotNull(this.preferQualityOverSpeed))
        }
    }

    @Test
    fun testMerged() {
        @Suppress("DEPRECATION")
        ImageOptions().apply {
            assertEquals(null, this.preferQualityOverSpeed)
        }.merged(ImageOptions {
            preferQualityOverSpeed(true)
        }).apply {
            assertEquals(true, this.preferQualityOverSpeed)
        }.merged(ImageOptions {
            preferQualityOverSpeed(false)
        }).apply {
            assertEquals(true, this.preferQualityOverSpeed)
        }
    }

    @Test
    fun testEqualsHashCodeToString() {
        val optionsList = buildList {
            ImageOptions()
                .apply { add(this) }.newOptions {
                    colorType(Bitmap.Config.RGB_565)
                }.apply { add(this) }.let { options ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        options.newOptions {
                            colorSpace(ColorSpace.Named.ACES)
                        }.apply { add(this) }
                    } else {
                        options
                    }
                }.newOptions {
                    @Suppress("DEPRECATION")
                    preferQualityOverSpeed(true)
                }.apply { add(this) }
        }

        optionsList.forEachIndexed { index, imageOptions ->
            optionsList.forEachIndexed { index1, imageOptions1 ->
                if (index != index1) {
                    assertNotEquals(imageOptions, imageOptions1)
                    assertNotEquals(imageOptions.hashCode(), imageOptions1.hashCode())
                    assertNotEquals(imageOptions.toString(), imageOptions1.toString())
                }
            }
        }

        val optionsList2 = optionsList.map { it.newOptions() }
        optionsList.forEachIndexed { index, imageOptions ->
            assertEquals(imageOptions, optionsList2[index])
            assertEquals(imageOptions.hashCode(), optionsList2[index].hashCode())
            assertEquals(imageOptions.toString(), optionsList2[index].toString())
        }

        assertEquals(optionsList[0], optionsList[0])
        assertNotEquals(optionsList[0], Any())
        assertNotEquals(optionsList[0], null as ImageOptions?)
    }
}