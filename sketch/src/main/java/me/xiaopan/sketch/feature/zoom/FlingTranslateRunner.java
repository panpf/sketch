/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package me.xiaopan.sketch.feature.zoom;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.zoom.scrollerproxy.ScrollerProxy;

class FlingTranslateRunner implements Runnable {
    private final ScrollerProxy mScroller;
    private ImageZoomer imageZoomer;
    private int mCurrentX, mCurrentY;

    public FlingTranslateRunner(Context context, ImageZoomer imageZoomer) {
        this.mScroller = ScrollerProxy.getScroller(context);
        this.imageZoomer = imageZoomer;
    }

    public void fling(int velocityX, int velocityY) {
        ImageView imageView = imageZoomer.getImageView();
        if (imageView == null) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, ImageZoomer.NAME + ". fling. imageView is null");
            }
            return;
        }

        final RectF displayRectF = new RectF();
        imageZoomer.checkMatrixBounds();
        imageZoomer.getDisplayRect(displayRectF);
        if (displayRectF.isEmpty()) {
            return;
        }

        final int startX = Math.round(-displayRectF.left);
        final int minX, maxX, minY, maxY;
        int viewWidth = imageZoomer.getImageViewWidth();
        if (viewWidth < displayRectF.width()) {
            minX = 0;
            maxX = Math.round(displayRectF.width() - viewWidth);
        } else {
            minX = maxX = startX;
        }

        int viewHeight = imageZoomer.getImageViewHeight();
        final int startY = Math.round(-displayRectF.top);
        if (viewHeight < displayRectF.height()) {
            minY = 0;
            maxY = Math.round(displayRectF.height() - viewHeight);
        } else {
            minY = maxY = startY;
        }

        mCurrentX = startX;
        mCurrentY = startY;

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, ImageZoomer.NAME + ". fling" +
                    ". StartX: " + startX + ", StartY: " + startY +
                    ", MaxX: " + maxX + ", MaxY: " + maxY);
        }

        // If we actually can move, fling the scroller
        if (startX != maxX || startY != maxY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY, 0, 0);
        }

        imageView.removeCallbacks(this);
        imageView.post(this);
    }

    @Override
    public void run() {
        // remaining post that should not be handled
        if (mScroller.isFinished()) {
            return;
        }

        ImageView imageView = imageZoomer.getImageView();
        if (null != imageView && mScroller.computeScrollOffset()) {

            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();

            imageZoomer.translateBy(mCurrentX - newX, mCurrentY - newY);

            mCurrentX = newX;
            mCurrentY = newY;

            // Post On animation
            CompatUtils.postOnAnimation(imageView, this);
        }
    }

    public void cancelFling() {
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, ImageZoomer.NAME + ". cancel fling");
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
