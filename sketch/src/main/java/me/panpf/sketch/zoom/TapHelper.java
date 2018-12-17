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

package me.panpf.sketch.zoom;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import me.panpf.sketch.util.SketchUtils;
import me.panpf.sketch.viewfun.FunctionCallbackView;

class TapHelper extends GestureDetector.SimpleOnGestureListener {

    @NonNull
    private ImageZoomer imageZoomer;
    @NonNull
    private GestureDetector tapGestureDetector;

    TapHelper(@NonNull Context appContext, @NonNull ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;
        this.tapGestureDetector = new GestureDetector(appContext, this);
    }

    boolean onTouchEvent(MotionEvent event) {
        return tapGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        ImageView imageView = imageZoomer.getImageView();
        ImageZoomer.OnViewTapListener tapListener = imageZoomer.getOnViewTapListener();
        if (tapListener != null) {
            tapListener.onViewTap(imageView, e.getX(), e.getY());
            return true;
        }

        if (imageView instanceof FunctionCallbackView) {
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
        if (longPressListener != null) {
            longPressListener.onViewLongPress(imageView, e.getX(), e.getY());
            return;
        }

        if (imageView instanceof FunctionCallbackView) {
            FunctionCallbackView functionCallbackView = (FunctionCallbackView) imageView;
            View.OnLongClickListener longClickListener = functionCallbackView.getOnLongClickListener();
            if (longClickListener != null && functionCallbackView.isLongClickable()) {
                longClickListener.onLongClick(imageView);
            }
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent ev) {
        try {
            float currentScaleFormat = SketchUtils.formatFloat(imageZoomer.getZoomScale(), 2);
            float finalScale = -1;
            for (float scale : imageZoomer.getZoomScales().getZoomScales()) {
                if (finalScale == -1) {
                    finalScale = scale;
                } else if (currentScaleFormat < SketchUtils.formatFloat(scale, 2)) {
                    finalScale = scale;
                    break;
                }
            }
            imageZoomer.zoom(finalScale, true);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Can sometimes happen when getX() and getY() is called
        }

        return true;
    }
}
