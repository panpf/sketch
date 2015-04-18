package me.xiaopan.spear.sample.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.Window;

import me.xiaopan.spear.sample.R;

public class WindowBackgroundManager {
    private Activity activity;
    private Drawable oneDrawable;
    private Drawable twoDrawable;

    public WindowBackgroundManager(Activity activity) {
        this.activity = activity;
        this.activity.getWindow().setFormat(PixelFormat.TRANSLUCENT); // 要先将Window的格式设为透明的，如果不这么做的话第一次改变Window的背景的话屏幕会快速的闪一下（黑色的）
    }

    public void setBackground(Drawable newDrawable) {
        Drawable oldOneDrawable = oneDrawable;
        Window window = activity.getWindow();
        Drawable oneDrawable = twoDrawable!=null?twoDrawable:activity.getResources().getDrawable(R.drawable.shape_window_background);
        Drawable[] drawables = new Drawable[]{oneDrawable, newDrawable};
        TransitionDrawable transitionDrawable = new TransitionDrawable(drawables);
        transitionDrawable.setCrossFadeEnabled(true);
        window.setBackgroundDrawable(transitionDrawable);
        transitionDrawable.startTransition(800);
        this.oneDrawable = oneDrawable;
        this.twoDrawable = newDrawable;
        recycleDrawable(oldOneDrawable);
    }

    public void destroy() {
        recycleDrawable(oneDrawable);
        recycleDrawable(twoDrawable);
    }

    private void recycleDrawable(Drawable drawable){
        if(drawable != null && drawable instanceof BitmapDrawable){
            ((BitmapDrawable) drawable).getBitmap().recycle();
        }
    }

    public interface OnSetWindowBackgroundListener {
        void onSetWindowBackground(Drawable drawable);
    }
}
