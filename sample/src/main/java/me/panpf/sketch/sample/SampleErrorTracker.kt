package me.panpf.sketch.sample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.text.format.Formatter
import com.tencent.bugly.crashreport.CrashReport
import me.panpf.sketch.ErrorTracker
import me.panpf.sketch.drawable.SketchDrawable
import me.panpf.sketch.process.ImageProcessor
import me.panpf.sketch.request.DisplayRequest
import me.panpf.sketch.request.LoadRequest
import me.panpf.sketch.uri.DrawableUriModel
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import me.panpf.sketch.util.UnableCreateDirException
import me.panpf.sketch.util.UnableCreateFileException
import me.panpf.sketch.zoom.block.Block
import java.io.File
import java.util.*

internal class SampleErrorTracker(context: Context) : ErrorTracker(context) {

    private val appContext = context.applicationContext
    private var lastUploadInstallFailedTime: Long = 0
    private var lastUploadDecodeNormalImageFailedTime: Long = 0
    private var lastUploadDecodeGifImageFailedTime: Long = 0
    private var lastUploadProcessImageFailedTime: Long = 0
    private var uploadNotFoundGidSoError: Boolean = false

    override fun toString(): String {
        return "SampleErrorTracker"
    }

    override fun onNotFoundGifSoError(e: Throwable) {
        super.onNotFoundGifSoError(e)

        // 每次运行只上报一次
        if (uploadNotFoundGidSoError) {
            return
        }
        uploadNotFoundGidSoError = true

        val abis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Arrays.toString(Build.SUPPORTED_ABIS)
        } else {
            Arrays.toString(arrayOf(Build.CPU_ABI, Build.CPU_ABI2))
        }
        val message = String.format("Didn't find “libpl_droidsonroids_gif.so” file, abis=%s", abis)

        CrashReport.postCatchedException(Exception(message, e))
    }

    override fun onDecodeGifImageError(throwable: Throwable, request: LoadRequest, outWidth: Int, outHeight: Int, outMimeType: String) {
        super.onDecodeGifImageError(throwable, request, outWidth, outHeight, outMimeType)

        // 其它异常每半小时上报一次
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUploadDecodeGifImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return
        }
        lastUploadDecodeGifImageFailedTime = currentTime

        val builder = StringBuilder().apply {
            append("Sketch")
            append(" - ").append("DecodeGifImageFailed")
            append(" - ").append(throwable.javaClass.simpleName)
            append(" - ").append(decodeUri(appContext, request.uri))

            append("\n")
            append("exceptionMessage: ").append(throwable.message)

            if (throwable is OutOfMemoryError) {
                append("\n")
                append("memoryInfo: ")
                append("maxMemory=").append(Formatter.formatFileSize(appContext, Runtime.getRuntime().maxMemory()))
                append(", freeMemory=").append(Formatter.formatFileSize(appContext, Runtime.getRuntime().freeMemory()))
                append(", totalMemory=").append(Formatter.formatFileSize(appContext, Runtime.getRuntime().totalMemory()))
            }

            append("\n")
            append("imageInfo: ")
            append("outWidth=").append(outWidth)
            append(", outHeight=").append(outHeight)
            append(", outMimeType=").append(outMimeType)
        }

        CrashReport.postCatchedException(Exception(builder.toString(), throwable))
    }

    override fun onDecodeNormalImageError(throwable: Throwable, request: LoadRequest, outWidth: Int, outHeight: Int, outMimeType: String) {
        super.onDecodeNormalImageError(throwable, request, outWidth, outHeight, outMimeType)

        // 每半小时上报一次
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUploadDecodeNormalImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return
        }
        lastUploadDecodeNormalImageFailedTime = currentTime

        val builder = StringBuilder()

        builder.append("Sketch")
                .append(" - ").append("DecodeNormalImageFailed")
                .append(" - ").append(throwable.javaClass.simpleName)
                .append(" - ").append(decodeUri(appContext, request.uri))

        builder.append("\n").append("exceptionMessage: ").append(throwable.message)

        if (throwable is OutOfMemoryError) {
            val maxMemory = Runtime.getRuntime().maxMemory()
            val freeMemory = Runtime.getRuntime().freeMemory()
            val totalMemory = Runtime.getRuntime().totalMemory()
            val maxMemoryFormatted = Formatter.formatFileSize(this.appContext, maxMemory)
            val freeMemoryFormatted = Formatter.formatFileSize(this.appContext, freeMemory)
            val totalMemoryFormatted = Formatter.formatFileSize(this.appContext, totalMemory)
            builder.append("\n").append("memoryInfo: ")
                    .append("maxMemory=").append(maxMemoryFormatted)
                    .append(", freeMemory=").append(freeMemoryFormatted)
                    .append(", totalMemory=").append(totalMemoryFormatted)
        }

        builder.append("\n").append("imageInfo: ")
                .append("outWidth=").append(outWidth)
                .append(", outHeight=").append(outHeight)
                .append(", outMimeType=").append(outMimeType)

        CrashReport.postCatchedException(Exception(builder.toString(), throwable))
    }

    override fun onInstallDiskCacheError(e: Exception, cacheDir: File) {
        super.onInstallDiskCacheError(e, cacheDir)

        // 每半小时上传一次
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUploadInstallFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return
        }
        lastUploadInstallFailedTime = currentTime

        val builder = StringBuilder()

        builder.append("Sketch")
                .append(" - ").append("InstallDiskCacheFailed")
        when (e) {
            is UnableCreateDirException -> builder.append(" - ").append("UnableCreateDirException")
            is UnableCreateFileException -> builder.append(" - ").append("UnableCreateFileException")
            else -> builder.append(" - ").append(e.javaClass.simpleName)
        }
        builder.append(" - ").append(cacheDir.path)

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

    override fun onProcessImageError(throwable: Throwable, imageUri: String, processor: ImageProcessor) {
        super.onProcessImageError(throwable, imageUri, processor)

        // 每半小时上报一次
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUploadProcessImageFailedTime < INSTALL_FAILED_RETRY_TIME_INTERVAL) {
            return
        }
        lastUploadProcessImageFailedTime = currentTime

        val outOfMemoryInfo = if (throwable is OutOfMemoryError) String.format("\nmemoryState: %s", systemState) else ""
        CrashReport.postCatchedException(Exception(String.format(
                "Sketch - %s - " +
                        "%s" +
                        "\n%s",
                processor.toString(),
                decodeUri(appContext, imageUri),
                outOfMemoryInfo
        ), throwable))
    }

    override fun onBlockSortError(e: IllegalArgumentException, blockList: List<Block>, useLegacyMergeSort: Boolean) {
        super.onBlockSortError(e, blockList, useLegacyMergeSort)

        CrashReport.postCatchedException(Exception(String.format(
                "Sketch - BlockSortError - " +
                        "%s " +
                        "\nblocks: %s",
                if (useLegacyMergeSort) "useLegacyMergeSort. " else "",
                SketchUtils.blockListToString(blockList)
        ), e))
    }

    override fun onBitmapRecycledOnDisplay(request: DisplayRequest, refDrawable: SketchDrawable) {
        super.onBitmapRecycledOnDisplay(request, refDrawable)

        CrashReport.postCatchedException(Exception(String.format(
                "Sketch - BitmapRecycledOnDisplay - " +
                        "%s " +
                        "\ndrawable: %s",
                decodeUri(appContext, request.uri),
                refDrawable.info)))
    }

    override fun onInBitmapDecodeError(imageUri: String, imageWidth: Int, imageHeight: Int, imageMimeType: String,
                                       throwable: Throwable, inSampleSize: Int, inBitmap: Bitmap) {
        super.onInBitmapDecodeError(imageUri, imageWidth, imageHeight, imageMimeType, throwable, inSampleSize, inBitmap)

        CrashReport.postCatchedException(Exception(String.format(
                "Sketch - InBitmapDecodeError - " +
                        "%s" +
                        "\nimage：%dx%d/%s" +
                        "\ninSampleSize：%d" +
                        "\ninBitmap：%dx%d, %d, %s" +
                        "\nsystemState：%s",
                decodeUri(appContext, imageUri),
                imageWidth, imageHeight, imageMimeType,
                inSampleSize,
                inBitmap.width, inBitmap.height, SketchUtils.getByteCount(inBitmap), inBitmap.config,
                systemState
        ), throwable))
    }

    override fun onDecodeRegionError(imageUri: String, imageWidth: Int, imageHeight: Int, imageMimeType: String,
                                     throwable: Throwable, srcRect: Rect, inSampleSize: Int) {
        super.onDecodeRegionError(imageUri, imageWidth, imageHeight, imageMimeType, throwable, srcRect, inSampleSize)

        CrashReport.postCatchedException(Exception(String.format(
                "Sketch - DecodeRegionError - " +
                        "%s" +
                        "\nimage：%dx%d/%s" +
                        "\nsrcRect：%s" +
                        "\ninSampleSize：%d" +
                        "\nsrcRect：%s" +
                        "\nsystemState：%s",
                decodeUri(appContext, imageUri),
                imageWidth, imageHeight, imageMimeType,
                srcRect.toString(),
                inSampleSize,
                srcRect.toShortString(),
                systemState
        ), throwable))
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
