package com.github.panpf.sketch.sample.ui.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ToFormatStringTest {

    @Test
    fun test1() {
        assertEquals(
            expected = """
                PainterImage(
                    painter=ResizeAnimatablePainter(
                        painter=DrawableAnimatablePainter(
                            drawable=AnimatableDrawable(
                                drawable=ScaledAnimatedImageDrawable(
                                    drawable=AnimatedImageDrawable(240x240),
                                    fitScale=true
                                )
                            )
                        ),
                        size=Size(346.0x346.0),
                        scale=CENTER_CROP
                    ),
                    shareable=false
                )
            """.trimIndent(),
            actual = "PainterImage(painter=ResizeAnimatablePainter(painter=DrawableAnimatablePainter(drawable=AnimatableDrawable(drawable=ScaledAnimatedImageDrawable(drawable=AnimatedImageDrawable(240x240), fitScale=true))), size=Size(346.0x346.0), scale=CENTER_CROP), shareable=false)"
                .toFormattedString()
        )
    }

    @Test
    fun test2() {
        assertEquals(
            expected = """
                AndroidDrawableImage(
                    drawable=ResizeDrawable(
                        drawable=BitmapDrawable(
                            AndroidBitmap@a3a8b76(
                                233x350,
                                ARGB_8888,
                                SRGB
                            )
                        ),
                        size=690x690,
                        scale=CENTER_CROP
                    ),
                    shareable=true
                )
            """.trimIndent(),
            actual = "AndroidDrawableImage(drawable=ResizeDrawable(drawable=BitmapDrawable(AndroidBitmap@a3a8b76(233x350,ARGB_8888,SRGB)), size=690x690, scale=CENTER_CROP), shareable=true)"
                .toFormattedString()
        )
    }
}