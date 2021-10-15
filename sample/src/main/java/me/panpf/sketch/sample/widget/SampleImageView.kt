package me.panpf.sketch.sample.widget

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View.OnLongClickListener
import com.github.panpf.liveevent.Listener
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.datasource.DataSource
import me.panpf.sketch.decode.ImageOrientationCorrector
import me.panpf.sketch.drawable.SketchDrawable
import me.panpf.sketch.drawable.SketchLoadingDrawable
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable
import me.panpf.sketch.request.RedisplayListener
import me.panpf.sketch.request.Resize
import me.panpf.sketch.sample.AppEvents
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.appSettingsService
import me.panpf.sketch.sample.image.ImageOptions
import me.panpf.sketch.sample.util.observe
import me.panpf.sketch.uri.GetDataSourceException
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import java.io.IOException

class SampleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SketchImageView(context, attrs) {

    private var disabledRedisplay: Boolean = false
    private val cacheCleanListener = Listener<Int> { key -> key?.let { onCacheClean() } }

    init {
        setShowGifFlagEnabled(R.drawable.ic_gif)
        isClickRetryOnPauseDownloadEnabled = true
        isClickRetryOnDisplayErrorEnabled = true
        appSettingsService.showImageFromFlagEnabled.observe(this) {
            isShowImageFromEnabled = it == true
        }
        appSettingsService.correctImageOrientationEnabled.observe(this) {
            options.isCorrectImageOrientationDisabled = it == true
            if (isAttachedToWindow) {
                redisplay { _, cacheOptions ->
                    cacheOptions.isCorrectImageOrientationDisabled = it == true
                }
            }
        }
        appSettingsService.thumbnailModeEnabled.observe(this) {
            val thumbnailMode = it == true
            options.isThumbnailMode = thumbnailMode
            if (thumbnailMode) {
                options.resize = Resize.byViewFixedSize()
            } else {
                options.resize = null
            }
            if (isAttachedToWindow) {
                redisplay { _, cacheOptions ->
                    cacheOptions.isThumbnailMode = thumbnailMode
                    if (thumbnailMode) {
                        cacheOptions.resize = Resize.byViewFixedSize()
                    } else {
                        cacheOptions.resize = null
                    }
                }
            }
        }
        appSettingsService.cacheProcessedImageEnabled.observe(this) {
            val cacheProcessedImageInDisk = it == true
            options.isCacheProcessedImageInDisk = cacheProcessedImageInDisk
            redisplay { _, cacheOptions ->
                cacheOptions.isCacheProcessedImageInDisk = cacheProcessedImageInDisk
            }
        }
        onLongClickListener = OnLongClickListener {
            if (it.context is Activity) {
                showInfo(it.context as Activity)
            }
            true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        AppEvents.cacheCleanEvent.listenForever(cacheCleanListener)
    }

    fun setOptions(@ImageOptions.Type optionsId: Int) {
        setOptions(ImageOptions.getDisplayOptions(context, optionsId))
    }

    override fun redisplay(listener: RedisplayListener?): Boolean {
        return !disabledRedisplay && super.redisplay(listener)
    }

    private fun onCacheClean() {
        redisplay(null)
    }

    override fun onDetachedFromWindow() {
        AppEvents.cacheCleanEvent.removeListener(cacheCleanListener)
        super.onDetachedFromWindow()
    }

    private fun showInfo(activity: Activity) {
        val imageInfo: String = when (val drawable = SketchUtils.getLastDrawable(drawable)) {
            is SketchLoadingDrawable -> "Image is loading, please wait later"
            is SketchDrawable -> assembleImageInfo(drawable, drawable as SketchDrawable)
            else -> "Unknown source image"
        }
        AlertDialog.Builder(activity).apply {
            setMessage(imageInfo)
            setNegativeButton("Cancel", null)
        }.show()
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
            imageLength = dataSource?.length ?: 0
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val needDiskSpace =
            if (imageLength > 0) Formatter.formatFileSize(context, imageLength) else "Unknown"

        val previewDrawableByteCount = sketchDrawable.byteCount
        val pixelByteCount: Int = if (drawable is SketchShapeBitmapDrawable) {
            val bitmap = drawable.bitmapDrawable.bitmap
            previewDrawableByteCount / bitmap.width / bitmap.height
        } else {
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
