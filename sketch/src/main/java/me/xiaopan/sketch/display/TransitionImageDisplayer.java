/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.display;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;

import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 过渡效果的图片显示器
 */
public class TransitionImageDisplayer implements ImageDisplayer {
    protected String logName = "TransitionImageDisplayer";

    private int duration;
    private boolean alwaysUse;

    public TransitionImageDisplayer(int duration, boolean alwaysUse) {
        this.duration = duration;
        this.alwaysUse = alwaysUse;
    }

    public TransitionImageDisplayer(int duration) {
        this(duration, false);
    }

    public TransitionImageDisplayer(boolean alwaysUse) {
        this(DEFAULT_ANIMATION_DURATION, alwaysUse);
    }

    public TransitionImageDisplayer() {
        this(DEFAULT_ANIMATION_DURATION, false);
    }

    @Override
    public void display(ImageViewInterface imageViewInterface, Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        if (newDrawable instanceof SketchGifDrawable) {
            imageViewInterface.clearAnimation();
            imageViewInterface.setImageDrawable(newDrawable);
        } else {
            Drawable oldDrawable = imageViewInterface.getDrawable();
            if (oldDrawable != null && oldDrawable instanceof LayerDrawable) {
                oldDrawable = SketchUtils.getLastDrawable(oldDrawable);
            }
            if (oldDrawable == null) {
                oldDrawable = new ColorDrawable(Color.TRANSPARENT);
            }
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{oldDrawable, newDrawable});
            imageViewInterface.clearAnimation();
            imageViewInterface.setImageDrawable(transitionDrawable);
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(duration);
        }
    }

    @Override
    public boolean isAlwaysUse() {
        return alwaysUse;
    }

    /**
     * 获取持续时间，单位毫秒
     */
    public int getDuration() {
        return duration;
    }

    @Override
    public String getKey() {
        return String.format("%s(duration=%d, alwaysUse=%s)", logName, duration, alwaysUse);
    }
}
