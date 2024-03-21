package com.github.panpf.sketch.sample.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun rememberMimeTypeLogoMap(): Map<String, Painter> {
    val textStyle = TextStyle(color = Color.White, fontSize = 10.sp)
    val background = Background(Color(0x88000000), shape = RoundedCornerShape(20.dp))
    val paddingValues = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
    val gifTextPainter = rememberTextPainter("GIF", textStyle, paddingValues, background)
    val pngTextPainter = rememberTextPainter("PNG", textStyle, paddingValues, background)
    val jpegTextPainter = rememberTextPainter("JPEG", textStyle, paddingValues, background)
    val webpTextPainter = rememberTextPainter("WEBP", textStyle, paddingValues, background)
    val bmpTextPainter = rememberTextPainter("BMP", textStyle, paddingValues, background)
    val svgTextPainter = rememberTextPainter("SVG", textStyle, paddingValues, background)
    val heicTextPainter = rememberTextPainter("HEIC", textStyle, paddingValues, background)
    val heifTextPainter = rememberTextPainter("HEIF", textStyle, paddingValues, background)
    val mp4TextPainter = rememberTextPainter("MP4", textStyle, paddingValues, background)
    return remember(
        gifTextPainter,
        pngTextPainter,
        jpegTextPainter,
        webpTextPainter,
        bmpTextPainter,
        svgTextPainter,
        heicTextPainter,
        heifTextPainter
    ) {
        mapOf(
            "image/gif" to gifTextPainter,
            "image/png" to pngTextPainter,
            "image/jpeg" to jpegTextPainter,
            "image/webp" to webpTextPainter,
            "image/bmp" to bmpTextPainter,
            "image/svg+xml" to svgTextPainter,
            "image/heic" to heicTextPainter,
            "image/heif" to heifTextPainter,
            "video/mp4" to mp4TextPainter,
        )
    }
}