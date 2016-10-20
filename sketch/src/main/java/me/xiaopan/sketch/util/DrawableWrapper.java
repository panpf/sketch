/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.util;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class DrawableWrapper extends Drawable implements Drawable.Callback{

    private Drawable drawable;

    public DrawableWrapper(Drawable drawable) {
        setWrappedDrawable(drawable);
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (drawable != null) {
            drawable.setBounds(bounds);
        }
    }

    @Override
    public int getChangingConfigurations() {
        return drawable != null ? drawable.getChangingConfigurations() : super.getChangingConfigurations();
    }

    @Override
    public void setChangingConfigurations(int configs) {
        if (drawable != null) {
            drawable.setChangingConfigurations(configs);
        }
    }

    @Override
    public void setDither(boolean dither) {
        if (drawable != null) {
            drawable.setDither(dither);
        }
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        if (drawable != null) {
            drawable.setFilterBitmap(filter);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (drawable != null) {
            drawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (drawable != null) {
            drawable.setColorFilter(cf);
        }
    }

    @Override
    public boolean isStateful() {
        return drawable != null ? drawable.isStateful() : super.isStateful();
    }

    @Override
    public boolean setState(final int[] stateSet) {
        return drawable != null ? drawable.setState(stateSet) : super.setState(stateSet);
    }

    @Override
    public int[] getState() {
        return drawable != null ? drawable.getState() : super.getState();
    }

    @Override
    public void jumpToCurrentState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            drawable.jumpToCurrentState();
        }
    }

    @Override
    public Drawable getCurrent() {
        return drawable != null ? drawable.getCurrent() : super.getCurrent();
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        return super.setVisible(visible, restart) || drawable != null && drawable.setVisible(visible, restart);
    }

    @Override
    public int getOpacity() {
        return drawable != null ? drawable.getOpacity() : PixelFormat.UNKNOWN;
    }

    @Override
    public Region getTransparentRegion() {
        return drawable != null ? drawable.getTransparentRegion() : super.getTransparentRegion();
    }

    @Override
    public int getIntrinsicWidth() {
        return drawable != null ? drawable.getIntrinsicWidth() : super.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return drawable != null ? drawable.getIntrinsicHeight() : super.getIntrinsicHeight();
    }

    @Override
    public int getMinimumWidth() {
        return drawable != null ? drawable.getMinimumWidth() : super.getMinimumWidth();
    }

    @Override
    public int getMinimumHeight() {
        return drawable != null ? drawable.getMinimumHeight() : super.getMinimumHeight();
    }

    @Override
    public boolean getPadding(Rect padding) {
        return drawable != null ? drawable.getPadding(padding) : super.getPadding(padding);
    }

    @Override
    protected boolean onLevelChange(int level) {
        return drawable != null ? drawable.setLevel(level) : super.onLevelChange(level);
    }

    @Override
    public boolean isAutoMirrored() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && drawable != null && drawable.isAutoMirrored();
    }

    @Override
    public void setAutoMirrored(boolean mirrored) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            drawable.setAutoMirrored(mirrored);
        }
    }

    @Override
    public void setTint(int tint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(tint);
        }
    }

    @Override
    public void setTintList(ColorStateList tint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTintList(tint);
        }
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTintMode(tintMode);
        }
    }

    @Override
    public void setHotspot(float x, float y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setHotspot(x, y);
        }
    }

    @Override
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setHotspotBounds(left, top, right, bottom);
        }
    }


    @Override
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }


    @SuppressWarnings("unused")
    public Drawable getWrappedDrawable() {
        return drawable;
    }

    public void setWrappedDrawable(Drawable drawable) {
        if (this.drawable != null) {
            this.drawable.setCallback(null);
        }

        this.drawable = drawable;

        if (drawable != null) {
            drawable.setCallback(this);
        }
    }
}