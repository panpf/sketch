/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package me.panpf.sketch.zoom;

import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.OverScroller;

import me.panpf.sketch.SLog;
import me.panpf.sketch.util.SketchUtils;

class FlingRunner implements Runnable {
    private ImageZoomer imageZoomer;
    private ScaleDragHelper scaleDragHelper;

    private OverScroller scroller;
    private int currentX;
    private int currentY;

    FlingRunner(ImageZoomer imageZoomer, ScaleDragHelper scaleDragHelper) {
        this.scroller = new OverScroller(imageZoomer.getImageView().getContext());
        this.imageZoomer = imageZoomer;
        this.scaleDragHelper = scaleDragHelper;
    }

    void fling(int velocityX, int velocityY) {
        if (!imageZoomer.isWorking()) {
            SLog.w(ImageZoomer.NAME, "not working. fling");
            return;
        }

        RectF drawRectF = new RectF();
        scaleDragHelper.getDrawRect(drawRectF);
        if (drawRectF.isEmpty()) {
            return;
        }

        Size viewSize = imageZoomer.getViewSize();
        final int imageViewWidth = viewSize.getWidth();
        final int imageViewHeight = viewSize.getHeight();

        final int startX = Math.round(-drawRectF.left);
        final int minX, maxX, minY, maxY;
        if (imageViewWidth < drawRectF.width()) {
            minX = 0;
            maxX = Math.round(drawRectF.width() - imageViewWidth);
        } else {
            minX = maxX = startX;
        }

        final int startY = Math.round(-drawRectF.top);
        if (imageViewHeight < drawRectF.height()) {
            minY = 0;
            maxY = Math.round(drawRectF.height() - imageViewHeight);
        } else {
            minY = maxY = startY;
        }

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
            SLog.d(ImageZoomer.NAME, "fling. start=%dx %d, min=%dx%d, max=%dx%d",
                    startX, startY, minX, minY, maxX, maxY);
        }

        // If we actually can move, fling the scroller
        if (startX != maxX || startY != maxY) {
            currentX = startX;
            currentY = startY;
            scroller.fling(startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY, 0, 0);
        }

        ImageView imageView = imageZoomer.getImageView();
        imageView.removeCallbacks(this);
        imageView.post(this);
    }

    @Override
    public void run() {
        // remaining post that should not be handled
        if (scroller.isFinished()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(ImageZoomer.NAME, "finished. fling run");
            }
            return;
        }

        if (!imageZoomer.isWorking()) {
            SLog.w(ImageZoomer.NAME, "not working. fling run");
            return;
        }

        if (!scroller.computeScrollOffset()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(ImageZoomer.NAME, "scroll finished. fling run");
            }
            return;
        }

        final int newX = scroller.getCurrX();
        final int newY = scroller.getCurrY();
        scaleDragHelper.translateBy(currentX - newX, currentY - newY);
        currentX = newX;
        currentY = newY;

        // Post On animation
        SketchUtils.postOnAnimation(imageZoomer.getImageView(), this);
    }

    @SuppressWarnings("WeakerAccess")
    public void cancelFling() {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
            SLog.d(ImageZoomer.NAME, "cancel fling");
        }

        if (scroller != null) {
            scroller.forceFinished(true);
        }
        imageZoomer.getImageView().removeCallbacks(this);
    }
}
