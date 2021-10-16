package me.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import me.panpf.sketch.request.RedisplayListener
import me.panpf.sketch.sample.appSettingsService
import me.panpf.sketch.sample.image.ImageOptions
import me.panpf.sketch.sample.util.observe
import me.panpf.sketch.zoom.SketchZoomImageView

class SampleZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SketchZoomImageView(context, attrs) {

    private var disabledRedisplay: Boolean = false

    init {
        appSettingsService.correctImageOrientationEnabled.observe(this) {
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
