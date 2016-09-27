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
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 定位执行器
 */
class LocationRunner implements Runnable {
    private final Scroller mScroller;
    private ImageZoomer imageZoomer;
    private int mCurrentX, mCurrentY;

    LocationRunner(Context context, ImageZoomer imageZoomer) {
        this.mScroller = new Scroller(context, new DecelerateInterpolator());
        this.imageZoomer = imageZoomer;
    }

    boolean start(float x, float y) {
        Point imageViewSize = imageZoomer.getImageViewSize();
        if (imageViewSize.x == 0 || imageViewSize.y == 0) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, ImageZoomer.NAME + ". location start. imageView is null");
            }
            return false;
        }

        final int imageViewWidth = imageViewSize.x;
        final int imageViewHeight = imageViewSize.y;

        // 充满的时候是无法移动的，因此先放到最大
        final float scale = SketchUtils.formatFloat(imageZoomer.getZoomScale(), 2);
        final float fullZoomScale = SketchUtils.formatFloat(imageZoomer.getFullZoomScale(), 2);
        if (scale == fullZoomScale) {
            float[] zoomScales = imageZoomer.getDoubleClickZoomScales();
            imageZoomer.zoom(zoomScales[zoomScales.length - 1], false);
        }

        RectF drawRectF = new RectF();
        imageZoomer.getDrawRect(drawRectF);

        // 传进来的位置是预览图上的位置，需要乘以当前的缩放倍数才行
        final float currentScale = imageZoomer.getZoomScale();
        final int scaleLocationX = (int) (x * currentScale);
        final int scaleLocationY = (int) (y * currentScale);
        final int trimScaleLocationX = Math.min(Math.max(scaleLocationX, 0), (int) drawRectF.width());
        final int trimScaleLocationY = Math.min(Math.max(scaleLocationY, 0), (int) drawRectF.height());

        // 让定位点显示在屏幕中间
        final int centerLocationX = trimScaleLocationX - (imageViewWidth / 2);
        final int centerLocationY = trimScaleLocationY - (imageViewHeight / 2);
        final int trimCenterLocationX = Math.max(centerLocationX, 0);
        final int trimCenterLocationY = Math.max(centerLocationY, 0);

        // 当前显示区域的left和top就是开始位置
        final int startX = Math.abs((int) drawRectF.left);
        final int startY = Math.abs((int) drawRectF.top);
        //noinspection UnnecessaryLocalVariable
        final int endX = trimCenterLocationX;
        //noinspection UnnecessaryLocalVariable
        final int endY = trimCenterLocationY;

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, ImageZoomer.NAME + ". location. start=" + startX + "x" + startY + ", end=" + endX + "x" + endY);
        }

        mCurrentX = startX;
        mCurrentY = startY;
        mScroller.startScroll(startX, startY, endX - startX, endY - startY, 300);

        ImageView imageView = imageZoomer.getImageView();
        imageView.removeCallbacks(this);
        imageView.post(this);
        return true;
    }

    @Override
    public void run() {
        // remaining post that should not be handled
        if (mScroller.isFinished()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, ImageZoomer.NAME + ". location. finished");
            }
            return;
        }

        ImageView imageView = imageZoomer.getImageView();
        if (imageView == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, ImageZoomer.NAME + ". location run. imageView is null");
            }
            return;
        }

        if (!mScroller.computeScrollOffset()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, ImageZoomer.NAME + ". location. scroll finished");
            }
            return;
        }

        final int newX = mScroller.getCurrX();
        final int newY = mScroller.getCurrY();
        final float dx = mCurrentX - newX;
        final float dy = mCurrentY - newY;
        imageZoomer.translateBy(dx, dy);
        if (Sketch.isDebugMode()) {
            RectF drawRectF = new RectF();
            imageZoomer.getDrawRect(drawRectF);
            Log.w(Sketch.TAG, ImageZoomer.NAME + ". location. scrolling. d=" + dx + "x" + dy + ", point=" + drawRectF.left + "x" + drawRectF.top);
        }
        mCurrentX = newX;
        mCurrentY = newY;

        // Post On animation
        CompatUtils.postOnAnimation(imageView, this);
    }

    boolean isRunning() {
        return !mScroller.isFinished();
    }

    void cancel() {
        mScroller.forceFinished(true);
        ImageView imageView = imageZoomer.getImageView();
        if (imageView != null) {
            imageView.removeCallbacks(this);
        }
    }
}
