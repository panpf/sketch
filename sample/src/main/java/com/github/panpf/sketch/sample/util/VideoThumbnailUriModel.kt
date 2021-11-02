package com.github.panpf.sketch.sample.util

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.uri.AbsBitmapDiskCacheUriModel
import com.github.panpf.sketch.uri.GetDataSourceException
import com.github.panpf.sketch.util.SketchUtils
import wseemann.media.FFmpegMediaMetadataRetriever

class VideoThumbnailUriModel : AbsBitmapDiskCacheUriModel() {

    companion object {
        const val SCHEME = "video.thumbnail://"
        private const val NAME = "VideoThumbnailUriModel"

        @JvmStatic
        fun makeUri(filePath: String): String {
            return SCHEME + filePath
        }
    }

    override fun match(uri: String): Boolean {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME)
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "video.thumbnail:///sdcard/test.mp4"，就会返回 "/sdcard/test.mp4"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "video.thumbnail:///sdcard/test.mp4"，就会返回 "/sdcard/test.mp4"
     */
    override fun getUriContent(uri: String): String {
        return if (match(uri)) uri.substring(SCHEME.length) else uri
    }

    override fun getDiskCacheKey(uri: String): String {
        return SketchUtils.createFileUriDiskCacheKey(uri, getUriContent(uri))
    }

    @Throws(GetDataSourceException::class)
    override fun getContent(context: Context, uri: String): Bitmap {
        val mediaMetadataRetriever = FFmpegMediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(getUriContent(uri))
        return try {
            readVideoThumbnail(context, uri, mediaMetadataRetriever)
        } finally {
            mediaMetadataRetriever.release()
        }
    }

    @Throws(GetDataSourceException::class)
    private fun readVideoThumbnail(
        context: Context,
        uri: String,
        mediaMetadataRetriever: FFmpegMediaMetadataRetriever
    ): Bitmap {
        val metadata = mediaMetadataRetriever.metadata
        val videoWidth = metadata.getInt(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val videoHeight = metadata.getInt(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)

        // 限制读取的帧的尺寸
        val sizeCalculator = Sketch.with(context).configuration.sizeCalculator
        val maxSize = sizeCalculator.getDefaultImageMaxSize(context)
        val inSampleSize = sizeCalculator.calculateInSampleSize(
            videoWidth,
            videoHeight,
            maxSize.width,
            maxSize.height,
            false
        )
        val finalWidth = SketchUtils.calculateSamplingSize(videoWidth, inSampleSize)
        val finalHeight = SketchUtils.calculateSamplingSize(videoHeight, inSampleSize)

        // 大于30分钟的一般是电影或电视剧，这类视频开头一般都一样显示出来也没有意义，所以显示中间的部分
        var timeUs: Long
        val duration = metadata.getLong(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
        val second = (duration / 1000).toInt()
        val minute = second / 60
        timeUs = if (minute > 30) {
            duration / 2 * 1000
        } else {
            -1
        }
        var frameBitmap =
            mediaMetadataRetriever.getScaledFrameAtTime(timeUs, finalWidth, finalHeight)

        // 偶尔会有读取中间帧失败的情况，这时候换到三分之一处再读一次
        if (frameBitmap == null && timeUs != -1L) {
            timeUs = duration / 3 * 1000
            frameBitmap =
                mediaMetadataRetriever.getScaledFrameAtTime(timeUs, finalWidth, finalHeight)
        }
        if (frameBitmap == null || frameBitmap.isRecycled) {
            val cause = String.format("Video thumbnail bitmap invalid. %s", uri)
            SLog.em(NAME, cause)
            throw GetDataSourceException(cause)
        }
        return frameBitmap
    }
}