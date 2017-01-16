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

package me.xiaopan.sketch.feature.zoom;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.widget.ImageView;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.feature.zoom.scrollerproxy.ScrollerProxy;

class FlingTranslateRunner implements Runnable {
    private final ScrollerProxy mScroller;
    private ImageZoomer imageZoomer;
    private int mCurrentX, mCurrentY;

    FlingTranslateRunner(Context context, ImageZoomer imageZoomer) {
        this.mScroller = ScrollerProxy.getScroller(context);
        this.imageZoomer = imageZoomer;
    }

    void fling(int velocityX, int velocityY) {
        if (!imageZoomer.isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. fling");
            }
            return;
        }

        RectF drawRectF = new RectF();
        imageZoomer.getDrawRect(drawRectF);
        if (drawRectF.isEmpty()) {
            return;
        }

        Point imageViewSize = imageZoomer.getImageViewSize();
        final int imageViewWidth = imageViewSize.x;
        final int imageViewHeight = imageViewSize.y;

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

        if (SLogType.ZOOM.isEnabled()) {
            SLog.d(SLogType.ZOOM, ImageZoomer.NAME, "fling. start=%dx %d, min=%dx%d, max=%dx%d",
                    startX, startY, minX, minY, maxX, maxY);
        }

        // If we actually can move, fling the scroller
        if (startX != maxX || startY != maxY) {
            mCurrentX = startX;
            mCurrentY = startY;
            mScroller.fling(startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY, 0, 0);
        }

        ImageView imageView = imageZoomer.getImageView();
        imageView.removeCallbacks(this);
        imageView.post(this);
    }

    @Override
    public void run() {
        // remaining post that should not be handled
        if (mScroller.isFinished()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "finished. fling run");
            }
            return;
        }

        if (!imageZoomer.isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. fling run");
            }
            return;
        }

        if (!mScroller.computeScrollOffset()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "scroll finished. fling run");
            }
            return;
        }

        final int newX = mScroller.getCurrX();
        final int newY = mScroller.getCurrY();
        imageZoomer.translateBy(mCurrentX - newX, mCurrentY - newY);
        mCurrentX = newX;
        mCurrentY = newY;

        // Post On animation
        CompatUtils.postOnAnimation(imageZoomer.getImageView(), this);
    }

    @SuppressWarnings("WeakerAccess")
    public void cancelFling() {
        if (SLogType.ZOOM.isEnabled()) {
            SLog.d(SLogType.ZOOM, ImageZoomer.NAME, "cancel fling");
        }

        if (mScroller != null) {
            mScroller.forceFinished(true);
        }
        ImageView imageView = imageZoomer.getImageView();
        if (imageView != null) {
            imageView.removeCallbacks(this);
        }
    }
}
