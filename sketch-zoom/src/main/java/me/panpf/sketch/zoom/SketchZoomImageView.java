/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch.zoom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.SketchImageView;

public class SketchZoomImageView extends SketchImageView {

    @NonNull
    private final ImageZoomer zoomer;

    public SketchZoomImageView(@NonNull Context context) {
        this(context, null, 0);
    }

    public SketchZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SketchZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.zoomer = new ImageZoomer(this);
        addViewFunction(new ImageZoomFunction(zoomer), 0);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (zoomer.isWorking() && scaleType != ScaleType.MATRIX) {
            zoomer.setScaleType(scaleType);
        } else {
            super.setScaleType(scaleType);
        }
    }

    @NonNull
    public ImageZoomer getZoomer() {
        return zoomer;
    }

    @Override
    public boolean isUseSmallerThumbnails() {
        return true;
    }
}
