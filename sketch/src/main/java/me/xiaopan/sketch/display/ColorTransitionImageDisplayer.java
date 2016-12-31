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

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import me.xiaopan.sketch.request.ImageViewInterface;

/**
 * 颜色渐入图片显示器
 */
@SuppressWarnings("unused")
public class ColorTransitionImageDisplayer implements ImageDisplayer {
    protected String logName = "ColorTransitionImageDisplayer";

    private int duration;
    private int color;
    private boolean alwaysUse;

    public ColorTransitionImageDisplayer(int color, int duration, boolean alwaysUse) {
        this.color = color;
        this.duration = duration;
        this.alwaysUse = alwaysUse;
    }

    public ColorTransitionImageDisplayer(int color, int duration) {
        this(color, duration, false);
    }

    public ColorTransitionImageDisplayer(int color, boolean alwaysUse) {
        this(color, DEFAULT_ANIMATION_DURATION, alwaysUse);
    }

    public ColorTransitionImageDisplayer(int color) {
        this(color, DEFAULT_ANIMATION_DURATION, false);
    }

    @Override
    public void display(ImageViewInterface imageViewInterface, Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(color), newDrawable});
        imageViewInterface.clearAnimation();
        imageViewInterface.setImageDrawable(transitionDrawable);
        transitionDrawable.setCrossFadeEnabled(true);
        transitionDrawable.startTransition(duration);
    }

    @Override
    public String getKey() {
        return String.format("%s(duration=%d, color=%d, alwaysUse=%s)", logName, duration, color, alwaysUse);
    }

    @Override
    public boolean isAlwaysUse() {
        return alwaysUse;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public int getColor() {
        return color;
    }
}
