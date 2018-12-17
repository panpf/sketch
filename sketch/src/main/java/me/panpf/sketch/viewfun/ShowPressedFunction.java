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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import me.panpf.sketch.SLog;
import me.panpf.sketch.request.DisplayCache;
import me.panpf.sketch.shaper.ImageShaper;

/**
 * 显示按下状态，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
 */
public class ShowPressedFunction extends ViewFunction {
    static final int DEFAULT_MASK_COLOR = 0x33000000;
    private static final String NAME = "ShowPressedFunction";

    private FunctionPropertyView view;
    private ImageShaper maskShaper;
    private int maskColor = DEFAULT_MASK_COLOR;

    private boolean showProcessed;
    private boolean singleTapUp;
    private Paint maskPaint;
    private GestureDetector gestureDetector;
    private Rect bounds;

    public ShowPressedFunction(@NonNull FunctionPropertyView view) {
        this.view = view;

        this.gestureDetector = new GestureDetector(view.getContext(), new PressedStatusManager());
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (view.isClickable()) {
            gestureDetector.onTouchEvent(event);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    if (showProcessed && !singleTapUp) {
                        showProcessed = false;
                        view.invalidate();
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (!showProcessed) {
            return;
        }

        ImageShaper shaper = getMaskShaper();
        if (shaper != null) {
            canvas.save();
            try {
                if (bounds == null) {
                    bounds = new Rect();
                }
                bounds.set(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
                Path maskPath = shaper.getPath(bounds);
                canvas.clipPath(maskPath);
            } catch (UnsupportedOperationException e) {
                SLog.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                e.printStackTrace();
            }
        }

        if (maskPaint == null) {
            maskPaint = new Paint();
            maskPaint.setColor(maskColor);
            maskPaint.setAntiAlias(true);
        }

        canvas.drawRect(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(),
                view.getHeight() - view.getPaddingBottom(), maskPaint);

        if (shaper != null) {
            canvas.restore();
        }
    }

    public boolean setMaskColor(@ColorInt int maskColor) {
        if (this.maskColor == maskColor) {
            return false;
        }

        this.maskColor = maskColor;
        if (maskPaint != null) {
            maskPaint.setColor(maskColor);
        }
        return true;
    }

    private ImageShaper getMaskShaper() {
        if (maskShaper != null) {
            return maskShaper;
        }

        DisplayCache displayCache = view.getDisplayCache();
        ImageShaper shaperFromCacheOptions = displayCache != null ? displayCache.options.getShaper() : null;
        if (shaperFromCacheOptions != null) {
            return shaperFromCacheOptions;
        }

        ImageShaper shaperFromOptions = view.getOptions().getShaper();
        if (shaperFromOptions != null) {
            return shaperFromOptions;
        }

        return null;
    }

    public boolean setMaskShaper(@Nullable ImageShaper maskShaper) {
        if (this.maskShaper == maskShaper) {
            return false;
        }

        this.maskShaper = maskShaper;
        return true;
    }

    private class PressedStatusManager extends GestureDetector.SimpleOnGestureListener {

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showProcessed = false;
                view.invalidate();
            }
        };

        @Override
        public boolean onDown(MotionEvent event) {
            showProcessed = false;
            singleTapUp = false;
            view.removeCallbacks(runnable);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);

            showProcessed = true;
            view.invalidate();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            singleTapUp = true;

            if (!showProcessed) {
                showProcessed = true;
                view.invalidate();
            }
            view.postDelayed(runnable, 120);

            return super.onSingleTapUp(e);
        }
    }
}
