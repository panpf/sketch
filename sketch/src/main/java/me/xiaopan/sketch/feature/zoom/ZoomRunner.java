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

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;

class ZoomRunner implements Runnable {

    private final float mFocalX;
    private final float mFocalY;
    private final long mStartTime;
    private final float mZoomStart;
    private final float mZoomEnd;
    private ImageZoomer imageZoomer;

    ZoomRunner(ImageZoomer imageZoomer, final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
        this.imageZoomer = imageZoomer;
        mFocalX = focalX;
        mFocalY = focalY;
        mStartTime = System.currentTimeMillis();
        mZoomStart = currentZoom;
        mZoomEnd = targetZoom;
    }

    @Override
    public void run() {
        if (!imageZoomer.isWorking()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "not working. zoom run");
            }
            return;
        }

        float t = interpolate();
        float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
        float deltaScale = scale / imageZoomer.getZoomScale();
        boolean continueZoom = t < 1f;

        imageZoomer.setZooming(continueZoom);
        imageZoomer.onScale(deltaScale, mFocalX, mFocalY);

        // We haven't hit our target scale yet, so post ourselves again
        if (continueZoom) {
            CompatUtils.postOnAnimation(imageZoomer.getImageView(), this);
        } else {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, ImageZoomer.NAME, "finished. zoom run");
            }
        }
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / imageZoomer.getZoomDuration();
        t = Math.min(1f, t);
        t = imageZoomer.getZoomInterpolator().getInterpolation(t);
        return t;
    }

    public void zoom() {
        imageZoomer.getImageView().post(this);
    }
}
