package me.xiaopan.sketchsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.LogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;
import me.xiaopan.sketch.shaper.RoundRectImageShaper;
import me.xiaopan.sketch.state.DrawableStateImage;
import me.xiaopan.sketch.state.OldStateImage;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.util.MyImagePreprocessor;
import me.xiaopan.sketchsample.util.Settings;

public class SketchManager {
    private Context context;

    public SketchManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void initConfig(Context context) {
        SLog.setLogTracker(Settings.getBoolean(context, Settings.PREFERENCE_OUT_LOG_2_SDCARD) ? new SampleLogTracker(context) : null);

        LogType.BASE.setEnabled(Settings.getBoolean(context, Settings.PREFERENCE_LOG_BASE));
        LogType.TIME.setEnabled(Settings.getBoolean(context, Settings.PREFERENCE_LOG_TIME));
        LogType.REQUEST.setEnabled(Settings.getBoolean(context, Settings.PREFERENCE_LOG_REQUEST));
        LogType.CACHE.setEnabled(Settings.getBoolean(context, Settings.PREFERENCE_LOG_CACHE));
        LogType.ZOOM.setEnabled(Settings.getBoolean(context, Settings.PREFERENCE_LOG_ZOOM));
        LogType.LARGE.setEnabled(Settings.getBoolean(context, Settings.PREFERENCE_LOG_LARGE));
        Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
        sketchConfiguration.setMobileNetworkGlobalPauseDownload(Settings.getBoolean(context, Settings.PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD));
        sketchConfiguration.setGlobalLowQualityImage(Settings.getBoolean(context, Settings.PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE));
        sketchConfiguration.setGlobalInPreferQualityOverSpeed(Settings.getBoolean(context, Settings.PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED));
        sketchConfiguration.getDiskCache().setDisabled(Settings.getBoolean(context, Settings.PREFERENCE_GLOBAL_DISABLE_CACHE_IN_DISK));
        sketchConfiguration.getBitmapPool().setDisabled(Settings.getBoolean(context, Settings.PREFERENCE_GLOBAL_DISABLE_BITMAP_POOL));
        sketchConfiguration.getMemoryCache().setDisabled(Settings.getBoolean(context, Settings.PREFERENCE_GLOBAL_DISABLE_CACHE_IN_MEMORY));
        sketchConfiguration.setImagePreprocessor(new MyImagePreprocessor());
        sketchConfiguration.setMonitor(new SampleSketchMonitor(context));
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
