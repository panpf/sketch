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
import androidx.annotation.Nullable;

import com.github.panpf.sketch.SketchImageView;
import com.github.panpf.sketch.state.StateImage;
import com.github.panpf.sketch.uri.UriModel;

public class DownloadOptions {

    /**
     * Disabled disk caching
     */
    private boolean cacheInDiskDisabled;

    /**
     * Limit request processing depth
     */
    @Nullable
    private RequestLevel requestLevel;

    public DownloadOptions() {
    }

    public DownloadOptions(@NonNull DownloadOptions from) {
        copy(from);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCacheInDiskDisabled() {
        return cacheInDiskDisabled;
    }

    @NonNull
    public DownloadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        this.cacheInDiskDisabled = cacheInDiskDisabled;
        return this;
    }

    @Nullable
    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    @NonNull
    public DownloadOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }

    public void reset() {
        cacheInDiskDisabled = false;
        requestLevel = null;
    }

    public void copy(@Nullable DownloadOptions options) {
        if (options == null) {
            return;
        }

        cacheInDiskDisabled = options.cacheInDiskDisabled;
        requestLevel = options.requestLevel;
    }

    /**
     * Generate option key for assembling the request key
     *
     * @see SketchImageView#getOptionsKey()
     * @see com.github.panpf.sketch.util.SketchUtils#makeRequestKey(String, UriModel, String)
     */
    @NonNull
    public String makeKey() {
        return "";
    }

    /**
     * Generate option key for {@link StateImage} to assemble the memory cache for {@link StateImage}
     *
     * @see com.github.panpf.sketch.util.SketchUtils#makeRequestKey(String, UriModel, String)
     */
    @NonNull
    public String makeStateImageKey() {
        return "";
    }
}
