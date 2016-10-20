package me.xiaopan.sketchsample;

import android.content.Context;
import android.widget.ImageView;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.CircleImageProcessor;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.process.RoundedCornerImageProcessor;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.MakerDrawableModeImage;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.util.MyImagePreprocessor;
import me.xiaopan.sketchsample.util.Settings;

public class SketchManager {
    private Context context;

    public SketchManager(Context context) {
        this.context = context;
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
    }

    public void initDisplayOptions() {
        TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
        Sketch.putOptions(OptionsType.NORMAL_RECT, new DisplayOptions()
                .setLoadingImage(R.drawable.image_loading)
                .setFailedImage(R.drawable.image_failed)
                .setPauseDownloadImage(R.drawable.image_pause_download)
                .setImageDisplayer(transitionImageDisplayer)
        );

        RoundedCornerImageProcessor roundedCornerImageProcessor = new RoundedCornerImageProcessor(SketchUtils.dp2px(context, 10));
        Resize appIconSize = new Resize(SketchUtils.dp2px(context, 60), SketchUtils.dp2px(context, 60), ImageView.ScaleType.CENTER_CROP);
        Sketch.putOptions(OptionsType.APP_ICON, new DisplayOptions()
                .setLoadingImage(new MakerDrawableModeImage(R.drawable.image_loading, roundedCornerImageProcessor, appIconSize, true))
                .setFailedImage(new MakerDrawableModeImage(R.drawable.image_failed, roundedCornerImageProcessor, appIconSize, true))
                .setPauseDownloadImage(new MakerDrawableModeImage(R.drawable.image_pause_download, roundedCornerImageProcessor, appIconSize, true))
                .setResizeByFixedSize(true)
                .setForceUseResize(true)
                .setImageDisplayer(transitionImageDisplayer)
                .setImageProcessor(roundedCornerImageProcessor)
        );

        Sketch.putOptions(OptionsType.DETAIL, new DisplayOptions()
                .setImageDisplayer(transitionImageDisplayer)
                .setDecodeGifImage(true)
        );

        Sketch.putOptions(OptionsType.NORMAL_CIRCULAR, new DisplayOptions()
                .setLoadingImage(new MakerDrawableModeImage(R.drawable.image_loading, CircleImageProcessor.getInstance()))
                .setFailedImage(new MakerDrawableModeImage(R.drawable.image_failed, CircleImageProcessor.getInstance()))
                .setPauseDownloadImage(new MakerDrawableModeImage(R.drawable.image_pause_download, CircleImageProcessor.getInstance()))
                .setImageDisplayer(transitionImageDisplayer)
                .setImageProcessor(CircleImageProcessor.getInstance())
        );

        Sketch.putOptions(OptionsType.WINDOW_BACKGROUND, new LoadOptions()
                .setImageProcessor(new GaussianBlurImageProcessor(true))
        );
    }
}
