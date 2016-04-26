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

package me.xiaopan.sketch;

import android.widget.ImageView.ScaleType;

public class DisplayAttrs {
    private String memoryCacheId;
    private ScaleType scaleType;
    private FixedSize fixedSize;
    private SketchBinder sketchBinder;

    public DisplayAttrs(String memoryCacheId, FixedSize fixedSize, ImageViewInterface imageViewInterface) {
        this.memoryCacheId = memoryCacheId;
        this.fixedSize = fixedSize;
        this.sketchBinder = new SketchBinder(imageViewInterface);
        this.scaleType = imageViewInterface.getScaleType();
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

    public SketchBinder getSketchBinder() {
        return sketchBinder;
    }
}
