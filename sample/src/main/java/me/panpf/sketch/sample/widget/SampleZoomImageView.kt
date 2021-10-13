package me.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.liveevent.Listener
import me.panpf.sketch.request.RedisplayListener
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.AppEvents
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.zoom.SketchZoomImageView

class SampleZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SketchZoomImageView(context, attrs) {

    private var disabledRedisplay: Boolean = false

    private val appConfigChangeListener =
        Listener<AppConfig.Key> { key -> key?.let { onConfigChange(it) } }
    private val cacheCleanListener = Listener<Int> { key -> key?.let { onCacheClean() } }

    init {
        onConfigChange(AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        AppEvents.appConfigChangedEvent.listenForever(appConfigChangeListener)
        AppEvents.cacheCleanEvent.listenForever(cacheCleanListener)
    }

    override fun onReadyDisplay(uri: String) {
        super.onReadyDisplay(uri)

        disabledRedisplay = true
        onConfigChange(AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION)
        disabledRedisplay = false
    }

    fun setOptions(@ImageOptions.Type optionsId: Int) {
        setOptions(ImageOptions.getDisplayOptions(context, optionsId))
    }

    override fun redisplay(listener: RedisplayListener?): Boolean {
        return !disabledRedisplay && super.redisplay(listener)
    }

    private fun onConfigChange(key: AppConfig.Key) {
        when (key) {
            AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION -> {
                val correctImageOrientationDisabled = AppConfig.getBoolean(context, key)
                options.isCorrectImageOrientationDisabled = correctImageOrientationDisabled

                redisplay { _, cacheOptions ->
                    cacheOptions.isCorrectImageOrientationDisabled = correctImageOrientationDisabled
                }
            }
            else -> {
            }
        }
    }

    private fun onCacheClean() {
        redisplay(null)
    }

    override fun onDetachedFromWindow() {
        AppEvents.appConfigChangedEvent.removeListener(appConfigChangeListener)
        AppEvents.cacheCleanEvent.removeListener(cacheCleanListener)
        super.onDetachedFromWindow()
    }
}
