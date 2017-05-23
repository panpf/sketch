package me.xiaopan.sketchsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;
import me.xiaopan.sketch.shaper.RoundRectImageShaper;
import me.xiaopan.sketch.state.DrawableStateImage;
import me.xiaopan.sketch.state.OldStateImage;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.util.AppConfig;
import me.xiaopan.sketchsample.util.XpkIconPreprocessor;

public class SketchManager {
    private Context context;

    public SketchManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void initConfig(Context context) {
        onEvent(new AppConfigChangedEvent(AppConfig.Key.OUT_LOG_2_SDCARD));
        onEvent(new AppConfigChangedEvent(AppConfig.Key.LOG_BASE));
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

        Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
        sketchConfiguration.getImagePreprocessor().addPreprocessor(new XpkIconPreprocessor());
        sketchConfiguration.setErrorTracker(new SampleErrorTracker(context));

        EventBus.getDefault().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(AppConfigChangedEvent event) {
        if (AppConfig.Key.OUT_LOG_2_SDCARD.equals(event.key)) {
            SLog.setLogTracker(AppConfig.getBoolean(context, AppConfig.Key.OUT_LOG_2_SDCARD) ? new SampleLogTracker(context) : null);
        } else if (AppConfig.Key.LOG_BASE.equals(event.key)) {
            SLogType.BASE.setEnabled(AppConfig.getBoolean(context, AppConfig.Key.LOG_BASE));
        } else if (AppConfig.Key.LOG_TIME.equals(event.key)) {
            SLogType.TIME.setEnabled(AppConfig.getBoolean(context, AppConfig.Key.LOG_TIME));
        } else if (AppConfig.Key.LOG_REQUEST.equals(event.key)) {
            SLogType.REQUEST.setEnabled(AppConfig.getBoolean(context, AppConfig.Key.LOG_REQUEST));
        } else if (AppConfig.Key.LOG_CACHE.equals(event.key)) {
            SLogType.CACHE.setEnabled(AppConfig.getBoolean(context, AppConfig.Key.LOG_CACHE));
        } else if (AppConfig.Key.LOG_ZOOM.equals(event.key)) {
            SLogType.ZOOM.setEnabled(AppConfig.getBoolean(context, AppConfig.Key.LOG_ZOOM));
        } else if (AppConfig.Key.LOG_LARGE.equals(event.key)) {
            SLogType.LARGE.setEnabled(AppConfig.getBoolean(context, AppConfig.Key.LOG_LARGE));
        } else if (AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(event.key)) {
            Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
            sketchConfiguration.setMobileNetworkGlobalPauseDownload(AppConfig.getBoolean(context, AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD));
        } else if (AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE.equals(event.key)) {
            Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
            sketchConfiguration.setGlobalLowQualityImage(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE));
        } else if (AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED.equals(event.key)) {
            Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
            sketchConfiguration.setGlobalInPreferQualityOverSpeed(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED));
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK.equals(event.key)) {
            Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
            sketchConfiguration.getDiskCache().setDisabled(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK));
        } else if (AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL.equals(event.key)) {
            Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
            sketchConfiguration.getBitmapPool().setDisabled(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL));
        } else if (AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY.equals(event.key)) {
            Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
            sketchConfiguration.getMemoryCache().setDisabled(AppConfig.getBoolean(context, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY));
        }
    }

    public void initDisplayOptions() {
        TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
        Sketch.putOptions(ImageOptions.RECT, new DisplayOptions()
                .setLoadingImage(R.drawable.image_loading)
                .setErrorImage(R.drawable.image_error)
                .setPauseDownloadImage(R.drawable.image_pause_download)
                .setImageDisplayer(transitionImageDisplayer)
                .setShapeSizeByFixedSize(true));

        Sketch.putOptions(ImageOptions.CIRCULAR_STROKE, new DisplayOptions()
                .setLoadingImage(R.drawable.image_loading)
                .setErrorImage(R.drawable.image_error)
                .setPauseDownloadImage(R.drawable.image_pause_download)
                .setImageDisplayer(transitionImageDisplayer)
                .setImageShaper(new CircleImageShaper().setStroke(Color.WHITE, SketchUtils.dp2px(context, 2)))
                .setShapeSizeByFixedSize(true));

        Sketch.putOptions(ImageOptions.ROUND_RECT, new DisplayOptions()
                .setLoadingImage(R.drawable.image_loading)
                .setErrorImage(R.drawable.image_error)
                .setPauseDownloadImage(R.drawable.image_pause_download)
                .setImageShaper(new RoundRectImageShaper(SketchUtils.dp2px(context, 6)))
                .setImageDisplayer(transitionImageDisplayer)
                .setShapeSizeByFixedSize(true));

        Sketch.putOptions(ImageOptions.WINDOW_BACKGROUND, new DisplayOptions()
                .setLoadingImage(new OldStateImage(new DrawableStateImage(R.drawable.shape_window_background)))
                .setImageProcessor(GaussianBlurImageProcessor.makeLayerColor(Color.parseColor("#66000000")))
                .setCacheProcessedImageInDisk(true)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)   // 效果比较重要
                .setShapeSizeByFixedSize(true)
                .setMaxSize(context.getResources().getDisplayMetrics().widthPixels / 4,
                        context.getResources().getDisplayMetrics().heightPixels / 4)
                .setImageDisplayer(new TransitionImageDisplayer(true)));
    }
}
