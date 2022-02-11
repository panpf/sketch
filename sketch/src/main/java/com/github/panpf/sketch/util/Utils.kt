package com.github.panpf.sketch.util

import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Looper
import android.os.Process
import android.view.View
import android.webkit.MimeTypeMap
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.drawable.SketchCountDrawable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

internal fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun requiredMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This method must be executed in the UI thread"
    }
}

fun requiredWorkThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "This method must be executed in the work thread"
    }
}

fun Context?.getLifecycle(): Lifecycle? {
    var context: Context? = this
    while (true) {
        when (context) {
            is LifecycleOwner -> return context.lifecycle
            !is ContextWrapper -> return null
            else -> context = context.baseContext
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Deferred<T>.getCompletedOrNull(): T? {
    return try {
        getCompleted()
    } catch (_: Throwable) {
        null
    }
}

val View.isAttachedToWindowCompat: Boolean
    get() = ViewCompat.isAttachedToWindow(this)

/** Remove and re-add the observer to ensure all its lifecycle callbacks are invoked. */
@MainThread
internal fun Lifecycle.removeAndAddObserver(observer: LifecycleObserver) {
    removeObserver(observer)
    addObserver(observer)
}

/**
 * 获取 [Bitmap] 占用内存大小，单位字节
 */
val Bitmap.byteCountCompat: Int
    get() {
        // bitmap.isRecycled()过滤很关键，在4.4以及以下版本当bitmap已回收时调用其getAllocationByteCount()方法将直接崩溃
        return when {
            this.isRecycled -> 0
            VERSION.SDK_INT >= VERSION_CODES.KITKAT -> this.allocationByteCount
            else -> this.byteCount
        }
    }

val Bitmap.safeConfig: Bitmap.Config
    get() = config ?: Bitmap.Config.ARGB_8888

fun Bitmap.toInfoString(): String = "Bitmap(width=${width}, height=${height}, config=$config)"
fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"

/**
 * 根据宽、高和配置计算所占用的字节数
 */
fun computeByteCount(width: Int, height: Int, config: Bitmap.Config?): Int {
    return width * height * config.getBytesPerPixel()
}

/**
 * 获取指定配置单个像素所占的字节数
 */
fun Bitmap.Config?.getBytesPerPixel(): Int {
    // A bitmap by decoding a gif has null "config" in certain environments.
    val config = this ?: Bitmap.Config.ARGB_8888
    return when {
        config == Bitmap.Config.ALPHA_8 -> 1
        config == Bitmap.Config.RGB_565 || config == Bitmap.Config.ARGB_4444 -> 2
        config == Bitmap.Config.ARGB_8888 -> 4
        VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.RGBA_F16 -> 8
        else -> 4
    }
}

/**
 * 根据指定的 [Bitmap] 配置获取合适的压缩格式
 */
val Bitmap.Config?.getCompressFormat: CompressFormat
    get() = if (this == Bitmap.Config.RGB_565) CompressFormat.JPEG else CompressFormat.PNG

/**
 * 获取修剪级别的名称
 */
fun trimLevelName(level: Int): String = when (level) {
    ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "COMPLETE"
    ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "MODERATE"
    ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "BACKGROUND"
    ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI_HIDDEN"
    ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "RUNNING_CRITICAL"
    ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "RUNNING_LOW"
    ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "RUNNING_MODERATE"
    else -> "UNKNOWN"
}

fun Any.toHexString(): String =
    Integer.toHexString(this.hashCode())

/**
 * Convert null and [Bitmap.Config.HARDWARE] configs to [Bitmap.Config.ARGB_8888].
 */
fun Bitmap.Config?.toSoftware(): Bitmap.Config {
    return if (this == null || isHardware) Bitmap.Config.ARGB_8888 else this
}

val Bitmap.Config.isHardware: Boolean
    get() = VERSION.SDK_INT >= 26 && this == Bitmap.Config.HARDWARE

internal fun fileNameCompatibilityMultiProcess(context: Context, file: File): File {
    val pid = Process.myPid()
    val processName = (context.getSystemService(Context.ACTIVITY_SERVICE)
        .asOrNull<ActivityManager>()?.runningAppProcesses)?.find {
            it.pid == pid
        }?.processName ?: return file

    val packageName = context.packageName
    val multiProcessNameSuffix = if (processName == packageName) {
        null
    } else if (
        processName.length > packageName.length
        && processName.startsWith(packageName)
        && processName[packageName.length] == ':'
    ) {
        processName.substring(packageName.length + 1)
    } else {
        null
    }

    return if (multiProcessNameSuffix != null) {
        File(file.parent, "${file.name}-$multiProcessNameSuffix")
    } else {
        file
    }
}

fun Float.format(newScale: Int): Float {
    val b = BigDecimal(this.toDouble())
    return b.setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()
}

fun Drawable.getLastDrawable(): Drawable? =
    when (val drawable = this) {
        is LayerDrawable -> {
            val layerCount = drawable.numberOfLayers
            if (layerCount > 0) {
                drawable.getDrawable(layerCount - 1).getLastDrawable()
            } else {
                null
            }
        }
        is CrossfadeDrawable -> {
            drawable.end?.getLastDrawable()
        }
        else -> {
            drawable
        }
    }

fun Drawable.findLastCountDrawable(): SketchCountDrawable? =
    getLastDrawable() as? SketchCountDrawable

internal fun View.fixedWidth(): Int? {
    val layoutParams = layoutParams?.takeIf { it.width > 0 } ?: return null
    return (layoutParams.width - paddingLeft - paddingRight).takeIf { it > 0 }
        ?: throw IllegalArgumentException("Invalid view width. Because 'layoutParams.width - paddingLeft - paddingRight' execute result is less than or equal to zero")
}

internal fun View.fixedHeight(): Int? {
    val layoutParams = layoutParams?.takeIf { it.height > 0 } ?: return null
    return (layoutParams.height - paddingTop - paddingBottom).takeIf { it > 0 }
        ?: throw IllegalArgumentException("Invalid view height. Because 'layoutParams.height - paddingTop - paddingBottom' execute result is less than or equal to zero")
}

/**
 * 生成文件 uri 的磁盘缓存 key，关键在于要在 uri 的后面加上文件的修改时间来作为缓存 key，这样当文件发生变化时能及时更新缓存
 *
 * @param uri      文件 uri
 * @param filePath 文件路径，要获取文件的修改时间
 * @return 文件 uri 的磁盘缓存 key
 */
fun createFileUriDiskCacheKey(uri: String, filePath: String): String {
    val file = File(filePath)
    return if (file.exists()) {
        val lastModifyTime = file.lastModified()
        // 这里必须用 uri 连接修改时间，不能用 filePath，因为使用 filePath 的话当同一个文件可以用于多种 uri 时会导致磁盘缓存错乱
        "$uri.$lastModifyTime"
    } else {
        uri
    }
}

/**
 * Returns the formatted file length that can be displayed, up to EB
 *
 * @receiver              File size
 * @param decimalPlacesLength   Keep a few decimal places
 * @param decimalPlacesFillZero Use 0 instead when the number of decimal places is insufficient
 * @param compact               If true, returns 150KB, otherwise returns 150 KB
 * @return For example: 300 B, 150.25 KB, 500.46 MB, 300 GB
 */
internal fun Long.formatFileSize(
    decimalPlacesLength: Int = 2,
    decimalPlacesFillZero: Boolean = false,
    compact: Boolean = true,
): String {
    // Multiplied by 999 to avoid 1000 KB, 1000 MB
    // Why is appendSuffix required to be true when calling the format method, because DecimalFormat encounters '#.##EB' and throws an exception 'IllegalArgumentException: Malformed exponential pattern "#.##EB"'
    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
    val finalFileSize = Math.max(this, 0)
    return if (finalFileSize <= 999) {
        finalFileSize.toString() + if (compact) "B" else " B"
    } else {
        val value: Double
        val suffix: String
        @Suppress("CascadeIf")
        if (finalFileSize <= 1024L * 999) {
            value = (finalFileSize / 1024f).toDouble()
            suffix = if (compact) "KB" else " KB"
        } else if (finalFileSize <= 1024L * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f).toDouble()
            suffix = if (compact) "MB" else " MB"
        } else if (finalFileSize <= 1024L * 1024 * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "GB" else " GB"
        } else if (finalFileSize <= 1024L * 1024 * 1024 * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "TB" else " TB"
        } else if (finalFileSize <= 1024L * 1024 * 1024 * 1024 * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "PB" else " PB"
        } else {
            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "EB" else " EB"
        }
        val buffString = StringBuilder()
        buffString.append("#")
        if (decimalPlacesLength > 0) {
            buffString.append(".")
            for (w in 0 until decimalPlacesLength) {
                buffString.append(if (decimalPlacesFillZero) "0" else "#")
            }
        }
        val format = DecimalFormat(buffString.toString())
        format.roundingMode = RoundingMode.HALF_UP
        format.format(value).toString() + suffix
    }
}

/**
 * Read apk file icon. Although the PackageManager will cache the icon, the bitmap returned by this method every time
 *
 * @param context         [Context]
 * @param apkFilePath     Apk file path
 * @param lowQualityImage If set true use ARGB_4444 create bitmap, KITKAT is above is invalid
 * @param logName         Print log is used identify log type
 * @param bitmapPool      Try to find Reusable bitmap from bitmapPool
 */
@Throws(IOException::class)
fun readApkIcon(
    context: Context,
    apkFilePath: String,
    lowQualityImage: Boolean,
    bitmapPool: BitmapPool
): Bitmap {
    val packageManager = context.packageManager
    val packageInfo =
        packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES)
            ?: throw IOException("getPackageArchiveInfo return null. $apkFilePath")
    packageInfo.applicationInfo.sourceDir = apkFilePath
    packageInfo.applicationInfo.publicSourceDir = apkFilePath
    val drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo)
    return drawableToBitmap(drawable, lowQualityImage, bitmapPool)
}

/**
 * Drawable into Bitmap. Each time a new bitmap is drawn
 */
fun drawableToBitmap(
    drawable: Drawable,
    lowQualityImage: Boolean,
    bitmapPool: BitmapPool
): Bitmap {
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val config = if (lowQualityImage) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
    val bitmap: Bitmap =
        bitmapPool.getOrCreate(drawable.intrinsicWidth, drawable.intrinsicHeight, config)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return bitmap
}

/**
 * Modified from [MimeTypeMap.getFileExtensionFromUrl] to be more permissive
 * with special characters.
 */
fun MimeTypeMap.getMimeTypeFromUrl(url: String?): String? {
    if (url.isNullOrBlank()) {
        return null
    }

    val extension = url
        .substringBeforeLast('#') // Strip the fragment.
        .substringBeforeLast('?') // Strip the query.
        .substringAfterLast('/') // Get the last path segment.
        .substringAfterLast('.', missingDelimiterValue = "") // Get the file extension.

    return getMimeTypeFromExtension(extension)
}