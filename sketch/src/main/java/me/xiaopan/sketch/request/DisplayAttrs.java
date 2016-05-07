/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.request;

import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.Sketch;

public class DisplayAttrs {
    private String memoryCacheId;
    private ScaleType scaleType;
    private FixedSize fixedSize;

    public DisplayAttrs() {

    }

    public DisplayAttrs(DisplayAttrs displayAttrs) {
        copy(displayAttrs);
    }

    public void copy(DisplayAttrs displayAttrs) {
        this.memoryCacheId = displayAttrs.memoryCacheId;
        this.scaleType = displayAttrs.scaleType;
        this.fixedSize = displayAttrs.fixedSize;
    }

    public void reset(ImageViewInterface imageViewInterface, Sketch sketch) {
        if (imageViewInterface != null) {
            this.memoryCacheId = null;
            this.scaleType = imageViewInterface.getScaleType();
            this.fixedSize = sketch.getConfiguration().getImageSizeCalculator().calculateImageFixedSize(imageViewInterface);
        } else {
            this.memoryCacheId = null;
            this.scaleType = null;
            this.fixedSize = null;
        }
    }

    void setMemoryCacheId(String memoryCacheId) {
        this.memoryCacheId = memoryCacheId;
    }

    public FixedSize getFixedSize() {
        return fixedSize;
    }

    public String getMemoryCacheId() {
        return memoryCacheId;
    }

    public ScaleType getScaleType() {
        return scaleType;
    }
}
