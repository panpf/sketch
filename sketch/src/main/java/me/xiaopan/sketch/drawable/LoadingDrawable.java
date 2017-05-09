/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.util.DrawableWrapper;

/**
 * 加载中占位图专用Drawable，可绑定请求
 */
public class LoadingDrawable extends DrawableWrapper implements RefDrawable {
    private WeakReference<DisplayRequest> weakReference;

    private RefDrawable refDrawable;
    private SketchDrawable sketchDrawable;

    public LoadingDrawable(Drawable drawable, DisplayRequest displayRequest) {
        super(drawable);
        this.weakReference = new WeakReference<>(displayRequest);

        if (drawable instanceof RefDrawable) {
            refDrawable = (RefDrawable) drawable;
        }
        if (drawable instanceof SketchDrawable) {
            sketchDrawable = (SketchDrawable) drawable;
        }
    }

    public DisplayRequest getRequest() {
        return weakReference.get();
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean displayed) {
        if (refDrawable != null) {
            refDrawable.setIsDisplayed(callingStation, displayed);
        }
    }

    @Override
    public void setIsCached(String callingStation, boolean cached) {
        if (refDrawable != null) {
            refDrawable.setIsCached(callingStation, cached);
        }
    }

    @Override
    public void setIsWaitingUse(String callingStation, boolean waitingUse) {
        if (refDrawable != null) {
            refDrawable.setIsWaitingUse(callingStation, waitingUse);
        }
    }

    @Override
    public boolean isRecycled() {
        return refDrawable != null && refDrawable.isRecycled();
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
    public int getOrientation() {
        return sketchDrawable != null ? sketchDrawable.getOrientation() : 0;
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
    public void setImageFrom(ImageFrom imageFrom) {
        if (sketchDrawable != null) {
            sketchDrawable.setImageFrom(imageFrom);
        }
    }

    @Override
    public String getInfo() {
        return sketchDrawable != null ? sketchDrawable.getInfo() : null;
    }
}
