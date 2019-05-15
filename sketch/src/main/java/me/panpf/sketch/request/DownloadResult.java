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

import me.panpf.sketch.cache.DiskCache;

@SuppressWarnings("WeakerAccess")
public class DownloadResult {
    @Nullable
    private DiskCache.Entry diskCacheEntry;
    @Nullable
    private byte[] imageData;
    @NonNull
    private ImageFrom imageFrom;

    public DownloadResult(@NonNull DiskCache.Entry diskCacheEntry, @NonNull ImageFrom imageFrom) {
        this.diskCacheEntry = diskCacheEntry;
        this.imageFrom = imageFrom;
    }

    public DownloadResult(@NonNull byte[] imageData, @NonNull ImageFrom imageFrom) {
        this.imageData = imageData;
        this.imageFrom = imageFrom;
    }

    @Nullable
    public DiskCache.Entry getDiskCacheEntry() {
        return diskCacheEntry;
    }

    @Nullable
    public byte[] getImageData() {
        return imageData;
    }

    @NonNull
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public boolean hasData() {
        return diskCacheEntry != null || (imageData != null && imageData.length > 0);
    }
}
