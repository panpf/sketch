/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.util.ExifInterface;

public class SketchTransitionDrawable extends TransitionDrawable implements SketchDrawable {
    private SketchDrawable sketchDrawable;

    public SketchTransitionDrawable(Drawable oldDrawable, Drawable newDrawable) {
        super(new Drawable[]{oldDrawable, newDrawable});

        if (newDrawable instanceof SketchDrawable) {
            sketchDrawable = (SketchDrawable) newDrawable;
        }
    }

    @Override
    public String getKey() {
        return sketchDrawable != null ? sketchDrawable.getKey() : null;
    }

    @Override
    public String getUri() {
        return sketchDrawable != null ? sketchDrawable.getUri() : null;
    }

    @Override
    public int getOriginWidth() {
        return sketchDrawable != null ? sketchDrawable.getOriginWidth() : 0;
    }

    @Override
    public int getOriginHeight() {
        return sketchDrawable != null ? sketchDrawable.getOriginHeight() : 0;
    }

    @Override
    public String getMimeType() {
        return sketchDrawable != null ? sketchDrawable.getMimeType() : null;
    }

    @Override
    public int getExifOrientation() {
        return sketchDrawable != null ? sketchDrawable.getExifOrientation() : ExifInterface.ORIENTATION_UNDEFINED;
    }

    @Override
    public int getByteCount() {
        return sketchDrawable != null ? sketchDrawable.getByteCount() : 0;
    }

    @Override
    public Bitmap.Config getBitmapConfig() {
        return sketchDrawable != null ? sketchDrawable.getBitmapConfig() : null;
    }

    @Override
    public ImageFrom getImageFrom() {
        return sketchDrawable != null ? sketchDrawable.getImageFrom() : null;
    }

    @Override
    public String getInfo() {
        return sketchDrawable != null ? sketchDrawable.getInfo() : null;
    }
}
