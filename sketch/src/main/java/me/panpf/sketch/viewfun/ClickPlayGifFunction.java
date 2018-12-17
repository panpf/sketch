/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.panpf.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.view.View;

import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.RedisplayListener;
import me.panpf.sketch.state.OldStateImage;
import me.panpf.sketch.util.SketchUtils;

/**
 * 点击播放 gif 功能
 */
public class ClickPlayGifFunction extends ViewFunction {
    private FunctionCallbackView view;
    private Drawable playIconDrawable;

    private boolean canClickPlay;
    private Drawable lastDrawable;
    private int cacheViewWidth;
    private int cacheViewHeight;
    private int iconDrawLeft;
    private int iconDrawTop;

    private PlayGifRedisplayListener redisplayListener;

    public ClickPlayGifFunction(FunctionCallbackView view) {
        this.view = view;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        Drawable drawable = view.getDrawable();
        if (drawable != lastDrawable) {
            canClickPlay = canClickPlay(drawable);
            lastDrawable = drawable;
        }

        if (!canClickPlay) {
            return;
        }

        if (cacheViewWidth != view.getWidth() || cacheViewHeight != view.getHeight()) {
            cacheViewWidth = view.getWidth();
            cacheViewHeight = view.getHeight();
            int availableWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight() - playIconDrawable.getBounds().width();
            int availableHeight = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom() - playIconDrawable.getBounds().height();
            iconDrawLeft = view.getPaddingLeft() + (availableWidth / 2);
            iconDrawTop = view.getPaddingTop() + (availableHeight / 2);
        }

        canvas.save();
        canvas.translate(iconDrawLeft, iconDrawTop);
        playIconDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 点击事件
     *
     * @param v View
     * @return true：已经消费了，不必往下传了
     */
    public boolean onClick(@SuppressWarnings("UnusedParameters") View v) {
        if (isClickable()) {
            if (redisplayListener == null) {
                redisplayListener = new PlayGifRedisplayListener();
            }
            view.redisplay(redisplayListener);
            return true;
        }
        return false;
    }

    public boolean isClickable() {
        return canClickPlay;
    }

    private boolean canClickPlay(Drawable newDrawable) {
        if (newDrawable == null) {
            return false;
        }
        Drawable endDrawable = SketchUtils.getLastDrawable(newDrawable);
        return SketchUtils.isGifImage(endDrawable) && !(endDrawable instanceof SketchGifDrawable);
    }

    public boolean setPlayIconDrawable(@NonNull Drawable playIconDrawable) {
        if (this.playIconDrawable == playIconDrawable) {
            return false;
        }

        this.playIconDrawable = playIconDrawable;
        this.playIconDrawable.setBounds(0, 0, playIconDrawable.getIntrinsicWidth(), playIconDrawable.getIntrinsicHeight());
        return true;
    }

    private static class PlayGifRedisplayListener implements RedisplayListener {

        @Override
        public void onPreCommit(String cacheUri, DisplayOptions cacheOptions) {
            cacheOptions.setLoadingImage(new OldStateImage());
            cacheOptions.setDecodeGifImage(true);
        }
    }
}
