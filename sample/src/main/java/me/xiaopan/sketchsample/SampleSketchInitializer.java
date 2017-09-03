package me.xiaopan.sketchsample;

import android.content.Context;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Initializer;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.util.AppConfig;
import me.xiaopan.sketchsample.util.VideoThumbnailUriModel;
import me.xiaopan.sketchsample.util.XpkIconUriModel;

public class SampleSketchInitializer implements Initializer {

    private Context context;
    private Configuration configuration;

    @Override
    public void onInitialize(@NonNull Context context, @NonNull Configuration configuration) {
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
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_HUGE_IMAGE));

        onEvent(new AppConfigChangedEvent(AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY));

        configuration.setErrorTracker(new SampleErrorTracker(context));

        configuration.getUriModelRegistry().add(new VideoThumbnailUriModel());
        configuration.getUriModelRegistry().add(new XpkIconUriModel());
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
                    SLog.setLevel(SLog.LEVEL_VERBOSE);
                    break;
                case "DEBUG":
                    SLog.setLevel(SLog.LEVEL_DEBUG);
                    break;
                case "INFO":
                    SLog.setLevel(SLog.LEVEL_INFO);
                    break;
                case "ERROR":
                    SLog.setLevel(SLog.LEVEL_ERROR);
                    break;
                case "WARNING":
                    SLog.setLevel(SLog.LEVEL_WARNING);
                    break;
                case "NONE":
                    SLog.setLevel(SLog.LEVEL_NONE);
                    break;
            }
        } else if (AppConfig.Key.LOG_TIME.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_TIME)) {
                SLog.openType(SLog.TYPE_TIME);
            } else {
                SLog.closeType(SLog.TYPE_TIME);
            }
        } else if (AppConfig.Key.LOG_REQUEST.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_REQUEST)) {
                SLog.openType(SLog.TYPE_FLOW);
            } else {
                SLog.closeType(SLog.TYPE_FLOW);
            }
        } else if (AppConfig.Key.LOG_CACHE.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_CACHE)) {
                SLog.openType(SLog.TYPE_CACHE);
            } else {
                SLog.closeType(SLog.TYPE_CACHE);
            }
        } else if (AppConfig.Key.LOG_ZOOM.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_ZOOM)) {
                SLog.openType(SLog.TYPE_ZOOM);
            } else {
                SLog.closeType(SLog.TYPE_ZOOM);
            }
        } else if (AppConfig.Key.LOG_HUGE_IMAGE.equals(event.key)) {
            if (AppConfig.getBoolean(context, AppConfig.Key.LOG_HUGE_IMAGE)) {
                SLog.openType(SLog.TYPE_HUGE_IMAGE);
            } else {
                SLog.closeType(SLog.TYPE_HUGE_IMAGE);
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
