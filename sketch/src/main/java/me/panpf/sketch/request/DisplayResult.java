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

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import me.panpf.sketch.decode.ImageAttrs;

@SuppressWarnings("WeakerAccess")
public class DisplayResult {
    @NonNull
    private Drawable drawable;
    @NonNull
    private ImageAttrs imageAttrs;
    @NonNull
    private ImageFrom imageFrom;

    public DisplayResult(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
        this.drawable = drawable;
        this.imageFrom = imageFrom;
        this.imageAttrs = imageAttrs;
    }

    @NonNull
    public Drawable getDrawable() {
        return drawable;
    }

    @NonNull
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @NonNull
    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }
}
