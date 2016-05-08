package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.util.AttributeSet;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.util.Settings;

public class MyImageView extends SketchImageView {
    private Settings settings;
    private boolean autoApplyGlobalAttr = true;

    public MyImageView(Context context) {
        super(context);
        onInit(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInit(context);
    }

    private void onInit(Context context){
        settings = Settings.with(context);
        if(!isInEditMode()){
            setShowGifFlag(R.drawable.ic_gif);
        }
    }

    @Override
    public void onDisplay(UriScheme uriScheme) {
        if(autoApplyGlobalAttr){
            setShowPressedStatus(settings.isShowPressedStatus());
            setShowImageFrom(settings.isShowImageFromFlag());
            setShowDownloadProgress(settings.isShowImageDownloadProgress());
            setClickRetryOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            setClickRetryOnFailed(settings.isClickDisplayOnFailed());
        }

        super.onDisplay(uriScheme);
    }

    public void setAutoApplyGlobalAttr(boolean autoApplyGlobalAttr){
        this.autoApplyGlobalAttr = autoApplyGlobalAttr;
    }
}
