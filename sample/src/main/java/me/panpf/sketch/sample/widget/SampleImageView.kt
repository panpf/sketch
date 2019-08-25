package me.panpf.sketch.sample.widget

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View
import me.panpf.javaxkt.util.requireNotNull
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.datasource.DataSource
import me.panpf.sketch.decode.ImageOrientationCorrector
import me.panpf.sketch.drawable.SketchDrawable
import me.panpf.sketch.drawable.SketchLoadingDrawable
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable
import me.panpf.sketch.request.RedisplayListener
import me.panpf.sketch.request.Resize
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.event.AppConfigChangedEvent
import me.panpf.sketch.sample.event.CacheCleanEvent
import me.panpf.sketch.uri.GetDataSourceException
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.greenrobot.eventbus.Subscribe
import java.io.IOException

class SampleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SketchImageView(context, attrs) {
    var page: Page? = null
    private var disabledRedisplay: Boolean = false
    private val longClickShowDrawableInfoListener: LongClickShowDrawableInfoListener = LongClickShowDrawableInfoListener()

    init {
        onEvent(AppConfigChangedEvent(AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            EventBus.getDefault().register(this)
        } catch (e: EventBusException) {
            e.printStackTrace()
        }
    }

    override fun onReadyDisplay(uriModel: UriModel?) {
        super.onReadyDisplay(uriModel)

        disabledRedisplay = true
        onEvent(AppConfigChangedEvent(AppConfig.Key.SHOW_GIF_FLAG))
        onEvent(AppConfigChangedEvent(AppConfig.Key.SHOW_IMAGE_FROM_FLAG))
        onEvent(AppConfigChangedEvent(AppConfig.Key.CLICK_SHOW_PRESSED_STATUS))
        onEvent(AppConfigChangedEvent(AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS))
        onEvent(AppConfigChangedEvent(AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD))
        onEvent(AppConfigChangedEvent(AppConfig.Key.CLICK_RETRY_ON_FAILED))
        onEvent(AppConfigChangedEvent(AppConfig.Key.CLICK_PLAY_GIF))
        onEvent(AppConfigChangedEvent(AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION))
        onEvent(AppConfigChangedEvent(AppConfig.Key.PLAY_GIF_ON_LIST))
        onEvent(AppConfigChangedEvent(AppConfig.Key.THUMBNAIL_MODE))
        onEvent(AppConfigChangedEvent(AppConfig.Key.CACHE_PROCESSED_IMAGE))
        disabledRedisplay = false
    }

    fun setOptions(@ImageOptions.Type optionsId: Int) {
        setOptions(ImageOptions.getDisplayOptions(context, optionsId))
    }

    override fun redisplay(listener: RedisplayListener?): Boolean {
        return !disabledRedisplay && super.redisplay(listener)
    }

    @Subscribe
    fun onEvent(event: AppConfigChangedEvent) {
        when (event.key) {
            AppConfig.Key.SHOW_GIF_FLAG -> if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setShowGifFlagEnabled(if (AppConfig.getBoolean(context, AppConfig.Key.SHOW_GIF_FLAG)) R.drawable.ic_gif else 0)
            }
            AppConfig.Key.SHOW_IMAGE_FROM_FLAG -> isShowImageFromEnabled = AppConfig.getBoolean(context, event.key)
            AppConfig.Key.CLICK_SHOW_PRESSED_STATUS -> if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                isShowPressedStatusEnabled = AppConfig.getBoolean(context, event.key)
            }
            AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS -> if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                isShowDownloadProgressEnabled = AppConfig.getBoolean(context, event.key)
            }
            AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD -> if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                isClickRetryOnPauseDownloadEnabled = AppConfig.getBoolean(context, event.key)
            }
            AppConfig.Key.CLICK_RETRY_ON_FAILED -> if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                isClickRetryOnDisplayErrorEnabled = AppConfig.getBoolean(context, event.key)
            }
            AppConfig.Key.CLICK_PLAY_GIF -> if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST || page == Page.APP_LIST) {
                setClickPlayGifEnabled(if (AppConfig.getBoolean(context, event.key)) R.drawable.ic_play else 0)
            }
            AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD -> {
                redisplay(null)
            }
            AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK -> {
                redisplay(null)
            }
            AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL -> {
                redisplay(null)
            }
            AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY -> {
                redisplay(null)
            }
            AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION -> {
                val correctImageOrientationDisabled = AppConfig.getBoolean(context, event.key)
                options.isCorrectImageOrientationDisabled = correctImageOrientationDisabled

                redisplay { _, cacheOptions -> cacheOptions.isCorrectImageOrientationDisabled = correctImageOrientationDisabled }
            }
            AppConfig.Key.PLAY_GIF_ON_LIST -> if (page == Page.PHOTO_LIST || page == Page.SEARCH_LIST || page == Page.UNSPLASH_LIST) {
                val playGifOnList = AppConfig.getBoolean(context, event.key)
                options.isDecodeGifImage = playGifOnList

                redisplay { _, cacheOptions -> cacheOptions.isDecodeGifImage = playGifOnList }
            }
            AppConfig.Key.THUMBNAIL_MODE -> if (page == Page.PHOTO_LIST) {
                val thumbnailMode = AppConfig.getBoolean(context, event.key)
                options.isThumbnailMode = thumbnailMode
                if (thumbnailMode) {
                    options.resize = Resize.byViewFixedSize()
                } else {
                    options.resize = null
                }

                redisplay { _, cacheOptions ->
                    cacheOptions.isThumbnailMode = thumbnailMode
                    if (thumbnailMode) {
                        cacheOptions.resize = Resize.byViewFixedSize()
                    } else {
                        cacheOptions.resize = null
                    }
                }
            }
            AppConfig.Key.CACHE_PROCESSED_IMAGE -> {
                val cacheProcessedImageInDisk = AppConfig.getBoolean(context, event.key)
                options.isCacheProcessedImageInDisk = cacheProcessedImageInDisk

                redisplay { _, cacheOptions -> cacheOptions.isCacheProcessedImageInDisk = cacheProcessedImageInDisk }
            }
            AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO -> {
                onLongClickListener = if (AppConfig.getBoolean(context, AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO)) {
                    longClickShowDrawableInfoListener
                } else {
                    null
                }
            }
            else -> {
            }
        }
    }

    @Suppress("unused")
    @Subscribe
    fun onEvent(@Suppress("UNUSED_PARAMETER") event: CacheCleanEvent) {
        redisplay(null)
    }

    override fun onDetachedFromWindow() {
        EventBus.getDefault().unregister(this)
        super.onDetachedFromWindow()
    }

    fun showInfo(activity: Activity) {
        longClickShowDrawableInfoListener.showInfo(activity)
    }

    enum class Page {
        PHOTO_LIST, UNSPLASH_LIST, SEARCH_LIST, APP_LIST, DETAIL, DEMO
    }

    private inner class LongClickShowDrawableInfoListener : View.OnLongClickListener {
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

            val uriModel = UriModel.match(context, sketchDrawable.uri.requireNotNull())
            var dataSource: DataSource? = null
            if (uriModel != null) {
                try {
                    dataSource = uriModel.getDataSource(context, sketchDrawable.uri.requireNotNull(), null)
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

            val needDiskSpace = if (imageLength > 0) Formatter.formatFileSize(context, imageLength) else "Unknown"

            val previewDrawableByteCount = sketchDrawable.byteCount
            val pixelByteCount: Int
            if (drawable is SketchShapeBitmapDrawable) {
                val bitmap = drawable.bitmapDrawable.bitmap
                pixelByteCount = previewDrawableByteCount / bitmap.width / bitmap.height
            } else {
                pixelByteCount = previewDrawableByteCount / drawable.intrinsicWidth / drawable.intrinsicHeight
            }
            val originImageByteCount = sketchDrawable.originWidth * sketchDrawable.originHeight * pixelByteCount
            val needMemory = Formatter.formatFileSize(context, originImageByteCount.toLong())
            val mimeType = sketchDrawable.mimeType

            messageBuilder.append("\n")
            messageBuilder.append("\n")
            messageBuilder.append("Original: ")
                    .append(sketchDrawable.originWidth).append("x").append(sketchDrawable.originHeight)
                    .append("/").append(if (mimeType != null && mimeType.startsWith("image/")) mimeType.substring(6) else "Unknown")
                    .append("/").append(needDiskSpace)

            messageBuilder.append("\n                ")
            messageBuilder.append(ImageOrientationCorrector.toName(sketchDrawable.exifOrientation))
                    .append("/").append(needMemory)

            messageBuilder.append("\n")
            messageBuilder.append("\n")
            messageBuilder.append("Preview: ")
                    .append(drawable.intrinsicWidth).append("x").append(drawable.intrinsicHeight)
                    .append("/").append(sketchDrawable.bitmapConfig)
                    .append("/").append(Formatter.formatFileSize(context, previewDrawableByteCount.toLong()))

            return messageBuilder.toString()
        }
    }
}
