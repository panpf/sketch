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

package me.xiaopan.sketch.request;

import me.xiaopan.sketch.cache.DiskCache;

public class DownloadResult {
    private DiskCache.Entry diskCacheEntry;
    private byte[] imageData;
    private boolean fromNetwork;

    public DownloadResult(DiskCache.Entry diskCacheEntry, boolean fromNetwork) {
        this.diskCacheEntry = diskCacheEntry;
        this.fromNetwork = fromNetwork;
    }

    public DownloadResult(byte[] imageData, boolean fromNetwork) {
        this.imageData = imageData;
        this.fromNetwork = fromNetwork;
    }

    public DiskCache.Entry getDiskCacheEntry() {
        return diskCacheEntry;
    }

    public boolean isFromNetwork() {
        return fromNetwork;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public ImageFrom getImageFrom(){
        return fromNetwork ? ImageFrom.NETWORK : ImageFrom.DISK_CACHE;
    }
}
