/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import com.github.panpf.sketch.request.DisplayRequest;
import com.github.panpf.sketch.request.ImageFrom;
import com.github.panpf.sketch.util.DrawableWrapper;
import com.github.panpf.sketch.util.ExifInterface;

/**
 * 加载中占位图专用 {@link Drawable}，可绑定请求
 */
public class SketchLoadingDrawable extends DrawableWrapper implements SketchRefDrawable {

    @NonNull
    private WeakReference<DisplayRequest> weakReference;

    @Nullable
    private SketchRefDrawable refDrawable;
    @Nullable
    private SketchDrawable sketchDrawable;

    public SketchLoadingDrawable(@Nullable Drawable drawable, @NonNull DisplayRequest displayRequest) {
        super(drawable);
        this.weakReference = new WeakReference<>(displayRequest);

        if (drawable instanceof SketchRefDrawable) {
            refDrawable = (SketchRefDrawable) drawable;
        }
        if (drawable instanceof SketchDrawable) {
            sketchDrawable = (SketchDrawable) drawable;
        }
    }

    @Nullable
    public DisplayRequest getRequest() {
        return weakReference.get();
    }

    @Override
    public void setIsDisplayed(@NonNull String callingStation, boolean displayed) {
        if (refDrawable != null) {
            refDrawable.setIsDisplayed(callingStation, displayed);
        }
    }

    @Override
    public void setIsWaitingUse(@NonNull String callingStation, boolean waitingUse) {
        if (refDrawable != null) {
            refDrawable.setIsWaitingUse(callingStation, waitingUse);
        }
    }

    @Override
    public boolean isRecycled() {
        return refDrawable != null && refDrawable.isRecycled();
    }

    @Nullable
    @Override
    public String getKey() {
        return sketchDrawable != null ? sketchDrawable.getKey() : null;
    }

    @Nullable
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

    @Nullable
    @Override
    public Bitmap.Config getBitmapConfig() {
        return sketchDrawable != null ? sketchDrawable.getBitmapConfig() : null;
    }

    @Nullable
    @Override
    public ImageFrom getImageFrom() {
        return sketchDrawable != null ? sketchDrawable.getImageFrom() : null;
    }

    @Override
    public String getInfo() {
        return sketchDrawable != null ? sketchDrawable.getInfo() : null;
    }
}
