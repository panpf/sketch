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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;

/**
 * 定位执行器
 */
class LocationRunner implements Runnable {
    private final Scroller mScroller;
    private ImageZoomer imageZoomer;
    private int mCurrentX, mCurrentY;

    LocationRunner(Context context, ImageZoomer imageZoomer) {
        this.mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
        this.imageZoomer = imageZoomer;
    }

    /**
     * 定位到预览图上指定的位置
     */
    boolean location(int startX, int startY, int endX, int endY) {
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
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "finished. location run");
            }
            return;
        }

        if (!imageZoomer.isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. location run");
            }
            mScroller.forceFinished(true);
            return;
        }

        if (!mScroller.computeScrollOffset()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "scroll finished. location run");
            }
            return;
        }

        final int newX = mScroller.getCurrX();
        final int newY = mScroller.getCurrY();
        final float dx = mCurrentX - newX;
        final float dy = mCurrentY - newY;
        imageZoomer.translateBy(dx, dy);
        mCurrentX = newX;
        mCurrentY = newY;

        // Post On animation
        CompatUtils.postOnAnimation(imageZoomer.getImageView(), this);
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
