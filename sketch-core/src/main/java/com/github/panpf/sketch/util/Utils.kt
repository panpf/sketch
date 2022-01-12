package com.github.panpf.sketch.util

import android.annotation.TargetApi
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES10
import android.opengl.GLES20
import android.os.Build
import android.os.Looper
import android.view.View
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.ImageType
import com.github.panpf.sketch.cache.BitmapPool
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

internal fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

internal fun Context?.getLifecycle(): Lifecycle? {
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

fun ImageType.supportBitmapRegionDecoder(): Boolean =
    this == ImageType.JPEG || this == ImageType.PNG || this == ImageType.WEBP

/**
 * 获取 [Bitmap] 占用内存大小，单位字节
 */
val Bitmap.byteCountCompat: Int
    get() {
        // bitmap.isRecycled()过滤很关键，在4.4以及以下版本当bitmap已回收时调用其getAllocationByteCount()方法将直接崩溃
        return when {
            this.isRecycled -> 0
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> this.allocationByteCount
            else -> this.byteCount
        }
    }

val Bitmap.safeConfig: Bitmap.Config
    get() = config ?: Bitmap.Config.ARGB_8888

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
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.RGBA_F16 -> 8
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
fun getTrimLevelName(level: Int): String = when (level) {
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
 * 计算 inSampleSize
 *
 * @param imageWidth            原始宽
 * @param imageHeight           原始高
 * @param targetWidth           目标宽
 * @param targetHeight          目标高
 * @return 合适的 inSampleSize
 */
fun calculateInSampleSize(
    imageWidth: Int,
    imageHeight: Int,
    targetWidth: Int,
    targetHeight: Int
): Int {
    // OpenGL 所允许的最大尺寸
    val maxSize = openGLMaxTextureSize

    // 计算 inSampleSize 的时候将 target size 稍微放大一点儿，这样能避免尺寸很接近的图片读取时尺寸被缩小
    val targetSizeScale = 1.1f
    val newTargetWidth = (targetWidth * targetSizeScale).toInt().coerceAtMost(maxSize)
    val newTargetHeight = (targetHeight * targetSizeScale).toInt().coerceAtMost(maxSize)

    // 如果目标宽高都小于等于 0 或目标宽高都大于等于原始尺寸，就别计算了
    if ((newTargetWidth <= 0 && newTargetHeight <= 0) || (newTargetWidth >= imageWidth && newTargetHeight >= imageHeight)) {
        return 1
    }

    var inSampleSize = 1
    when {
        newTargetWidth <= 0 -> {
            // 目标宽小于等于 0 时，只要高度满足要求即可
            while (calculateSamplingSize(imageHeight, inSampleSize) > newTargetHeight) {
                inSampleSize *= 2
            }
        }
        newTargetHeight <= 0 -> {
            // 目标高小于等于 0 时，只要宽度满足要求即可
            while (calculateSamplingSize(imageWidth, inSampleSize) > newTargetWidth) {
                inSampleSize *= 2
            }
        }
        else -> {
            // 首先限制像素数不能超过目标宽高的像素数
            val maxPixels = (newTargetWidth * newTargetHeight).toLong()
            while (
                calculateSamplingSize(imageWidth, inSampleSize)
                * calculateSamplingSize(imageHeight, inSampleSize) > maxPixels
            ) {
                inSampleSize *= 2
            }

            // 然后限制宽高不能大于 OpenGL 所允许的最大尺寸
            while ((calculateSamplingSize(imageWidth, inSampleSize) > maxSize)
                || (calculateSamplingSize(imageHeight, inSampleSize) > maxSize)
            ) {
                inSampleSize *= 2
            }
        }
    }
    return inSampleSize
}

fun calculateSamplingSize(value1: Int, inSampleSize: Int): Int {
    return ceil((value1 / inSampleSize.toFloat()).toDouble()).toInt()
}

fun calculateSamplingSizeForRegion(value1: Int, inSampleSize: Int): Int {
    return floor((value1 / inSampleSize.toFloat()).toDouble()).toInt()
}

fun Float.format(newScale: Int): Float {
    val b = BigDecimal(this.toDouble())
    return b.setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()
}

fun Drawable.getLastDrawable(): Drawable? {
    val drawable = this
    if (drawable is LayerDrawable) {
        val layerCount = drawable.numberOfLayers
        return if (layerCount > 0) {
            drawable.getDrawable(layerCount - 1).getLastDrawable()
        } else {
            null
        }
    }
    return drawable
}

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
        bitmapPool.getOrMake(drawable.intrinsicWidth, drawable.intrinsicHeight, config)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return bitmap
}


/**
 * OpenGL 所允许的图片的最大尺寸(单边长)
 */
val openGLMaxTextureSize: Int by lazy {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            openGLMaxTextureSizeJB1
        } else {
            openGLMaxTextureSizeBase
        }
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }.takeIf { it != 0 } ?: 4096
}

// TROUBLE! No config found.

// To make a context current, which we will need later,
// you need a rendering surface, even if you don't actually plan to render.
// To satisfy this requirement, create a small offscreen (Pbuffer) surface:

// Next, create the context:

// Ready to make the context current now:

// If all of the above succeeded (error checking was omitted), you can make your OpenGL calls now:

// Once you're all done, you can tear down everything:
// Then get a hold of the default display, and initialize.
// This could get more complex if you have to deal with devices that could have multiple displays,
// but will be sufficient for a typical phone/tablet:
@get:TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
private val openGLMaxTextureSizeJB1: Int
    // Next, we need to find a config. Since we won't use this context for rendering,
    // the exact attributes aren't very critical:
    get() {
        // Then get a hold of the default display, and initialize.
        // This could get more complex if you have to deal with devices that could have multiple displays,
        // but will be sufficient for a typical phone/tablet:
        val dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val vers = IntArray(2)
        EGL14.eglInitialize(dpy, vers, 0, vers, 1)

        // Next, we need to find a config. Since we won't use this context for rendering,
        // the exact attributes aren't very critical:
        val configAttr = intArrayOf(
            EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
            EGL14.EGL_LEVEL, 0,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfig = IntArray(1)
        EGL14.eglChooseConfig(
            dpy, configAttr, 0,
            configs, 0, 1, numConfig, 0
        )
        if (numConfig[0] == 0) {
            // TROUBLE! No config found.
        }
        val config = configs[0]

        // To make a context current, which we will need later,
        // you need a rendering surface, even if you don't actually plan to render.
        // To satisfy this requirement, create a small offscreen (Pbuffer) surface:
        val surfAttr = intArrayOf(
            EGL14.EGL_WIDTH, 64,
            EGL14.EGL_HEIGHT, 64,
            EGL14.EGL_NONE
        )
        val surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0)

        // Next, create the context:
        val ctxAttrib = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        val ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0)

        // Ready to make the context current now:
        EGL14.eglMakeCurrent(dpy, surf, surf, ctx)

        // If all of the above succeeded (error checking was omitted), you can make your OpenGL calls now:
        val maxSize = IntArray(1)
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0)

        // Once you're all done, you can tear down everything:
        EGL14.eglMakeCurrent(
            dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_CONTEXT
        )
        EGL14.eglDestroySurface(dpy, surf)
        EGL14.eglDestroyContext(dpy, ctx)
        EGL14.eglTerminate(dpy)
        return maxSize[0]
    }// missing in EGL10// TROUBLE! No config found.

// In JELLY_BEAN will collapse
private val openGLMaxTextureSizeBase: Int
    get() {
        // In JELLY_BEAN will collapse
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            return 0
        }
        val egl = EGLContext.getEGL() as EGL10
        val dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val vers = IntArray(2)
        egl.eglInitialize(dpy, vers)
        val configAttr = intArrayOf(
            EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
            EGL10.EGL_LEVEL, 0,
            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
            EGL10.EGL_NONE
        )
        val configs = arrayOfNulls<javax.microedition.khronos.egl.EGLConfig>(1)
        val numConfig = IntArray(1)
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig)
        if (numConfig[0] == 0) {
            // TROUBLE! No config found.
        }
        val config = configs[0]
        val surfAttr = intArrayOf(
            EGL10.EGL_WIDTH, 64,
            EGL10.EGL_HEIGHT, 64,
            EGL10.EGL_NONE
        )
        val surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr)
        val EGL_CONTEXT_CLIENT_VERSION = 0x3098 // missing in EGL10
        val ctxAttrib = intArrayOf(
            EGL_CONTEXT_CLIENT_VERSION, 1,
            EGL10.EGL_NONE
        )
        val ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib)
        egl.eglMakeCurrent(dpy, surf, surf, ctx)
        val maxSize = IntArray(1)
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0)
        egl.eglMakeCurrent(
            dpy,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_CONTEXT
        )
        egl.eglDestroySurface(dpy, surf)
        egl.eglDestroyContext(dpy, ctx)
        egl.eglTerminate(dpy)
        return maxSize[0]
    }