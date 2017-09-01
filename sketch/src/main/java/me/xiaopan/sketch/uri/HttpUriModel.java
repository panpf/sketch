/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.uri;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.datasource.ByteArrayDataSource;
import me.xiaopan.sketch.datasource.DataSource;
import me.xiaopan.sketch.datasource.DiskCacheDataSource;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ImageFrom;

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

    /**
     * 获取 uri 所真正包含的内容部分，但对于 http 格式的 uri 来说就是返回它自己
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，但对于 http 格式的 uri 来说就是返回它自己
     */
    @Override
    public String getUriContent(@NonNull String uri) {
        return uri;
    }

    @Override
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, DownloadResult downloadResult) {
        if (downloadResult != null) {
            DiskCache.Entry diskCacheEntry = downloadResult.getDiskCacheEntry();
            if (diskCacheEntry != null) {
                return new DiskCacheDataSource(diskCacheEntry, downloadResult.getImageFrom());
            }

            byte[] imageDataArray = downloadResult.getImageData();
            if (imageDataArray != null && imageDataArray.length > 0) {
                return new ByteArrayDataSource(imageDataArray, downloadResult.getImageFrom());
            }

            SLog.e(NAME, "Not found data from download result. %s", uri);
            return null;
        } else {
            DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey(uri));
            if (diskCacheEntry != null) {
                return new DiskCacheDataSource(diskCacheEntry, ImageFrom.DISK_CACHE);
            }

            SLog.e(NAME, "Not found disk cache. %s", uri);
            return null;
        }
    }
}
