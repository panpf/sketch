package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.request.RedisplayListener
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.image.ImageOptions
import com.github.panpf.sketch.sample.util.observeFromViewAndInit
import com.github.panpf.sketch.zoom.SketchZoomImageView

class SampleZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SketchZoomImageView(context, attrs) {

    private var disabledRedisplay: Boolean = false

    init {
        appSettingsService.correctImageOrientationEnabled.observeFromViewAndInit(this) {
            options.isCorrectImageOrientationDisabled = it == true
            if (isAttachedToWindow) {
                redisplay { _, cacheOptions ->
                    cacheOptions.isCorrectImageOrientationDisabled = it == true
                }
            }
        }
    }

    fun setOptions(@ImageOptions.Type optionsId: Int) {
        setOptions(ImageOptions.getDisplayOptions(context, optionsId))
    }

    override fun redisplay(listener: RedisplayListener?): Boolean {
        return !disabledRedisplay && super.redisplay(listener)
    }
}
