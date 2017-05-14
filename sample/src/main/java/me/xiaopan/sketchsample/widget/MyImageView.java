package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.util.Settings;

public class MyImageView extends SketchImageView {
    private boolean autoApplyGlobalAttr = true;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        } catch (EventBusException e) {
            e.printStackTrace();
        }

        if (!isInEditMode()) {
            onGlobalAttrChanged(Settings.PREFERENCE_SHOW_GIF_FLAG);
        }
    }

    @Override
    public void onReadyDisplay(UriScheme uriScheme) {
        super.onReadyDisplay(uriScheme);

        onGlobalAttrChanged(Settings.PREFERENCE_SHOW_IMAGE_FROM_FLAG);
        onGlobalAttrChanged(Settings.PREFERENCE_CLICK_SHOW_PRESSED_STATUS);
        onGlobalAttrChanged(Settings.PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS);
        onGlobalAttrChanged(Settings.PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD);
        onGlobalAttrChanged(Settings.PREFERENCE_CLICK_DISPLAY_ON_FAILED);
        onGlobalAttrChanged(Settings.PREFERENCE_DISABLE_CORRECT_IMAGE_ORIENTATION);
    }

    public void setAutoApplyGlobalAttr(boolean autoApplyGlobalAttr) {
        this.autoApplyGlobalAttr = autoApplyGlobalAttr;
    }

    @Subscribe
    public void onGlobalAttrChanged(String key) {
        if (Settings.PREFERENCE_SHOW_GIF_FLAG.equals(key)) {
            if (autoApplyGlobalAttr) {
                boolean showGifFlag = Settings.getBoolean(getContext(), Settings.PREFERENCE_SHOW_GIF_FLAG);
                if (showGifFlag) {
                    setShowGifFlag(R.drawable.ic_gif);
                } else {
                    setShowGifFlag(null);
                }
            }
        } else if (Settings.PREFERENCE_SHOW_IMAGE_FROM_FLAG.equals(key)) {
            if (autoApplyGlobalAttr) {
                setShowImageFrom(Settings.getBoolean(getContext(), Settings.PREFERENCE_SHOW_IMAGE_FROM_FLAG));
            }
        } else if (Settings.PREFERENCE_CLICK_SHOW_PRESSED_STATUS.equals(key)) {
            if (autoApplyGlobalAttr) {
                setShowPressedStatus(Settings.getBoolean(getContext(), Settings.PREFERENCE_CLICK_SHOW_PRESSED_STATUS));
            }
        } else if (Settings.PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS.equals(key)) {
            if (autoApplyGlobalAttr) {
                setShowDownloadProgress(Settings.getBoolean(getContext(), Settings.PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS));
            }
        } else if (Settings.PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD.equals(key)) {
            if (autoApplyGlobalAttr) {
                setClickRetryOnPauseDownload(Settings.getBoolean(getContext(), Settings.PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD));
            }
        } else if (Settings.PREFERENCE_CLICK_DISPLAY_ON_FAILED.equals(key)) {
            if (autoApplyGlobalAttr) {
                setClickRetryOnError(Settings.getBoolean(getContext(), Settings.PREFERENCE_CLICK_DISPLAY_ON_FAILED));
            }
        } else if (Settings.PREFERENCE_DISABLE_CORRECT_IMAGE_ORIENTATION.equals(key)) {
            getOptions().setCorrectImageOrientationDisabled(Settings.getBoolean(getContext(), Settings.PREFERENCE_DISABLE_CORRECT_IMAGE_ORIENTATION));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }
}
