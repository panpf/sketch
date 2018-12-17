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

package me.panpf.sketch.display;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import androidx.annotation.NonNull;

import me.panpf.sketch.SketchView;
import me.panpf.sketch.drawable.SketchDrawable;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.drawable.SketchLoadingDrawable;
import me.panpf.sketch.drawable.SketchTransitionDrawable;
import me.panpf.sketch.util.SketchUtils;

/**
 * 过渡效果的图片显示器
 */
public class TransitionImageDisplayer implements ImageDisplayer {
    private static final String KEY = "TransitionImageDisplayer";

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
    public void display(@NonNull SketchView sketchView, @NonNull Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        if (newDrawable instanceof SketchGifDrawable) {
            sketchView.clearAnimation();
            sketchView.setImageDrawable(newDrawable);
        } else {
            Drawable oldDrawable = SketchUtils.getLastDrawable(sketchView.getDrawable());
            if (oldDrawable == null) {
                oldDrawable = new ColorDrawable(Color.TRANSPARENT);
            }

            if (oldDrawable instanceof SketchDrawable
                    && !(oldDrawable instanceof SketchLoadingDrawable)
                    && newDrawable instanceof SketchDrawable
                    && ((SketchDrawable) oldDrawable).getKey().equals(((SketchDrawable) newDrawable).getKey())) {
                sketchView.setImageDrawable(newDrawable);
            } else {
                TransitionDrawable transitionDrawable = new SketchTransitionDrawable(oldDrawable, newDrawable);
                sketchView.clearAnimation();
                sketchView.setImageDrawable(transitionDrawable);
                transitionDrawable.setCrossFadeEnabled(true);
                transitionDrawable.startTransition(duration);
            }
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

    @NonNull
    @Override
    public String toString() {
        return String.format("%s(duration=%d,alwaysUse=%s)", KEY, duration, alwaysUse);
    }
}
