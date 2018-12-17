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

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

import me.panpf.sketch.SketchView;

/**
 * 由小到大图片显示器
 */
@SuppressWarnings("unused")
public class ZoomInImageDisplayer implements ImageDisplayer {
    private static final String KEY = "ZoomInImageDisplayer";
    private static final float DEFAULT_FROM = 0.5f;

    private int duration;
    private float fromX;
    private float fromY;
    private Interpolator interpolator;
    private boolean alwaysUse;

    public ZoomInImageDisplayer(float fromX, float fromY, Interpolator interpolator, int duration, boolean alwaysUse) {
        this.duration = duration;
        this.fromY = fromY;
        this.fromX = fromX;
        this.interpolator = interpolator;
        this.alwaysUse = alwaysUse;
    }

    public ZoomInImageDisplayer(float fromX, float fromY, Interpolator interpolator, int duration) {
        this(fromX, fromY, interpolator, duration, false);
    }

    public ZoomInImageDisplayer(float fromX, float fromY, Interpolator interpolator, boolean alwaysUse) {
        this(fromX, fromY, interpolator, DEFAULT_ANIMATION_DURATION, alwaysUse);
    }

    public ZoomInImageDisplayer(float fromX, float fromY, Interpolator interpolator) {
        this(fromX, fromY, interpolator, DEFAULT_ANIMATION_DURATION, false);
    }

    public ZoomInImageDisplayer(float fromX, float fromY, boolean alwaysUse) {
        this(fromX, fromY, new AccelerateDecelerateInterpolator(), DEFAULT_ANIMATION_DURATION, alwaysUse);
    }

    public ZoomInImageDisplayer(float fromX, float fromY) {
        this(fromX, fromY, new AccelerateDecelerateInterpolator(), DEFAULT_ANIMATION_DURATION, false);
    }

    public ZoomInImageDisplayer(Interpolator interpolator, boolean alwaysUse) {
        this(DEFAULT_FROM, DEFAULT_FROM, interpolator, DEFAULT_ANIMATION_DURATION, alwaysUse);
    }

    public ZoomInImageDisplayer(Interpolator interpolator) {
        this(DEFAULT_FROM, DEFAULT_FROM, interpolator, DEFAULT_ANIMATION_DURATION, false);
    }

    public ZoomInImageDisplayer(int duration, boolean alwaysUse) {
        this(DEFAULT_FROM, DEFAULT_FROM, new AccelerateDecelerateInterpolator(), duration, alwaysUse);
    }

    public ZoomInImageDisplayer(int duration) {
        this(DEFAULT_FROM, DEFAULT_FROM, new AccelerateDecelerateInterpolator(), duration, false);
    }

    public ZoomInImageDisplayer(boolean alwaysUse) {
        this(DEFAULT_FROM, DEFAULT_FROM, new AccelerateDecelerateInterpolator(), DEFAULT_ANIMATION_DURATION, alwaysUse);
    }

    public ZoomInImageDisplayer() {
        this(DEFAULT_FROM, DEFAULT_FROM, new AccelerateDecelerateInterpolator(), DEFAULT_ANIMATION_DURATION, false);
    }

    @Override
    public void display(@NonNull SketchView sketchView, @NonNull Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, 1.0f, fromY, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(interpolator);
        scaleAnimation.setDuration(duration);
        sketchView.clearAnimation();
        sketchView.setImageDrawable(newDrawable);
        sketchView.startAnimation(scaleAnimation);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s(duration=%d,fromX=%s,fromY=%s,interpolator=%s,alwaysUse=%s)",
                KEY, duration, fromX, fromY, interpolator != null ? interpolator.getClass().getSimpleName() : null, alwaysUse);
    }

    @Override
    public boolean isAlwaysUse() {
        return alwaysUse;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public float getFromX() {
        return fromX;
    }

    public float getFromY() {
        return fromY;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }
}
