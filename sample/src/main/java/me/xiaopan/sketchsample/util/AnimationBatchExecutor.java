package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 动画批量执行器
 */
public class AnimationBatchExecutor {
    private Context context;
    private View[] views;
    private Handler handler;
    private int runningNumber;
    private int delayMills;
    private int showAnimResId;
    private int hiddenAnimResId;

    public AnimationBatchExecutor(Context context, int showAnimResId, int hiddenAnimResId, int delayMills, View... views) {
        this.context = context;
        this.views = views;
        this.delayMills = delayMills;
        this.showAnimResId = showAnimResId;
        this.hiddenAnimResId = hiddenAnimResId;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public boolean isRunning() {
        return runningNumber != 0;
    }

    public boolean start(boolean show) {
        runningNumber = views.length;
        int delay = 0;
        int w = 0;
        for (View view : views) {
            handler.postDelayed(new ExecuteAnimation(view, show ? showAnimResId : hiddenAnimResId, show ? View.VISIBLE : View.INVISIBLE), delay + (w * 10));
            w++;
            delay += delayMills;
        }

        return true;
    }

    private class ExecuteAnimation implements Runnable {
        private View view;
        private int afterVisibility;
        private int animId = 0;

        private ExecuteAnimation(View view, int animId, int afterVisibility) {
            this.view = view;
            this.animId = animId;
            this.afterVisibility = afterVisibility;
        }

        @Override
        public void run() {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }

            Animation animation = AnimationUtils.loadAnimation(context, animId);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (view.getVisibility() != afterVisibility) {
                        view.setVisibility(afterVisibility);
                    }
                    runningNumber--;
                    view.setEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(animation);
        }
    }
}
