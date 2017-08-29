package me.xiaopan.sketchsample;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Initializer;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.util.AppConfig;
import me.xiaopan.sketchsample.util.VideoThumbnailPreprocessor;
import me.xiaopan.sketchsample.util.XpkIconPreprocessor;

public class SketchInitializer implements Initializer {

    private Context context;
    private Configuration configuration;

    @Override
    public void onInitialize(Context context, Sketch sketch, Configuration configuration) {
        this.context = context;
        this.configuration = configuration;

        initConfig();

        EventBus.getDefault().register(this);
    }

    private void initConfig() {
        onEvent(new AppConfigChangedEvent(AppConfig.Key.OUT_LOG_2_SDCARD));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_LEVEL));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_TIME));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_REQUEST));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_CACHE));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_ZOOM));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_LARGE));

        onEvent(new AppConfigChangedEvent(AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY));

        configuration.getImagePreprocessor().addPreprocessor(new VideoThumbnailPreprocessor());
        configuration.getImagePreprocessor().addPreprocessor(new XpkIconPreprocessor());
        configuration.setErrorTracker(new SampleErrorTracker(context));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(AppConfigChangedEvent event) {
        if (AppConfig.Key.OUT_LOG_2_SDCARD.equals(event.key)) {
            SLog.setProxy(AppConfig.getBoolean(context, AppConfig.Key.OUT_LOG_2_SDCARD) ? new SampleLogProxy(context) : null);
        } else if (AppConfig.Key.LOG_LEVEL.equals(event.key)) {
            String levelValue = AppConfig.getString(context, AppConfig.Key.LOG_LEVEL);
            if (levelValue == null) {
                levelValue = "";
            }
            switch (levelValue) {
                case "VERBOSE":
                    SLog.setLoggable(SLog.LEVEL_VERBOSE);
                    break;
                case "DEBUG":
                    SLog.setLoggable(SLog.LEVEL_DEBUG);
                    break;
                case "INFO":
                    SLog.setLoggable(SLog.LEVEL_INFO);
                    break;
                case "ERROR":
                    SLog.setLoggable(SLog.LEVEL_ERROR);
                    break;
                case "WARNING":
                    SLog.setLoggable(SLog.LEVEL_WARNING);
                    break;
                case "NONE":
                    SLog.setLoggable(SLog.LEVEL_NONE);
                    break;
            }
        } else if (AppConfig.Key.LOG_TIME.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_TIME)) {
                SLog.setLoggable(SLog.TYPE_TIME);
            } else {
                SLog.removeLoggable(SLog.TYPE_TIME);
            }
        } else if (AppConfig.Key.LOG_REQUEST.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_REQUEST)) {
                SLog.setLoggable(SLog.TYPE_FLOW);
            } else {
                SLog.removeLoggable(SLog.TYPE_FLOW);
            }
        } else if (AppConfig.Key.LOG_CACHE.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_CACHE)) {
                SLog.setLoggable(SLog.TYPE_CACHE);
            } else {
                SLog.removeLoggable(SLog.TYPE_CACHE);
            }
        } else if (AppConfig.Key.LOG_ZOOM.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_ZOOM)) {
                SLog.setLoggable(SLog.TYPE_ZOOM);
            } else {
                SLog.removeLoggable(SLog.TYPE_ZOOM);
            }
        } else if (AppConfig.Key.LOG_LARGE.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_LARGE)) {
                SLog.setLoggable(SLog.TYPE_LARGE);
            } else {
                SLog.removeLoggable(SLog.TYPE_LARGE);
            }
        } else if (AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(event.key)) {
            configuration.setGlobalMobileNetworkPauseDownload(AppConfig.getBoolean(context, AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD));
        } else if (AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE.equals(event.key)) {
            configuration.setGlobalLowQualityImage(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE));
        } else if (AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED.equals(event.key)) {
            configuration.setGlobalInPreferQualityOverSpeed(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED));
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK.equals(event.key)) {
            configuration.getDiskCache().setDisabled(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK));
        } else if (AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL.equals(event.key)) {
            configuration.getBitmapPool().setDisabled(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL));
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY.equals(event.key)) {
            configuration.getMemoryCache().setDisabled(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY));
        }
    }
}
