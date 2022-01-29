package com.github.panpf.sketch.cache

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.formatFileSize
import kotlin.math.roundToInt

/**
 * A calculator that tries to intelligently determine cache sizes for a given device based on some constants and the
 * devices screen density, width, and height.
 */
class MemorySizeCalculator(context: Context, val logger: Logger) {
    /**
     * Returns the recommended bitmap pool size for the device it is run on in bytes.
     */
    var bitmapPoolSize = 0

    /**
     * Returns the recommended memory cache size for the device it is run on in bytes.
     */
    var memoryCacheSize = 0

    // Visible for testing.
    init {
        val appContext = context.applicationContext
        val activityManager =
            appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val displayMetrics = appContext.resources.displayMetrics
        val maxSize = getMaxSize(activityManager)
        val screenSize =
            displayMetrics.widthPixels * displayMetrics.heightPixels * BYTES_PER_ARGB_8888_PIXEL
        val targetPoolSize = screenSize * BITMAP_POOL_TARGET_SCREENS
        val targetMemoryCacheSize = screenSize * MEMORY_CACHE_TARGET_SCREENS
        if (targetMemoryCacheSize + targetPoolSize <= maxSize) {
            memoryCacheSize = targetMemoryCacheSize
            bitmapPoolSize = targetPoolSize
        } else {
            val part =
                (maxSize.toFloat() / (BITMAP_POOL_TARGET_SCREENS + MEMORY_CACHE_TARGET_SCREENS)).roundToInt()
            memoryCacheSize = part * MEMORY_CACHE_TARGET_SCREENS
            bitmapPoolSize = part * BITMAP_POOL_TARGET_SCREENS
        }
        logger.d("MemorySizeCalculator") {
            "Calculated memory cache size: %s pool size: %s memory class limited? %s max size: %s memoryClass: %d isLowMemoryDevice: %s".format(
                memoryCacheSize.toLong().formatFileSize(),
                bitmapPoolSize.toLong().formatFileSize(),
                targetMemoryCacheSize + targetPoolSize > maxSize,
                maxSize.toLong().formatFileSize(),
                activityManager.memoryClass,
                isLowMemoryDevice(activityManager)
            )
        }
    }

    companion object {
        // Visible for testing.
        private const val BYTES_PER_ARGB_8888_PIXEL = 4
        private const val MEMORY_CACHE_TARGET_SCREENS = 3
        private const val BITMAP_POOL_TARGET_SCREENS = 3
        private const val MAX_SIZE_MULTIPLIER = 0.4f
        private const val LOW_MEMORY_MAX_SIZE_MULTIPLIER = 0.33f
        private const val NAME = "MemorySizeCalculator"
        private fun getMaxSize(activityManager: ActivityManager?): Int {
            val memoryClassBytes =
                if (activityManager != null) activityManager.memoryClass * 1024 * 1024 else 100
            val isLowMemoryDevice = isLowMemoryDevice(activityManager)
            return (memoryClassBytes
                    * if (isLowMemoryDevice) LOW_MEMORY_MAX_SIZE_MULTIPLIER else MAX_SIZE_MULTIPLIER).roundToInt()
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        private fun isLowMemoryDevice(activityManager: ActivityManager?): Boolean {
            val sdkInt = Build.VERSION.SDK_INT
            return activityManager == null || sdkInt >= Build.VERSION_CODES.KITKAT && activityManager.isLowRamDevice
        }
    }
}