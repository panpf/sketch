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

package me.xiaopan.sketch.feature.zoom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

public class ScrollBarManager {

    private ImageZoomer imageZoomer;
    private Paint scrollBarPaint;

    private int scrollBarSize;
    private int scrollBarMargin;
    private int scrollBarRadius;
    private int scrollBarAlpha = 51;
    private RectF scrollBarRectF = new RectF();
    private RectF tempDisplayRectF = new RectF();

    private Handler handler;
    private HiddenScrollBarRunner hiddenScrollBarRunner;
    private FadeScrollBarRunner fadeScrollBarRunner;

    public ScrollBarManager(Context context, ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;

        scrollBarPaint = new Paint();
        scrollBarPaint.setColor(Color.parseColor("#000000"));
        scrollBarPaint.setAlpha(scrollBarAlpha);
        scrollBarSize = SketchUtils.dp2px(context, 3);
        scrollBarMargin = SketchUtils.dp2px(context, 3);
        scrollBarRadius = SketchUtils.dp2px(context, 20);

        handler = new Handler(Looper.getMainLooper());
        hiddenScrollBarRunner = new HiddenScrollBarRunner();
        fadeScrollBarRunner = new FadeScrollBarRunner(context);
    }

    public void drawScrollBar(Canvas canvas) {
        ImageView imageView = imageZoomer.getImageView();
        if (imageView == null) {
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, ImageZoomer.NAME + ". drawable not available. drawScrollBar");
            }
            return;
        }

        final RectF displayRectF = tempDisplayRectF;
        imageZoomer.getDisplayRect(displayRectF);
        if (displayRectF.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, ImageZoomer.NAME + ". displayRectF is empty. drawScrollBar. displayRectF=" + displayRectF.toString());
            }
            return;
        }

        final int viewWidth = imageZoomer.getImageViewWidth();
        final int viewHeight = imageZoomer.getImageViewHeight();
        final float displayWidth = displayRectF.width();
        final float displayHeight = displayRectF.height();

        if (viewWidth <= 0 || viewHeight <= 0 || displayWidth == 0 || displayHeight == 0) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, ImageZoomer.NAME + ". size is 0. drawScrollBar" +
                        ". viewSize=" + viewWidth + "x" + viewHeight +
                        ", displaySize=" + displayWidth + "x" + displayHeight);
            }
            return;
        }

        final int finalViewWidth = viewWidth - (scrollBarMargin * 2);
        final int finalViewHeight = viewHeight - (scrollBarMargin * 2);

        if ((int) displayWidth > viewWidth) {
            final float widthScale = (float) viewWidth / displayWidth;
            final int horScrollBarWidth = (int) (finalViewWidth * widthScale);

            RectF horScrollBarRectF = scrollBarRectF;
            horScrollBarRectF.setEmpty();
            horScrollBarRectF.left = imageView.getPaddingLeft() + scrollBarMargin + (displayRectF.left < 0 ? (int) ((Math.abs(displayRectF.left) / displayRectF.width()) * finalViewWidth) : 0);
            horScrollBarRectF.top = imageView.getPaddingTop() + scrollBarMargin + finalViewHeight - scrollBarSize;
            horScrollBarRectF.right = horScrollBarRectF.left + horScrollBarWidth;
            horScrollBarRectF.bottom = horScrollBarRectF.top + scrollBarSize;
            canvas.drawRoundRect(horScrollBarRectF, scrollBarRadius, scrollBarRadius, scrollBarPaint);

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, ImageZoomer.NAME + ". draw horizontal scroll bar. drawScrollBar" +
                        ". viewWidth=" + viewWidth +
                        ", displayWidth=" + displayWidth +
                        ", widthScale=" + widthScale +
                        ", scrollBarSize=" + scrollBarSize +
                        ", scrollBarMargin=" + scrollBarMargin +
                        ", horScrollBarWidth=" + horScrollBarWidth +
                        ", horScrollBarRectF=" + horScrollBarRectF.toString());
            }
        }

        if ((int) displayHeight > viewHeight) {
            final float heightScale = (float) viewHeight / displayHeight;
            final int verScrollBarHeight = (int) (finalViewHeight * heightScale);

            RectF verScrollBarRectF = scrollBarRectF;
            verScrollBarRectF.setEmpty();
            verScrollBarRectF.left = imageView.getPaddingLeft() + scrollBarMargin + finalViewWidth - scrollBarSize;
            verScrollBarRectF.top = imageView.getPaddingTop() + scrollBarMargin + (displayRectF.top < 0 ? (int) ((Math.abs(displayRectF.top) / displayRectF.height()) * finalViewHeight) : 0);
            verScrollBarRectF.right = verScrollBarRectF.left + scrollBarSize;
            verScrollBarRectF.bottom = verScrollBarRectF.top + verScrollBarHeight;
            canvas.drawRoundRect(verScrollBarRectF, scrollBarRadius, scrollBarRadius, scrollBarPaint);

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, ImageZoomer.NAME + ". draw vertical scroll bar. drawScrollBar" +
                        ". viewHeight=" + viewHeight +
                        ", displayHeight=" + displayHeight +
                        ", heightScale=" + heightScale +
                        ", scrollBarSize=" + scrollBarSize +
                        ", scrollBarMargin=" + scrollBarMargin +
                        ", verScrollBarHeight=" + verScrollBarHeight +
                        ", verScrollBarRectF=" + verScrollBarRectF.toString());
            }
        }
    }

    public void matrixChanged() {
        scrollBarPaint.setAlpha(scrollBarAlpha);

        if (fadeScrollBarRunner.isRunning()) {
            fadeScrollBarRunner.abort();
        }
        handler.removeCallbacks(hiddenScrollBarRunner);

        handler.postDelayed(hiddenScrollBarRunner, 300);
    }

    private void invalidateView() {
        ImageView imageView = imageZoomer.getImageView();
        if (imageView != null) {
            imageView.invalidate();
        }
    }

    private class HiddenScrollBarRunner implements Runnable {

        @Override
        public void run() {
            fadeScrollBarRunner.start();
        }
    }

    private class FadeScrollBarRunner implements Runnable {
        private Scroller scroller;

        public FadeScrollBarRunner(Context context) {
            this.scroller = new Scroller(context, new DecelerateInterpolator());
            scroller.forceFinished(true);
        }

        public void start() {
            scroller.startScroll(scrollBarAlpha, 0, -scrollBarAlpha, 0, 300);
            handler.post(this);
        }

        public boolean isRunning() {
            return !scroller.isFinished();
        }

        public void abort() {
            scroller.forceFinished(true);
        }

        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                scrollBarPaint.setAlpha(scroller.getCurrX());
                invalidateView();
                handler.postDelayed(this, 60);
            }
        }
    }
}
