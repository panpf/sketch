package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.xiaopan.sketch.BuildConfig;
import me.xiaopan.sketch.LogType;
import me.xiaopan.sketch.Sketch;

public class Settings {
    public static final String PREFERENCE_SCROLLING_PAUSE_LOAD = "PREFERENCE_SCROLLING_PAUSE_LOAD";
    public static final String PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS = "PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS";
    public static final String PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD = "PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD";
    public static final String PREFERENCE_SHOW_IMAGE_FROM_FLAG = "PREFERENCE_SHOW_IMAGE_FROM_FLAG";
    public static final String PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD = "PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD";
    public static final String PREFERENCE_CLICK_DISPLAY_ON_FAILED = "PREFERENCE_CLICK_DISPLAY_ON_FAILED";
    public static final String PREFERENCE_CLICK_SHOW_PRESSED_STATUS = "PREFERENCE_CLICK_SHOW_PRESSED_STATUS";
    public static final String PREFERENCE_GLOBAL_DISABLE_CACHE_IN_MEMORY = "PREFERENCE_GLOBAL_DISABLE_CACHE_IN_MEMORY";
    public static final String PREFERENCE_GLOBAL_DISABLE_CACHE_IN_DISK = "PREFERENCE_GLOBAL_DISABLE_CACHE_IN_DISK";
    public static final String PREFERENCE_GLOBAL_DISABLE_BITMAP_POOL = "PREFERENCE_GLOBAL_DISABLE_BITMAP_POOL";
    public static final String PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE = "PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE";
    public static final String PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED = "PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED";
    public static final String PREFERENCE_SUPPORT_ZOOM = "PREFERENCE_SUPPORT_ZOOM";
    public static final String PREFERENCE_SUPPORT_LARGE_IMAGE = "PREFERENCE_SUPPORT_LARGE_IMAGE";
    public static final String PREFERENCE_READ_MODE = "PREFERENCE_READ_MODE";
    public static final String PREFERENCE_THUMBNAIL_MODE = "PREFERENCE_THUMBNAIL_MODE";
    public static final String PREFERENCE_LOCATION_ANIMATE = "PREFERENCE_LOCATION_ANIMATE";
    public static final String PREFERENCE_CACHE_PROCESSED_IMAGE = "PREFERENCE_CACHE_PROCESSED_IMAGE";
    public static final String PREFERENCE_PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE = "PREFERENCE_PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE";
    public static final String PREFERENCE_PLAY_GIF_ON_LIST = "PREFERENCE_PLAY_GIF_ON_LIST";
    public static final String PREFERENCE_SHOW_GIF_FLAG = "PREFERENCE_SHOW_GIF_FLAG";
    public static final String PREFERENCE_LOG_BASE = "PREFERENCE_LOG_BASE";
    public static final String PREFERENCE_LOG_REQUEST = "PREFERENCE_LOG_REQUEST";
    public static final String PREFERENCE_LOG_CACHE = "PREFERENCE_LOG_CACHE";
    public static final String PREFERENCE_LOG_ZOOM = "PREFERENCE_LOG_ZOOM";
    public static final String PREFERENCE_LOG_LARGE = "PREFERENCE_LOG_LARGE";
    public static final String PREFERENCE_LOG_TIME = "PREFERENCE_LOG_TIME";

    public static boolean getBoolean(Context context, @Key String key) {
        boolean defaultValue = false;
        if (PREFERENCE_CLICK_SHOW_PRESSED_STATUS.equals(key)
                || PREFERENCE_SHOW_IMAGE_FROM_FLAG.equals(key)
                || PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(key)
                || PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD.equals(key)
                || PREFERENCE_CLICK_DISPLAY_ON_FAILED.equals(key)
                || PREFERENCE_CACHE_PROCESSED_IMAGE.equals(key)
                || PREFERENCE_LOCATION_ANIMATE.equals(key)
                || PREFERENCE_THUMBNAIL_MODE.equals(key)
                || PREFERENCE_SUPPORT_ZOOM.equals(key)
                || PREFERENCE_READ_MODE.equals(key)
                || PREFERENCE_SUPPORT_LARGE_IMAGE.equals(key)
                || PREFERENCE_PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE.equals(key)
                || PREFERENCE_SHOW_GIF_FLAG.equals(key)) {
            defaultValue = true;
        } else if(PREFERENCE_LOG_REQUEST.equals(key)){
            defaultValue = BuildConfig.DEBUG;
        }
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, @Key String key, boolean newValue) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, newValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }

        if (PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(key)) {
            Sketch.with(context).getConfiguration().setMobileNetworkGlobalPauseDownload(newValue);
        } else if (PREFERENCE_GLOBAL_DISABLE_CACHE_IN_DISK.equals(key)) {
            Sketch.with(context).getConfiguration().getDiskCache().setDisabled(newValue);
        } else if (PREFERENCE_GLOBAL_DISABLE_CACHE_IN_MEMORY.equals(key)) {
            Sketch.with(context).getConfiguration().getMemoryCache().setDisabled(newValue);
        } else if (PREFERENCE_GLOBAL_DISABLE_BITMAP_POOL.equals(key)) {
            Sketch.with(context).getConfiguration().getBitmapPool().setDisabled(newValue);
        } else if (PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE.equals(key)) {
            Sketch.with(context).getConfiguration().setGlobalLowQualityImage(newValue);
        } else if (PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED.equals(key)) {
            Sketch.with(context).getConfiguration().setGlobalInPreferQualityOverSpeed(newValue);
        } else if (PREFERENCE_LOG_BASE.equals(key)) {
            LogType.BASE.setEnabled(newValue);
        } else if (PREFERENCE_LOG_REQUEST.equals(key)) {
            LogType.REQUEST.setEnabled(newValue);
        } else if (PREFERENCE_LOG_TIME.equals(key)) {
            LogType.TIME.setEnabled(newValue);
        } else if (PREFERENCE_LOG_CACHE.equals(key)) {
            LogType.CACHE.setEnabled(newValue);
        } else if (PREFERENCE_LOG_ZOOM.equals(key)) {
            LogType.ZOOM.setEnabled(newValue);
        } else if (PREFERENCE_LOG_LARGE.equals(key)) {
            LogType.LARGE.setEnabled(newValue);
        }
    }

    @StringDef({
            PREFERENCE_SCROLLING_PAUSE_LOAD,
            PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS,
            PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD,
            PREFERENCE_SHOW_IMAGE_FROM_FLAG,
            PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD,
            PREFERENCE_CLICK_DISPLAY_ON_FAILED,
            PREFERENCE_GLOBAL_DISABLE_CACHE_IN_MEMORY,
            PREFERENCE_GLOBAL_DISABLE_CACHE_IN_DISK,
            PREFERENCE_GLOBAL_DISABLE_BITMAP_POOL,
            PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE,
            PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED,
            PREFERENCE_SUPPORT_ZOOM,
            PREFERENCE_SUPPORT_LARGE_IMAGE,
            PREFERENCE_READ_MODE,
            PREFERENCE_THUMBNAIL_MODE,
            PREFERENCE_THUMBNAIL_MODE,
            PREFERENCE_LOCATION_ANIMATE,
            PREFERENCE_CACHE_PROCESSED_IMAGE,
            PREFERENCE_CLICK_SHOW_PRESSED_STATUS,
            PREFERENCE_PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE,
            PREFERENCE_PLAY_GIF_ON_LIST,
            PREFERENCE_SHOW_GIF_FLAG,
            PREFERENCE_LOG_BASE,
            PREFERENCE_LOG_REQUEST,
            PREFERENCE_LOG_CACHE,
            PREFERENCE_LOG_ZOOM,
            PREFERENCE_LOG_LARGE,
            PREFERENCE_LOG_TIME,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Key {

    }
}
