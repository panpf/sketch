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

package me.panpf.sketch.request;

import android.widget.ImageView.ScaleType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchView;

@SuppressWarnings("WeakerAccess")
public class ViewInfo {
    @Nullable
    private ScaleType scaleType;
    @Nullable
    private FixedSize fixedSize;
    private boolean useSmallerThumbnails;

    public ViewInfo() {

    }

    public ViewInfo(@NonNull ViewInfo viewInfo) {
        copy(viewInfo);
    }

    public void copy(@NonNull ViewInfo viewInfo) {
        this.scaleType = viewInfo.scaleType;
        this.fixedSize = viewInfo.fixedSize;
        this.useSmallerThumbnails = viewInfo.useSmallerThumbnails;
    }

    public void reset(@Nullable SketchView sketchView, @Nullable Sketch sketch) {
        if (sketchView != null && sketch != null) {
            this.scaleType = sketchView.getScaleType();
            this.fixedSize = sketch.getConfiguration().getSizeCalculator().calculateImageFixedSize(sketchView);
            this.useSmallerThumbnails = sketchView.isUseSmallerThumbnails();
        } else {
            this.scaleType = null;
            this.fixedSize = null;
            this.useSmallerThumbnails = false;
        }
    }

    @Nullable
    public FixedSize getFixedSize() {
        return fixedSize;
    }

    @Nullable
    public ScaleType getScaleType() {
        return scaleType;
    }

    public boolean isUseSmallerThumbnails() {
        return useSmallerThumbnails;
    }
}
