package com.github.panpf.sketch.sample.image

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.format.Formatter
import com.github.panpf.sketch.SketchCallback
import com.github.panpf.sketch.SketchException
import com.github.panpf.sketch.cache.InstallDiskCacheException
import com.github.panpf.sketch.decode.*
import com.github.panpf.sketch.request.BitmapRecycledOnDisplayException
import com.github.panpf.sketch.uri.DrawableUriModel
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.tools4a.device.Devicex
import com.tencent.bugly.crashreport.CrashReport
import java.util.*
import com.github.panpf.sketch.zoom.block.Block
import com.github.panpf.sketch.zoom.block.BlockSortException

internal class MySketchCallback(context: Application) : SketchCallback {

    private val appContext = context.applicationContext
    private var lastUploadInstallFailedTime: Long = 0
    private var lastUploadDecodeNormalImageFailedTime: Long = 0
    private var lastUploadDecodeGifImageFailedTime: Long = 0
    private var lastUploadProcessImageFailedTime: Long = 0
    private var uploadNotFoundGidSoError: Boolean = false

    override fun toString(): String {
        return "MySketchCallback"
    }

    override fun onError(e: SketchException) {
        when (e) {
            is NotFoundGifSoException ->
                if (!uploadNotFoundGidSoError) {// 每次运行只上报一次
                    uploadNotFoundGidSoError = true
                    val message = String.format(
                        "Didn't find “libpl_droidsonroids_gif.so” file, abis=%s",
                        Devicex.getSupportedAbis()
                    )
                    CrashReport.postCatchedException(Exception(message, e))
                }
            is InstallDiskCacheException -> {   // 每半小时上传一次
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUploadInstallFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
                    return
                }
                lastUploadInstallFailedTime = currentTime

                val builder = StringBuilder()

                builder.append("Sketch")
                    .append(" - ").append("InstallDiskCacheFailed")
                when (e) {
//                    is UnableCreateDirException -> builder.append(" - ").append("UnableCreateDirException")
//                    is UnableCreateFileException -> builder.append(" - ").append("UnableCreateFileException")
                    else -> builder.append(" - ").append(e.javaClass.simpleName)
                }
                builder.append(" - ").append(e.cacheDir.path)

                builder.append("\n").append("exceptionMessage: ").append(e.message)

                val sdcardState = Environment.getExternalStorageState()
                builder.append("\n").append("sdcardState: ").append(sdcardState)

                if (Environment.MEDIA_MOUNTED == sdcardState) {
                    val sdcardDir = Environment.getExternalStorageDirectory()
                    val totalBytes = SketchUtils.getTotalBytes(sdcardDir)
                    val availableBytes = SketchUtils.getAvailableBytes(sdcardDir)
                    builder.append("\n")
                        .append("sdcardSize: ")
                        .append(Formatter.formatFileSize(appContext, availableBytes))
                        .append("/")
                        .append(Formatter.formatFileSize(appContext, totalBytes))
                }

                CrashReport.postCatchedException(Exception(builder.toString(), e))
            }
            is DecodeGifException -> {
                // 其它异常每半小时上报一次
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUploadDecodeGifImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
                    return
                }
                lastUploadDecodeGifImageFailedTime = currentTime

                val builder = StringBuilder().apply {
                    append("Sketch")
                    append(" - ").append("DecodeGifImageFailed")
                    append(" - ").append(e.cause.javaClass.simpleName)
                    append(" - ").append(decodeUri(appContext, e.request.uri))

                    append("\n")
                    append("exceptionMessage: ").append(e.cause.message)

                    if (e.cause is OutOfMemoryError) {
                        append("\n")
                        append("memoryInfo: ")
                        append("maxMemory=").append(
                            Formatter.formatFileSize(
                                appContext,
                                Runtime.getRuntime().maxMemory()
                            )
                        )
                        append(", freeMemory=").append(
                            Formatter.formatFileSize(
                                appContext,
                                Runtime.getRuntime().freeMemory()
                            )
                        )
                        append(", totalMemory=").append(
                            Formatter.formatFileSize(
                                appContext,
                                Runtime.getRuntime().totalMemory()
                            )
                        )
                    }

                    append("\n")
                    append("imageInfo: ")
                    append("outWidth=").append(e.outWidth)
                    append(", outHeight=").append(e.outHeight)
                    append(", outMimeType=").append(e.outMimeType)
                }

                CrashReport.postCatchedException(Exception(builder.toString(), e.cause))
            }
            is DecodeImageException -> {
                // 每半小时上报一次
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUploadDecodeNormalImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
                    return
                }
                lastUploadDecodeNormalImageFailedTime = currentTime

                val builder = StringBuilder()

                builder.append("Sketch")
                    .append(" - ").append("DecodeNormalImageFailed")
                    .append(" - ").append(e.cause.javaClass.simpleName)
                    .append(" - ").append(decodeUri(appContext, e.request.uri))

                builder.append("\n").append("exceptionMessage: ").append(e.cause.message)

                if (e.cause is OutOfMemoryError) {
                    val maxMemory = Runtime.getRuntime().maxMemory()
                    val freeMemory = Runtime.getRuntime().freeMemory()
                    val totalMemory = Runtime.getRuntime().totalMemory()
                    val maxMemoryFormatted = Formatter.formatFileSize(this.appContext, maxMemory)
                    val freeMemoryFormatted = Formatter.formatFileSize(this.appContext, freeMemory)
                    val totalMemoryFormatted =
                        Formatter.formatFileSize(this.appContext, totalMemory)
                    builder.append("\n").append("memoryInfo: ")
                        .append("maxMemory=").append(maxMemoryFormatted)
                        .append(", freeMemory=").append(freeMemoryFormatted)
                        .append(", totalMemory=").append(totalMemoryFormatted)
                }

                builder.append("\n").append("imageInfo: ")
                    .append("outWidth=").append(e.outWidth)
                    .append(", outHeight=").append(e.outHeight)
                    .append(", outMimeType=").append(e.outMimeType)

                CrashReport.postCatchedException(Exception(builder.toString(), e.cause))
            }
            is ProcessImageException -> {
                // 每半小时上报一次
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUploadProcessImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
                    return
                }
                lastUploadProcessImageFailedTime = currentTime

                val outOfMemoryInfo = if (e.cause is OutOfMemoryError) String.format(
                    "\nmemoryState: %s",
                    systemState
                ) else ""
                CrashReport.postCatchedException(
                    Exception(
                        String.format(
                            "Sketch - %s - " +
                                    "%s" +
                                    "\n%s",
                            e.processor.toString(),
                            decodeUri(appContext, e.imageUri),
                            outOfMemoryInfo
                        ), e.cause
                    )
                )
            }
            is BlockSortException -> {
                CrashReport.postCatchedException(
                    Exception(
                        String.format(
                            "Sketch - BlockSortError - " +
                                    "%s " +
                                    "\nblocks: %s",
                            if (e.isUseLegacyMergeSort) "useLegacyMergeSort. " else "",
                            Block.blockListToString(e.blockList)
                        ), e
                    )
                )
            }
            is BitmapRecycledOnDisplayException -> {
                CrashReport.postCatchedException(
                    Exception(
                        String.format(
                            "Sketch - BitmapRecycledOnDisplay - " +
                                    "%s " +
                                    "\ndrawable: %s",
                            decodeUri(appContext, e.request.uri),
                            e.sketchDrawable.info
                        )
                    )
                )
            }
            is InBitmapDecodeException -> {
                CrashReport.postCatchedException(
                    Exception(
                        String.format(
                            "Sketch - InBitmapDecodeError - " +
                                    "%s" +
                                    "\nimage：%dx%d/%s" +
                                    "\ninSampleSize：%d" +
                                    "\ninBitmap：%dx%d, %d, %s" +
                                    "\nsystemState：%s",
                            decodeUri(appContext, e.imageUri),
                            e.imageWidth,
                            e.imageHeight,
                            e.imageMimeType,
                            e.inSampleSize,
                            e.inBitmap.width,
                            e.inBitmap.height,
                            SketchUtils.getByteCount(e.inBitmap),
                            e.inBitmap.config,
                            systemState
                        ), e.cause
                    )
                )
            }
            is DecodeRegionException -> {
                CrashReport.postCatchedException(
                    Exception(
                        String.format(
                            "Sketch - DecodeRegionError - " +
                                    "%s" +
                                    "\nimage：%dx%d/%s" +
                                    "\nsrcRect：%s" +
                                    "\ninSampleSize：%d" +
                                    "\nsrcRect：%s" +
                                    "\nsystemState：%s",
                            decodeUri(appContext, e.imageUri),
                            e.imageWidth, e.imageHeight, e.imageMimeType,
                            e.srcRect.toString(),
                            e.inSampleSize,
                            e.srcRect.toShortString(),
                            systemState
                        ), e.cause
                    )
                )
            }
        }
    }

    private fun decodeUri(context: Context, imageUri: String): String {
        val uriModel = UriModel.match(context, imageUri)
        if (uriModel != null && uriModel is DrawableUriModel) {
            try {
                val resId = uriModel.getResId(imageUri)
                return context.resources.getResourceName(resId)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        return imageUri
    }

    private val systemInfo: String
        get() = String.format(Locale.getDefault(), "%s, %d", Build.MODEL, Build.VERSION.SDK_INT)

    private val memoryInfo: String
        get() {
            val freeMemory = Formatter.formatFileSize(appContext, Runtime.getRuntime().freeMemory())
            val maxMemory = Formatter.formatFileSize(appContext, Runtime.getRuntime().maxMemory())
            return String.format("%s/%s", freeMemory, maxMemory)
        }

    private val systemState: String
        get() = String.format(Locale.getDefault(), "%s, %s", systemInfo, memoryInfo)

    companion object {
        private const val INSTALL_FAILED_RETRY_TIME_INTERVAL = 30 * 60 * 1000
    }
}
