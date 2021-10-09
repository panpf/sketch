package me.panpf.sketch.sample.widget

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View
import com.github.panpf.liveevent.Listener
import me.panpf.sketch.datasource.DataSource
import me.panpf.sketch.decode.ImageOrientationCorrector
import me.panpf.sketch.drawable.SketchDrawable
import me.panpf.sketch.drawable.SketchLoadingDrawable
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable
import me.panpf.sketch.request.RedisplayListener
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.AppEvents
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.uri.GetDataSourceException
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import me.panpf.sketch.zoom.SketchZoomImageView
import java.io.IOException

class SampleZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SketchZoomImageView(context, attrs) {

    private var disabledRedisplay: Boolean = false
    private val longClickShowDrawableInfoListener: LongClickShowDrawableInfoListener =
        LongClickShowDrawableInfoListener()

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

    fun showInfo(activity: Activity) {
        longClickShowDrawableInfoListener.showInfo(activity)
    }

    private inner class LongClickShowDrawableInfoListener : OnLongClickListener {
        override fun onLongClick(v: View): Boolean {
            if (v.context is Activity) {
                showInfo(v.context as Activity)
                return true
            }
            return false
        }

        fun showInfo(activity: Activity) {
            val builder = AlertDialog.Builder(activity)

            val drawable = SketchUtils.getLastDrawable(drawable)

            val imageInfo: String
            if (drawable is SketchLoadingDrawable) {
                imageInfo = "Image is loading, please wait later"
            } else if (drawable is SketchDrawable) {
                imageInfo = assembleImageInfo(drawable, drawable as SketchDrawable)
            } else {
                imageInfo = "Unknown source image"
            }
            builder.setMessage(imageInfo)

            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        private fun assembleImageInfo(drawable: Drawable, sketchDrawable: SketchDrawable): String {
            val messageBuilder = StringBuilder()

            messageBuilder.append(sketchDrawable.key)

            val uriModel = UriModel.match(context, sketchDrawable.uri!!)
            var dataSource: DataSource? = null
            if (uriModel != null) {
                try {
                    dataSource = uriModel.getDataSource(context, sketchDrawable.uri!!, null)
                } catch (e: GetDataSourceException) {
                    e.printStackTrace()
                }

            }
            var imageLength: Long = 0
            try {
                imageLength = if (dataSource != null) dataSource.length else 0
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val needDiskSpace =
                if (imageLength > 0) Formatter.formatFileSize(context, imageLength) else "Unknown"

            val previewDrawableByteCount = sketchDrawable.byteCount
            val pixelByteCount: Int
            if (drawable is SketchShapeBitmapDrawable) {
                val bitmap = drawable.bitmapDrawable.bitmap
                pixelByteCount = previewDrawableByteCount / bitmap.width / bitmap.height
            } else {
                pixelByteCount =
                    previewDrawableByteCount / drawable.intrinsicWidth / drawable.intrinsicHeight
            }
            val originImageByteCount =
                sketchDrawable.originWidth * sketchDrawable.originHeight * pixelByteCount
            val needMemory = Formatter.formatFileSize(context, originImageByteCount.toLong())
            val mimeType = sketchDrawable.mimeType

            messageBuilder.append("\n")
            messageBuilder.append("\n")
            messageBuilder.append("Original: ")
                .append(sketchDrawable.originWidth).append("x").append(sketchDrawable.originHeight)
                .append("/")
                .append(if (mimeType != null && mimeType.startsWith("image/")) mimeType.substring(6) else "Unknown")
                .append("/").append(needDiskSpace)

            messageBuilder.append("\n                ")
            messageBuilder.append(ImageOrientationCorrector.toName(sketchDrawable.exifOrientation))
                .append("/").append(needMemory)

            messageBuilder.append("\n")
            messageBuilder.append("\n")
            messageBuilder.append("Preview: ")
                .append(drawable.intrinsicWidth).append("x").append(drawable.intrinsicHeight)
                .append("/").append(sketchDrawable.bitmapConfig)
                .append("/")
                .append(Formatter.formatFileSize(context, previewDrawableByteCount.toLong()))

            return messageBuilder.toString()
        }
    }
}
