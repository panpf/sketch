package me.panpf.sketch.cache;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import me.panpf.sketch.SLog;

/**
 * A calculator that tries to intelligently determine cache sizes for a given device based on some constants and the
 * devices screen density, width, and height.
 */
public class MemorySizeCalculator {
    // Visible for testing.
    static final int BYTES_PER_ARGB_8888_PIXEL = 4;
    static final int MEMORY_CACHE_TARGET_SCREENS = 3;
    static final int BITMAP_POOL_TARGET_SCREENS = 3;
    static final float MAX_SIZE_MULTIPLIER = 0.4f;
    static final float LOW_MEMORY_MAX_SIZE_MULTIPLIER = 0.33f;
    private static final String NAME = "MemorySizeCalculator";
    private final int bitmapPoolSize;
    private final int memoryCacheSize;

    // Visible for testing.
    public MemorySizeCalculator(Context context) {
        context = context.getApplicationContext();
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int maxSize = getMaxSize(activityManager);

        final int screenSize = displayMetrics.widthPixels * displayMetrics.heightPixels * BYTES_PER_ARGB_8888_PIXEL;

        final int targetPoolSize = screenSize * BITMAP_POOL_TARGET_SCREENS;
        final int targetMemoryCacheSize = screenSize * MEMORY_CACHE_TARGET_SCREENS;

        if (targetMemoryCacheSize + targetPoolSize <= maxSize) {
            memoryCacheSize = targetMemoryCacheSize;
            bitmapPoolSize = targetPoolSize;
        } else {
            int part = Math.round((float) maxSize / (BITMAP_POOL_TARGET_SCREENS + MEMORY_CACHE_TARGET_SCREENS));
            memoryCacheSize = part * MEMORY_CACHE_TARGET_SCREENS;
            bitmapPoolSize = part * BITMAP_POOL_TARGET_SCREENS;
        }

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            SLog.d(NAME, "Calculated memory cache size: %s pool size: %s memory class limited? %s max size: %s memoryClass: %d isLowMemoryDevice: %s",
                    toMb(context, memoryCacheSize), toMb(context, bitmapPoolSize), targetMemoryCacheSize + targetPoolSize > maxSize, toMb(context, maxSize),
                    activityManager.getMemoryClass(), isLowMemoryDevice(activityManager));
        }
    }

    private static int getMaxSize(ActivityManager activityManager) {
        final int memoryClassBytes = activityManager.getMemoryClass() * 1024 * 1024;
        final boolean isLowMemoryDevice = isLowMemoryDevice(activityManager);
        return Math.round(memoryClassBytes
                * (isLowMemoryDevice ? LOW_MEMORY_MAX_SIZE_MULTIPLIER : MAX_SIZE_MULTIPLIER));
    }

    private static String toMb(Context context, int bytes) {
        return Formatter.formatFileSize(context, bytes);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean isLowMemoryDevice(ActivityManager activityManager) {
        final int sdkInt = Build.VERSION.SDK_INT;
        return sdkInt >= Build.VERSION_CODES.KITKAT && activityManager.isLowRamDevice();
    }

    /**
     * Returns the recommended memory cache size for the device it is run on in bytes.
     */
    public int getMemoryCacheSize() {
        return memoryCacheSize;
    }

    /**
     * Returns the recommended bitmap pool size for the device it is run on in bytes.
     */
    public int getBitmapPoolSize() {
        return bitmapPoolSize;
    }
}
