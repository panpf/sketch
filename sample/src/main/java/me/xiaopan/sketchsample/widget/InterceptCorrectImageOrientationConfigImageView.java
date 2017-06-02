package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.greenrobot.eventbus.Subscribe;

import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.util.AppConfig;

public class InterceptCorrectImageOrientationConfigImageView extends SampleImageView {

    public InterceptCorrectImageOrientationConfigImageView(Context context) {
        super(context);
    }

    public InterceptCorrectImageOrientationConfigImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Subscribe
    public void onEvent(AppConfigChangedEvent event) {
        if (AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION.equals(event.key)) {
            return;
        }

        super.onEvent(event);
    }
}
