package me.xiaopan.sketchsample;

import android.content.Context;
import android.widget.ImageView;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.feature.ExceptionMonitor;
import me.xiaopan.sketch.feature.large.Tile;
import me.xiaopan.sketch.process.CircleImageProcessor;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.process.RoundedCornerImageProcessor;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.MakerDrawableModeImage;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.util.MyImagePreprocessor;
import me.xiaopan.sketchsample.util.Settings;

public class SketchManager {
    private Context context;

    public SketchManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void initConfig() {
        Sketch.setDebugMode(BuildConfig.DEBUG);
        Settings settings = Settings.with(context);
        Configuration sketchConfiguration = Sketch.with(context).getConfiguration();
        sketchConfiguration.setMobileNetworkGlobalPauseDownload(settings.isMobileNetworkPauseDownload());
        sketchConfiguration.setGlobalLowQualityImage(settings.isGlobalLowQualityImage());
        sketchConfiguration.setGlobalInPreferQualityOverSpeed(settings.isGlobalInPreferQualityOverSpeed());
        sketchConfiguration.setGlobalDisableCacheInDisk(settings.isGlobalDisableCacheInDisk());
        sketchConfiguration.setGlobalDisableCacheInMemory(settings.isGlobalDisableCacheInMemory());
        sketchConfiguration.setImagePreprocessor(new MyImagePreprocessor());
        sketchConfiguration.setExceptionMonitor(new MyExceptionMonitor(context));
    }

    public void initDisplayOptions() {
        TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
        Sketch.putOptions(OptionsType.NORMAL_RECT, new DisplayOptions()
                .setLoadingImage(R.drawable.image_loading)
                .setErrorImage(R.drawable.image_error)
                .setPauseDownloadImage(R.drawable.image_pause_download)
                .setImageDisplayer(transitionImageDisplayer)
        );

        RoundedCornerImageProcessor roundedCornerImageProcessor = new RoundedCornerImageProcessor(SketchUtils.dp2px(context, 10));
        Resize appIconSize = new Resize(SketchUtils.dp2px(context, 60), SketchUtils.dp2px(context, 60), ImageView.ScaleType.CENTER_CROP);
        Sketch.putOptions(OptionsType.APP_ICON, new DisplayOptions()
                .setLoadingImage(new MakerDrawableModeImage(R.drawable.image_loading, roundedCornerImageProcessor, appIconSize, true))
                .setErrorImage(new MakerDrawableModeImage(R.drawable.image_error, roundedCornerImageProcessor, appIconSize, true))
                .setPauseDownloadImage(new MakerDrawableModeImage(R.drawable.image_pause_download,
                        roundedCornerImageProcessor, appIconSize, true))
                .setResizeByFixedSize(true)
                .setForceUseResize(true)
                .setImageDisplayer(transitionImageDisplayer)
                .setImageProcessor(roundedCornerImageProcessor)
        );

        Sketch.putOptions(OptionsType.NORMAL_CIRCULAR, new DisplayOptions()
                .setLoadingImage(new MakerDrawableModeImage(R.drawable.image_loading, CircleImageProcessor.getInstance()))
                .setErrorImage(new MakerDrawableModeImage(R.drawable.image_error, CircleImageProcessor.getInstance()))
                .setPauseDownloadImage(new MakerDrawableModeImage(R.drawable.image_pause_download, CircleImageProcessor.getInstance()))
                .setImageDisplayer(transitionImageDisplayer)
                .setImageProcessor(CircleImageProcessor.getInstance())
        );

        Sketch.putOptions(OptionsType.WINDOW_BACKGROUND, new LoadOptions()
                .setImageProcessor(new GaussianBlurImageProcessor(true))
        );
    }

    private static class MyExceptionMonitor extends ExceptionMonitor {

        public MyExceptionMonitor(Context context) {
            super(context);
        }

        @Override
        public void onTileSortError(@SuppressWarnings("UnusedParameters") IllegalArgumentException e,
                                    List<Tile> tileList, @SuppressWarnings("UnusedParameters") boolean useLegacyMergeSort) {
            super.onTileSortError(e, tileList, useLegacyMergeSort);
            String message = (useLegacyMergeSort ? "useLegacyMergeSort. " : "") + SketchUtils.tileListToString(tileList);
            CrashReport.postCatchedException(new Exception(message, e));
        }
    }
}
