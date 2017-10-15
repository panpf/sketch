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

package me.xiaopan.sketch.zoom;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.zoom.gestures.OnScaleDragGestureListener;

class ZoomRunner implements Runnable {

    private final float mFocalX;
    private final float mFocalY;
    private final long mStartTime;
    private final float mZoomStart;
    private final float mZoomEnd;
    private ZoomManager zoomManager;
    private OnScaleDragGestureListener scaleDragGestureListener;

    ZoomRunner(ZoomManager zoomManager, OnScaleDragGestureListener scaleDragGestureListener, final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
        this.zoomManager = zoomManager;
        this.scaleDragGestureListener = scaleDragGestureListener;
        mFocalX = focalX;
        mFocalY = focalY;
        mStartTime = System.currentTimeMillis();
        mZoomStart = currentZoom;
        mZoomEnd = targetZoom;
    }

    @Override
    public void run() {
        if (!zoomManager.getImageZoomer().isWorking()) {
            SLog.w(ImageZoomer.NAME, "not working. zoom run");
            return;
        }

        float t = interpolate();
        float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
        float deltaScale = scale / zoomManager.getZoomScale();
        boolean continueZoom = t < 1f;

        zoomManager.setZooming(continueZoom);
        scaleDragGestureListener.onScale(deltaScale, mFocalX, mFocalY);

        // We haven't hit our target scale yet, so post ourselves again
        if (continueZoom) {
            CompatUtils.postOnAnimation(zoomManager.getImageZoomer().getImageView(), this);
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(ImageZoomer.NAME, "finished. zoom run");
            }
        }
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / zoomManager.getImageZoomer().getZoomDuration();
        t = Math.min(1f, t);
        t = zoomManager.getImageZoomer().getZoomInterpolator().getInterpolation(t);
        return t;
    }

    public void zoom() {
        zoomManager.getImageZoomer().getImageView().post(this);
    }
}
