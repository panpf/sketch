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

package com.github.panpf.sketch.uri;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.Sketch;
import com.github.panpf.sketch.cache.DiskCache;
import com.github.panpf.sketch.datasource.ByteArrayDataSource;
import com.github.panpf.sketch.datasource.DataSource;
import com.github.panpf.sketch.datasource.DiskCacheDataSource;
import com.github.panpf.sketch.request.BytesDownloadResult;
import com.github.panpf.sketch.request.CacheDownloadResult;
import com.github.panpf.sketch.request.DownloadResult;
import com.github.panpf.sketch.request.ImageFrom;

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
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, @Nullable DownloadResult downloadResult) throws GetDataSourceException {
        if (downloadResult instanceof BytesDownloadResult) {
            return new ByteArrayDataSource(((BytesDownloadResult) downloadResult).getImageData(), downloadResult.getImageFrom());
        } else if (downloadResult instanceof CacheDownloadResult) {
            return new DiskCacheDataSource(((CacheDownloadResult) downloadResult).getDiskCacheEntry(), downloadResult.getImageFrom());
        } else if (downloadResult == null) {
            DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey(uri));
            if (diskCacheEntry != null) {
                return new DiskCacheDataSource(diskCacheEntry, ImageFrom.DISK_CACHE);
            } else {
                String cause = String.format("Not found disk cache. %s", uri);
                SLog.em(NAME, cause);
                throw new GetDataSourceException(cause);
            }
        } else {
            String cause = String.format("Not found data from download result. %s", uri);
            SLog.em(NAME, cause);
            throw new GetDataSourceException(cause);
        }
    }
}
