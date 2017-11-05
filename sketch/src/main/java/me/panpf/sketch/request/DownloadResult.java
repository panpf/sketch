/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

import me.panpf.sketch.cache.DiskCache;

public class DownloadResult {
    private DiskCache.Entry diskCacheEntry;
    private byte[] imageData;
    private ImageFrom imageFrom;

    public DownloadResult(DiskCache.Entry diskCacheEntry, ImageFrom imageFrom) {
        this.diskCacheEntry = diskCacheEntry;
        this.imageFrom = imageFrom;
    }

    public DownloadResult(byte[] imageData, ImageFrom imageFrom) {
        this.imageData = imageData;
        this.imageFrom = imageFrom;
    }

    public DiskCache.Entry getDiskCacheEntry() {
        return diskCacheEntry;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public boolean hasData() {
        return diskCacheEntry != null || (imageData != null && imageData.length > 0);
    }
}
