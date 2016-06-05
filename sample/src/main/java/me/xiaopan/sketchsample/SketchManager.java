package me.xiaopan.sketchsample;

import android.content.Context;
import android.widget.ImageView;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ImageHolder;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.CircleImageProcessor;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.process.RoundedCornerImageProcessor;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.util.MyImagePreprocessor;
import me.xiaopan.sketchsample.util.Settings;

public class SketchManager {
    private Context context;

    public SketchManager(Context context) {
        this.context = context;
    }

    public void initConfig(){
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

    public void initDisplayOptions(){
        TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
        Sketch.putOptions(OptionsType.NORMAL_RECT, new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setFailedImage(R.drawable.image_failed)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageDisplayer(transitionImageDisplayer)
        );

        RoundedCornerImageProcessor roundedCornerImageProcessor = new RoundedCornerImageProcessor(DeviceUtils.dp2px(context, 10));
        Resize appIconSize = new Resize(DeviceUtils.dp2px(context, 60), DeviceUtils.dp2px(context, 60), ImageView.ScaleType.CENTER_CROP);
        Sketch.putOptions(OptionsType.APP_ICON, new DisplayOptions()
                        .setLoadingImage(new ImageHolder(R.drawable.image_loading).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setFailedImage(new ImageHolder(R.drawable.image_failed).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setPauseDownloadImage(new ImageHolder(R.drawable.image_pause_download).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
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
                        .setLoadingImage(new ImageHolder(R.drawable.image_loading).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setFailedImage(new ImageHolder(R.drawable.image_failed).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setPauseDownloadImage(new ImageHolder(R.drawable.image_pause_download).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageProcessor(CircleImageProcessor.getInstance())
        );

        Sketch.putOptions(OptionsType.WINDOW_BACKGROUND, new LoadOptions()
                        .setImageProcessor(new GaussianBlurImageProcessor(true))
        );
    }
}
