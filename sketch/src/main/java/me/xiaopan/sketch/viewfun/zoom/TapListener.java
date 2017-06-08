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

package me.xiaopan.sketch.viewfun.zoom;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.viewfun.FunctionCallbackView;

class TapListener extends GestureDetector.SimpleOnGestureListener {

    private ImageZoomer imageZoomer;

    TapListener(ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        ImageView imageView = imageZoomer.getImageView();

        ImageZoomer.OnViewTapListener tapListener = imageZoomer.getOnViewTapListener();
        if (imageView != null && tapListener != null) {
            tapListener.onViewTap(imageView, e.getX(), e.getY());
            return true;
        }

        if (imageView != null && imageView instanceof FunctionCallbackView) {
            FunctionCallbackView functionCallbackView = (FunctionCallbackView) imageView;
            View.OnClickListener clickListener = functionCallbackView.getOnClickListener();
            if (clickListener != null && functionCallbackView.isClickable()) {
                clickListener.onClick(imageView);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);

        ImageView imageView = imageZoomer.getImageView();

        ImageZoomer.OnViewLongPressListener longPressListener = imageZoomer.getOnViewLongPressListener();
        if (imageView != null && longPressListener != null) {
            longPressListener.onViewLongPress(imageView, e.getX(), e.getY());
            return;
        }

        if (imageView != null && imageView instanceof FunctionCallbackView) {
            FunctionCallbackView functionCallbackView = (FunctionCallbackView) imageView;
            View.OnLongClickListener longClickListener = functionCallbackView.getOnLongClickListener();
            if (longClickListener != null && functionCallbackView.isLongClickable()) {
                longClickListener.onLongClick(imageView);
            }
        }
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
}
