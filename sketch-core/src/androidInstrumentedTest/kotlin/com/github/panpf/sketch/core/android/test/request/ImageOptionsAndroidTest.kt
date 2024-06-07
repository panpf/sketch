package com.github.panpf.sketch.core.android.test.request

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.isNotEmpty
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.uriEmpty
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.uriEmptyError
import com.github.panpf.sketch.util.ColorDrawableEqualizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageOptionsAndroidTest {

    @Test
    fun testIsEmpty() {
        ImageOptions().apply {
            assertTrue(this.isEmpty())
            assertFalse(this.isNotEmpty())
            assertNull(this.bitmapConfig)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertNull(this.colorSpace)
            }
            @Suppress("DEPRECATION")
            assertNull(this.preferQualityOverSpeed)
        }

        ImageOptions {
            bitmapConfig(Bitmap.Config.ALPHA_8)
        }.apply {
            assertFalse(this.isEmpty())
            assertTrue(this.isNotEmpty())
            assertNotNull(this.bitmapConfig)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ImageOptions {
                colorSpace(ColorSpace.Named.BT709)
            }.apply {
                assertFalse(this.isEmpty())
                assertTrue(this.isNotEmpty())
                assertNotNull(this.colorSpace)
            }
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
    fun testBitmapConfig() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(bitmapConfig)
            }

            bitmapConfig(BitmapConfig(Bitmap.Config.RGB_565))
            build().apply {
                assertEquals(BitmapConfig(Bitmap.Config.RGB_565), bitmapConfig)
            }

            bitmapConfig(Bitmap.Config.ARGB_8888)
            build().apply {
                assertEquals(BitmapConfig(Bitmap.Config.ARGB_8888), bitmapConfig)
            }

            bitmapConfig(BitmapConfig.LowQuality)
            build().apply {
                assertEquals(BitmapConfig.LowQuality, bitmapConfig)
            }

            bitmapConfig(BitmapConfig.HighQuality)
            build().apply {
                assertEquals(BitmapConfig.HighQuality, bitmapConfig)
            }

            bitmapConfig(null)
            build().apply {
                assertNull(bitmapConfig)
            }
        }
    }

    @Test
    fun testColorSpace() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        ImageOptions.Builder().apply {
            build().apply {
                assertNull(colorSpace)
            }

            colorSpace(ColorSpace.Named.ACES)
            build().apply {
                assertEquals(ColorSpace.get(ColorSpace.Named.ACES), colorSpace)
            }

            colorSpace(ColorSpace.Named.BT709)
            build().apply {
                assertEquals(ColorSpace.get(ColorSpace.Named.BT709), colorSpace)
            }

            colorSpace(null)
            build().apply {
                assertNull(colorSpace)
            }
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
                assertEquals(false, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(null)
            build().apply {
                assertNull(preferQualityOverSpeed)
            }
        }
    }

    @Test
    fun testMerged() {
        ImageOptions().apply {
            assertEquals(null, this.bitmapConfig)
        }.merged(ImageOptions {
            bitmapConfig(Bitmap.Config.ARGB_8888)
        }).apply {
            assertEquals(BitmapConfig(Bitmap.Config.ARGB_8888), this.bitmapConfig)
        }.merged(ImageOptions {
            bitmapConfig(Bitmap.Config.RGB_565)
        }).apply {
            assertEquals(BitmapConfig(Bitmap.Config.ARGB_8888), this.bitmapConfig)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ImageOptions().apply {
                assertEquals(null, this.colorSpace)
            }.merged(ImageOptions {
                colorSpace(ColorSpace.Named.BT709)
            }).apply {
                assertEquals(ColorSpace.get(ColorSpace.Named.BT709), this.colorSpace)
            }.merged(ImageOptions {
                colorSpace(ColorSpace.Named.ACES)
            }).apply {
                assertEquals(ColorSpace.get(ColorSpace.Named.BT709), this.colorSpace)
            }
        }

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
                    bitmapConfig(Bitmap.Config.RGB_565)
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

            placeholder(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                assertEquals(true, placeholder is DrawableStateImage)
            }

            placeholder(android.R.drawable.bottom_bar)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.bottom_bar),
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
    fun testUriEmpty() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(uriEmpty)
            }

            uriEmpty(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                assertEquals(IntColorDrawableStateImage(Color.BLUE), uriEmpty)
            }

            uriEmpty(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                assertEquals(true, uriEmpty is DrawableStateImage)
            }

            uriEmpty(android.R.drawable.bottom_bar)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.bottom_bar),
                    uriEmpty
                )
            }

            uriEmpty(null)
            build().apply {
                assertNull(uriEmpty)
            }
        }
    }

    @Test
    fun testError() {
        ImageOptions.Builder().apply {
            build().apply {
                assertNull(error)
            }

            error(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                assertEquals(
                    ErrorStateImage(IntColorDrawableStateImage(Color.BLUE)),
                    error
                )
            }

            error(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                assertEquals(true, error is ErrorStateImage)
            }

            error(android.R.drawable.bottom_bar)
            build().apply {
                assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                    error
                )
            }

            error(android.R.drawable.bottom_bar) {
                uriEmptyError(android.R.drawable.alert_dark_frame)
            }
            build().apply {
                assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)) {
                        uriEmptyError(android.R.drawable.alert_dark_frame)
                    },
                    error
                )
            }

            error()
            build().apply {
                assertNull(error)
            }

            error {
                uriEmptyError(android.R.drawable.btn_dialog)
            }
            build().apply {
                assertNotNull(error)
            }
        }
    }
}