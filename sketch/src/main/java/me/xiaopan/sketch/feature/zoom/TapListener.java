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

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.xiaopan.sketch.util.SketchUtils;

class TapListener extends GestureDetector.SimpleOnGestureListener {

    private ImageZoomer imageZoomer;

    TapListener(ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        ImageView imageView = imageZoomer.getImageView();
        if (imageView != null && imageZoomer.getOnViewTapListener() != null) {
            imageZoomer.getOnViewTapListener().onViewTap(imageView, e.getX(), e.getY());
        }

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent ev) {
        float scale = SketchUtils.formatFloat(imageZoomer.getZoomScale(), 2);

        float[] doubleClickZoomScales = imageZoomer.getDoubleClickZoomScales();
        if (doubleClickZoomScales.length < 2) {
            return true;
        }
        float finalScale = doubleClickZoomScales[0];
        for (int w = doubleClickZoomScales.length - 1; w >= 0; w--) {
            float currentScale = doubleClickZoomScales[w];
            if (scale < SketchUtils.formatFloat(currentScale, 2)) {
                finalScale = currentScale;
                break;
            }
        }

        try {
            imageZoomer.zoom(finalScale, ev.getX(), ev.getY(), true);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Can sometimes happen when getX() and getY() is called
        }

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // Wait for the confirmed onDoubleTap() instead
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);

        ImageView imageView = imageZoomer.getImageView();
        if (imageView != null && imageZoomer.getOnViewLongPressListener() != null) {
            imageZoomer.getOnViewLongPressListener().onViewLongPress(imageView, e.getX(), e.getY());
        }
    }
}
