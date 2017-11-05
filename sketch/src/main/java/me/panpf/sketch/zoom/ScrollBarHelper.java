/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import me.panpf.sketch.SLog;
import me.panpf.sketch.util.SketchUtils;

class ScrollBarHelper {

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

    ScrollBarHelper(Context context, ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;

        scrollBarPaint = new Paint();
        scrollBarPaint.setColor(Color.parseColor("#000000"));
        scrollBarPaint.setAlpha(scrollBarAlpha);
        scrollBarSize = SketchUtils.dp2px(context, 3);
        scrollBarMargin = SketchUtils.dp2px(context, 3);
        scrollBarRadius = Math.round(scrollBarSize / 2);

        handler = new Handler(Looper.getMainLooper());
        hiddenScrollBarRunner = new HiddenScrollBarRunner();
        fadeScrollBarRunner = new FadeScrollBarRunner(context);
    }

    void onDraw(Canvas canvas) {
        final RectF drawRectF = tempDisplayRectF;
        imageZoomer.getDrawRect(drawRectF);
        if (drawRectF.isEmpty()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(ImageZoomer.NAME, "displayRectF is empty. drawScrollBar. drawRectF=%s", drawRectF.toString());
            }
            return;
        }

        Size viewSize = imageZoomer.getViewSize();
        final int viewWidth = viewSize.getWidth();
        final int viewHeight = viewSize.getHeight();
        final float displayWidth = drawRectF.width();
        final float displayHeight = drawRectF.height();

        if (viewWidth <= 0 || viewHeight <= 0 || displayWidth == 0 || displayHeight == 0) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(ImageZoomer.NAME, "size is 0. drawScrollBar. viewSize=%dx%d, displaySize=%sx%s",
                        viewWidth, viewHeight, displayWidth, displayHeight);
            }
            return;
        }

        ImageView imageView = imageZoomer.getImageView();
        final int finalViewWidth = viewWidth - (scrollBarMargin * 2);
        final int finalViewHeight = viewHeight - (scrollBarMargin * 2);

        if ((int) displayWidth > viewWidth) {
            final float widthScale = (float) viewWidth / displayWidth;
            final int horScrollBarWidth = (int) (finalViewWidth * widthScale);

            RectF horScrollBarRectF = scrollBarRectF;
            horScrollBarRectF.setEmpty();
            horScrollBarRectF.left = imageView.getPaddingLeft() + scrollBarMargin + (drawRectF.left < 0 ? (int) ((Math.abs(drawRectF.left) / drawRectF.width()) * finalViewWidth) : 0);
            horScrollBarRectF.top = imageView.getPaddingTop() + scrollBarMargin + finalViewHeight - scrollBarSize;
            horScrollBarRectF.right = horScrollBarRectF.left + horScrollBarWidth;
            horScrollBarRectF.bottom = horScrollBarRectF.top + scrollBarSize;
            canvas.drawRoundRect(horScrollBarRectF, scrollBarRadius, scrollBarRadius, scrollBarPaint);
        }

        if ((int) displayHeight > viewHeight) {
            final float heightScale = (float) viewHeight / displayHeight;
            final int verScrollBarHeight = (int) (finalViewHeight * heightScale);

            RectF verScrollBarRectF = scrollBarRectF;
            verScrollBarRectF.setEmpty();
            verScrollBarRectF.left = imageView.getPaddingLeft() + scrollBarMargin + finalViewWidth - scrollBarSize;
            verScrollBarRectF.top = imageView.getPaddingTop() + scrollBarMargin + (drawRectF.top < 0 ? (int) ((Math.abs(drawRectF.top) / drawRectF.height()) * finalViewHeight) : 0);
            verScrollBarRectF.right = verScrollBarRectF.left + scrollBarSize;
            verScrollBarRectF.bottom = verScrollBarRectF.top + verScrollBarHeight;
            canvas.drawRoundRect(verScrollBarRectF, scrollBarRadius, scrollBarRadius, scrollBarPaint);
        }
    }

    /**
     * 此方法里没有执行 imageView.invalidate()，因为回调的地方会有执行
     */
    void onMatrixChanged() {
        scrollBarPaint.setAlpha(scrollBarAlpha);

        if (fadeScrollBarRunner.isRunning()) {
            fadeScrollBarRunner.abort();
        }
        handler.removeCallbacks(hiddenScrollBarRunner);

        handler.postDelayed(hiddenScrollBarRunner, 800);
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

        FadeScrollBarRunner(Context context) {
            this.scroller = new Scroller(context, new DecelerateInterpolator());
            scroller.forceFinished(true);
        }

        public void start() {
            scroller.startScroll(scrollBarAlpha, 0, -scrollBarAlpha, 0, 300);
            handler.post(this);
        }

        boolean isRunning() {
            return !scroller.isFinished();
        }

        void abort() {
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
