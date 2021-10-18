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

package com.github.panpf.sketch.request;

import androidx.annotation.NonNull;

import com.github.panpf.sketch.decode.ImageAttrs;
import com.github.panpf.sketch.drawable.SketchGifDrawable;

public class GifLoadResult implements LoadResult {
    @NonNull
    private SketchGifDrawable gifDrawable;
    @NonNull
    private ImageFrom imageFrom;
    @NonNull
    private ImageAttrs imageAttrs;

    public GifLoadResult(@NonNull SketchGifDrawable gifDrawable, @NonNull ImageAttrs imageAttrs, @NonNull ImageFrom imageFrom) {
        this.gifDrawable = gifDrawable;
        this.imageAttrs = imageAttrs;
        this.imageFrom = imageFrom;
    }

    @NonNull
    public SketchGifDrawable getGifDrawable() {
        return gifDrawable;
    }

    @NonNull
    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @NonNull
    @Override
    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }
}
