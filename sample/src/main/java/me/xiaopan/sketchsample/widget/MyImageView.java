package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.util.AttributeSet;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.util.Settings;

public class MyImageView extends SketchImageView {
    private boolean autoApplyGlobalAttr = true;

    public MyImageView(Context context) {
        super(context);
        onInit(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInit(context);
    }

    private void onInit(Context context) {
        if (!isInEditMode()) {
            setShowGifFlag(R.drawable.ic_gif);
        }
    }

    @Override
    public void onDisplay(UriScheme uriScheme) {
        if (autoApplyGlobalAttr) {
            setShowPressedStatus(Settings.getBoolean(getContext(), Settings.PREFERENCE_CLICK_SHOW_PRESSED_STATUS));
            setShowImageFrom(Settings.getBoolean(getContext(), Settings.PREFERENCE_SHOW_IMAGE_FROM_FLAG));
            setShowDownloadProgress(Settings.getBoolean(getContext(), Settings.PREFERENCE_SHOW_IMAGE_DOWNLOAD_PROGRESS));
            setClickRetryOnPauseDownload(Settings.getBoolean(getContext(), Settings.PREFERENCE_CLICK_DISPLAY_ON_PAUSE_DOWNLOAD));
            setClickRetryOnError(Settings.getBoolean(getContext(), Settings.PREFERENCE_CLICK_DISPLAY_ON_FAILED));
        }

        super.onDisplay(uriScheme);
    }

    public void setAutoApplyGlobalAttr(boolean autoApplyGlobalAttr) {
        this.autoApplyGlobalAttr = autoApplyGlobalAttr;
    }
}
