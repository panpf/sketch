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

package me.panpf.sketch.uri;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.datasource.ByteArrayDataSource;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.datasource.DiskCacheDataSource;
import me.panpf.sketch.request.DownloadResult;
import me.panpf.sketch.request.ImageFrom;

public class HttpUriModel extends UriModel {

    public static final String SCHEME = "http://";
    private static final String NAME = "HttpUriModel";

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    @Override
    public boolean isFromNet() {
        return true;
    }

    @NonNull
    @Override
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, DownloadResult downloadResult) throws GetDataSourceException {
        if (downloadResult != null) {
            DiskCache.Entry diskCacheEntry = downloadResult.getDiskCacheEntry();
            if (diskCacheEntry != null) {
                return new DiskCacheDataSource(diskCacheEntry, downloadResult.getImageFrom());
            }

            byte[] imageDataArray = downloadResult.getImageData();
            if (imageDataArray != null && imageDataArray.length > 0) {
                return new ByteArrayDataSource(imageDataArray, downloadResult.getImageFrom());
            }

            String cause = String.format("Not found data from download result. %s", uri);
            SLog.e(NAME, cause);
            throw new GetDataSourceException(cause);
        } else {
            DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey(uri));
            if (diskCacheEntry != null) {
                return new DiskCacheDataSource(diskCacheEntry, ImageFrom.DISK_CACHE);
            }

            String cause = String.format("Not found disk cache. %s", uri);
            SLog.e(NAME, cause);
            throw new GetDataSourceException(cause);
        }
    }
}
