package me.xiaopan.sketchsample;

import android.content.Context;
import android.widget.ImageView;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.FailureImageHolder;
import me.xiaopan.sketch.LoadOptions;
import me.xiaopan.sketch.LoadingImageHolder;
import me.xiaopan.sketch.PauseDownloadImageHolder;
import me.xiaopan.sketch.Resize;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.CircleImageProcessor;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.process.RoundedCornerImageProcessor;
import me.xiaopan.sketchsample.util.DeviceUtils;
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
        sketchConfiguration.setMobileNetworkPauseDownload(settings.isMobileNetworkPauseDownload());
        sketchConfiguration.setLowQualityImage(settings.isLowQualityImage());
        sketchConfiguration.setLowQualityImage(settings.isLowQualityImage());
        sketchConfiguration.setCacheInDisk(settings.isCacheInDisk());
        sketchConfiguration.setCacheInMemory(settings.isCacheInMemory());
    }

    public void initDisplayOptions(){
        TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
        Sketch.putOptions(OptionsType.NORMAL_RECT, new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setFailureImage(R.drawable.image_failure)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setDecodeGifImage(false)
                        .setImageDisplayer(transitionImageDisplayer)
        );

        RoundedCornerImageProcessor roundedCornerImageProcessor = new RoundedCornerImageProcessor(DeviceUtils.dp2px(context, 10));
        Resize appIconSize = new Resize(DeviceUtils.dp2px(context, 60), DeviceUtils.dp2px(context, 60), ImageView.ScaleType.CENTER_CROP);
        Sketch.putOptions(OptionsType.APP_ICON, new DisplayOptions()
                        .setLoadingImage(new LoadingImageHolder(R.drawable.image_loading).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setFailureImage(new FailureImageHolder(R.drawable.image_failure).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setPauseDownloadImage(new PauseDownloadImageHolder(R.drawable.image_pause_download).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setDecodeGifImage(false)
                        .setResizeByFixedSize(true)
                        .setForceUseResize(true)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageProcessor(roundedCornerImageProcessor)
        );

        Sketch.putOptions(OptionsType.DETAIL, new DisplayOptions()
                        .setImageDisplayer(transitionImageDisplayer)
        );

        Sketch.putOptions(OptionsType.NORMAL_CIRCULAR, new DisplayOptions()
                        .setLoadingImage(new LoadingImageHolder(R.drawable.image_loading).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setFailureImage(new FailureImageHolder(R.drawable.image_failure).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setPauseDownloadImage(new PauseDownloadImageHolder(R.drawable.image_pause_download).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setDecodeGifImage(false)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageProcessor(CircleImageProcessor.getInstance())
        );

        Sketch.putOptions(OptionsType.WINDOW_BACKGROUND, new LoadOptions()
                        .setImageProcessor(new GaussianBlurImageProcessor(true))
                        .setDecodeGifImage(false)
        );
    }
}
