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

import me.panpf.sketch.SLog;
import me.panpf.sketch.util.SketchUtils;

class ZoomRunner implements Runnable {

    private final float mFocalX;
    private final float mFocalY;
    private final long mStartTime;
    private final float mZoomStart;
    private final float mZoomEnd;
    
    private ImageZoomer imageZoomer;
    private ScaleDragHelper scaleDragHelper;

    ZoomRunner(ImageZoomer imageZoomer, ScaleDragHelper scaleDragHelper, final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
        this.imageZoomer = imageZoomer;
        this.scaleDragHelper = scaleDragHelper;
        this.mFocalX = focalX;
        this.mFocalY = focalY;
        this.mStartTime = System.currentTimeMillis();
        this.mZoomStart = currentZoom;
        this.mZoomEnd = targetZoom;
    }

    @Override
    public void run() {
        if (!imageZoomer.isWorking()) {
            SLog.w(ImageZoomer.NAME, "not working. zoom run");
            return;
        }

        float t = interpolate();
        float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
        float deltaScale = scale / scaleDragHelper.getZoomScale();
        boolean continueZoom = t < 1f;

        scaleDragHelper.setZooming(continueZoom);
        scaleDragHelper.onScale(deltaScale, mFocalX, mFocalY);

        // We haven't hit our target scale yet, so post ourselves again
        if (continueZoom) {
            SketchUtils.postOnAnimation(imageZoomer.getImageView(), this);
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(ImageZoomer.NAME, "finished. zoom run");
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
