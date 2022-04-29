package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.github.panpf.liveevent.Listener
import com.github.panpf.liveevent.MediatorLiveEvent
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.util.observeFromView
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.viewability.removeDataFromLogo
import com.github.panpf.sketch.viewability.removeProgressIndicator
import com.github.panpf.sketch.viewability.showDataFromLogo
import com.github.panpf.sketch.viewability.showMaskProgressIndicator
import com.github.panpf.sketch.viewability.showMimeTypeLogoWithDrawable
import com.github.panpf.tools4a.dimen.ktx.dp2px

class MyListImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : MyImageView(context, attrs, defStyle) {

    private val mimeTypeLogoMap by lazy {
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
        mapOf(
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
    private val mediatorLiveData = MediatorLiveEvent<Any>()

    init {
        mediatorLiveData.observeFromView(this) {
            SketchUtils.restart(this@MyListImageView)
        }

        context.appSettingsService.apply {
            mediatorLiveData.apply {
                val observer = Listener<Any> {
                    postValue(1)
                }
                addSource(disabledAnimatedImageInList.liveEvent, observer)
                addSource(pauseLoadWhenScrollInList.liveEvent, observer)
                addSource(saveCellularTrafficInList.liveEvent, observer)
            }

            showProgressIndicatorInList.observeFromView(this@MyListImageView) {
                if (it == true) {
                    showMaskProgressIndicator()
                } else {
                    removeProgressIndicator()
                }
            }
            showMimeTypeLogoInLIst.observeFromView(this@MyListImageView) {
                showMimeTypeLogoWithDrawable(
                    mimeTypeIconMap = if (it == true) mimeTypeLogoMap else null,
                    margin = 4.dp2px
                )
            }
            showDataFromLogo.observeFromView(this@MyListImageView) {
                if (it == true) {
                    showDataFromLogo()
                } else {
                    removeDataFromLogo()
                }
            }
        }
    }
}