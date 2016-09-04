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

    public TapListener(ImageZoomer imageZoomer) {
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
        try {
            float scale = imageZoomer.getZoomScale();
            float x = ev.getX();
            float y = ev.getY();

            if (SketchUtils.formatFloat(scale, 2) < SketchUtils.formatFloat(imageZoomer.getMaxZoomScale(), 2)) {
                imageZoomer.zoom(imageZoomer.getMaxZoomScale(), x, y, true);
            } else {
                imageZoomer.zoom(imageZoomer.getMinZoomScale(), x, y, true);
            }
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
}
