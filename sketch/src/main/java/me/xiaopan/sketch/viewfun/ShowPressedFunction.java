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

package me.xiaopan.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.request.DisplayCache;
import me.xiaopan.sketch.shaper.ImageShaper;

/**
 * 显示按下状态，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
 */
public class ShowPressedFunction extends ViewFunction {
    static final int DEFAULT_MASK_COLOR = 0x33000000;
    private static final String NAME = "ShowPressedFunction";

    private FunctionPropertyView view;
    private ImageShaper maskShaper;
    private int maskColor = DEFAULT_MASK_COLOR;

    private int touchX;
    private int touchY;
    private int rippleRadius;
    private boolean allowShowPressedStatus;
    private boolean animationRunning;
    private Paint maskPaint;
    private GestureDetector gestureDetector;
    private boolean showRect;
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
                    allowShowPressedStatus = false;
                    view.invalidate();
                    break;
            }
        }
        return false;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (!allowShowPressedStatus && !animationRunning && !showRect) {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                e.printStackTrace();
            }
        }

        if (maskPaint == null) {
            maskPaint = new Paint();
            maskPaint.setColor(maskColor);
            maskPaint.setAntiAlias(true);
        }
        if (allowShowPressedStatus || animationRunning) {
            canvas.drawCircle(touchX, touchY, rippleRadius, maskPaint);
        } else if (showRect) {
            canvas.drawRect(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom(), maskPaint);
        }

        if (shaper != null) {
            canvas.restore();
        }
    }

    public boolean setMaskColor(@ColorInt int maskColor) {
        if (this.maskColor == maskColor) {
            return false;
        }

        this.maskColor = maskColor;
        return true;
    }

    private ImageShaper getMaskShaper() {
        if (maskShaper != null) {
            return maskShaper;
        }

        DisplayCache displayCache = view.getDisplayCache();
        ImageShaper cacheImageShaper = displayCache != null ? displayCache.options.getImageShaper() : null;
        if (cacheImageShaper != null) {
            return cacheImageShaper;
        }

        ImageShaper shaperFromOptions = view.getOptions().getImageShaper();
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

    private class PressedStatusManager extends GestureDetector.SimpleOnGestureListener implements Runnable {
        private boolean showPress;
        private Scroller scroller;
        private Runnable cancelRunnable;

        public PressedStatusManager() {
            scroller = new Scroller(view.getContext());
        }

        @Override
        public void run() {
            animationRunning = scroller.computeScrollOffset();
            if (animationRunning) {
                rippleRadius = scroller.getCurrX();
                view.post(this);
            }
            view.invalidate();
        }

        @Override
        public boolean onDown(MotionEvent event) {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
                view.removeCallbacks(this);
                animationRunning = false;
                view.invalidate();
            }

            touchX = (int) event.getX();
            touchY = (int) event.getY();
            showPress = false;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            allowShowPressedStatus = true;
            showPress = true;
            startAnimation(1000);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!showPress) {
                showRect = true;
                view.invalidate();
                if (cancelRunnable == null) {
                    cancelRunnable = new Runnable() {
                        @Override
                        public void run() {
                            showRect = false;
                            view.invalidate();
                        }
                    };
                }
                view.postDelayed(cancelRunnable, 200);
            }
            return super.onSingleTapUp(e);
        }

        private void startAnimation(int duration) {
            if (scroller == null) {
                scroller = new Scroller(view.getContext(), new DecelerateInterpolator());
            }
            scroller.startScroll(0, 0, computeRippleRadius(), 0, duration);
            view.post(this);
        }

        /**
         * 计算涟漪的半径
         *
         * @return 涟漪的半径
         */
        private int computeRippleRadius() {
            // 先计算按下点到四边的距离
            int toLeftDistance = touchX - view.getPaddingLeft();
            int toTopDistance = touchY - view.getPaddingTop();
            int toRightDistance = Math.abs(view.getWidth() - view.getPaddingRight() - touchX);
            int toBottomDistance = Math.abs(view.getHeight() - view.getPaddingBottom() - touchY);

            // 当按下位置在第一或第四象限的时候，比较按下位置在左上角到右下角这条线上距离谁最远就以谁为半径，否则在左下角到右上角这条线上比较
            int centerX = view.getWidth() / 2;
            int centerY = view.getHeight() / 2;
            if ((touchX < centerX && touchY < centerY) || (touchX > centerX && touchY > centerY)) {
                int toLeftTopDistance = (int) Math.sqrt((toLeftDistance * toLeftDistance) + (toTopDistance * toTopDistance));
                int toRightBottomDistance = (int) Math.sqrt((toRightDistance * toRightDistance) + (toBottomDistance * toBottomDistance));
                return toLeftTopDistance > toRightBottomDistance ? toLeftTopDistance : toRightBottomDistance;
            } else {
                int toLeftBottomDistance = (int) Math.sqrt((toLeftDistance * toLeftDistance) + (toBottomDistance * toBottomDistance));
                int toRightTopDistance = (int) Math.sqrt((toRightDistance * toRightDistance) + (toTopDistance * toTopDistance));
                return toLeftBottomDistance > toRightTopDistance ? toLeftBottomDistance : toRightTopDistance;
            }
        }
    }
}
