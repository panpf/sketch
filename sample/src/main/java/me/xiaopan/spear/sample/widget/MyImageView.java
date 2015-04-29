package me.xiaopan.spear.sample.widget;

import android.content.Context;
import android.util.AttributeSet;

import me.xiaopan.spear.SpearImageView;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.util.Settings;

public class MyImageView extends SpearImageView {
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
        setShowGifFlag(true);
        if(!isInEditMode()){
            setGifFlagDrawable(R.drawable.ic_gif);
        }
    }

    @Override
    public void onDisplay() {
        super.onDisplay();

        if(autoApplyGlobalAttr){
            setShowClickRipple(settings.isShowClickRipple());
            setShowFromFlag(settings.isShowImageFromFlag());
            setShowDownloadProgress(settings.isShowImageDownloadProgress());
            setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
        }
    }

    public void setAutoApplyGlobalAttr(boolean autoApplyGlobalAttr){
        this.autoApplyGlobalAttr = autoApplyGlobalAttr;
    }
}
