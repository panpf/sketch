package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.lifecycle.Observer
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.internal.setMimeTypeLogoWithDrawable
import com.github.panpf.sketch.internal.showDataFrom
import com.github.panpf.sketch.internal.showMaskProgressIndicator
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.tools4a.dimen.ktx.dp2px

class MyListImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SketchImageView(context, attrs) {

    private val mimeTypeLogoMap by lazy {
        val newLogoDrawable: (String) -> Drawable = {
            TextDrawable.builder()
                .beginConfig()
                .width(((it.length+1) * 6).dp2px)
                .height(16.dp2px)
                .fontSize(9.dp2px)
                .bold()
                .textColor(Color.WHITE)
                .endConfig()
                .buildRoundRect(it, Color.parseColor("#88000000"), 10.dp2px)
        }
        mapOf(
            "image/gif" to newLogoDrawable("GIF"),
            "image/png" to newLogoDrawable("PNG"),
            "image/jpeg" to newLogoDrawable("JPEG"),
            "image/webp" to newLogoDrawable("WEBP"),
            "image/bmp" to newLogoDrawable("BMP"),
            "image/heic" to newLogoDrawable("HEIC"),
            "image/heif" to newLogoDrawable("HEIF")
        )
    }

    private val progressIndicatorObserver = Observer<Boolean> {
        showMaskProgressIndicator(it == true)
    }
    private val mimeTypeLogoObserver = Observer<Boolean> {
        setMimeTypeLogoWithDrawable(
            mimeTypeIconMap = if (it == true) mimeTypeLogoMap else null,
            margin = 4.dp2px
        )
    }
    private val dataFromObserver = Observer<Boolean> {
        showDataFrom(it == true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        context.appSettingsService.showProgressIndicatorInList.observeForever(
            progressIndicatorObserver
        )
        context.appSettingsService.showMimeTypeLogoInLIst.observeForever(mimeTypeLogoObserver)
        context.appSettingsService.showDataFrom.observeForever(dataFromObserver)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.appSettingsService.showProgressIndicatorInList.removeObserver(
            progressIndicatorObserver
        )
        context.appSettingsService.showMimeTypeLogoInLIst.removeObserver(mimeTypeLogoObserver)
        context.appSettingsService.showDataFrom.removeObserver(dataFromObserver)
    }
}