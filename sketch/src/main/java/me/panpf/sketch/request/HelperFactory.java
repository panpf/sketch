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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchView;

/**
 * 负责创建 {@link DisplayHelper}、{@link LoadHelper}、{@link DownloadHelper}
 */
@SuppressWarnings("WeakerAccess")
public class HelperFactory {
    private static final String KEY = "HelperFactory";

    @Nullable
    private DisplayHelper cacheDisplayHelper;

    @NonNull
    public DownloadHelper getDownloadHelper(@NonNull Sketch sketch, @NonNull String uri, @Nullable DownloadListener listener) {
        return new DownloadHelper(sketch, uri, listener);
    }

    @NonNull
    public LoadHelper getLoadHelper(@NonNull Sketch sketch, @NonNull String uri, @Nullable LoadListener listener) {
        return new LoadHelper(sketch, uri, listener);
    }

    @NonNull
    public DisplayHelper getDisplayHelper(@NonNull Sketch sketch, @NonNull String uri, @NonNull SketchView sketchView) {
        if (this.cacheDisplayHelper == null) {
            this.cacheDisplayHelper = new DisplayHelper();
        }

        DisplayHelper displayHelper = this.cacheDisplayHelper;
        this.cacheDisplayHelper = null;
        displayHelper.init(sketch, uri, sketchView);
        return displayHelper;
    }

    /**
     * 用完了要回收
     */
    public void recycleDisplayHelper(@NonNull DisplayHelper displayHelper) {
        displayHelper.reset();
        if (this.cacheDisplayHelper == null) {
            this.cacheDisplayHelper = displayHelper;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return KEY;
    }
}