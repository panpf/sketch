package com.github.panpf.sketch.sample.ui.common

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.sample.widget.TextDrawable
import com.github.panpf.tools4a.dimen.ktx.dp2px

fun createMimeTypeLogoMap(): Map<String, Drawable> {
    val newLogoDrawable: (String) -> Drawable = {
        TextDrawable.builder()
            .beginConfig()
            .width(((it.length + 1) * 6).dp2px)
            .height(16.dp2px)
            .fontSize(9.dp2px)
            .bold()
            .textColor(Color.WHITE)
            .endConfig()
            .buildRoundRect(it, Color.parseColor("#88000000"), 10.dp2px)
    }
    return mapOf(
        "image/gif" to newLogoDrawable("GIF"),
        "image/png" to newLogoDrawable("PNG"),
        "image/jpeg" to newLogoDrawable("JPEG"),
        "image/webp" to newLogoDrawable("WEBP"),
        "image/bmp" to newLogoDrawable("BMP"),
        "image/svg+xml" to newLogoDrawable("SVG"),
        "image/heic" to newLogoDrawable("HEIC"),
        "image/heif" to newLogoDrawable("HEIF"),
    )
}