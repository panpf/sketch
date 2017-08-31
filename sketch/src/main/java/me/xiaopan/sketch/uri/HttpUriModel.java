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
import android.text.TextUtils;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.ByteArrayDataSource;
import me.xiaopan.sketch.decode.DiskCacheDataSource;
import me.xiaopan.sketch.decode.DataSource;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.UriInfo;

public class HttpUriModel implements UriModel {

    public static final String SCHEME = "http://";
    private static final String NAME = "HttpUriModel";

    @Override
    public boolean match(String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    @Override
    public String getUriContent(String uri) {
        return uri;
    }

    @Override
    public String getDiskCacheKey(String uri) {
        return uri;
    }

    @Override
    public boolean isFromNet() {
        return true;
    }

    @Override
    public DataSource getDataSource(Context context, UriInfo uriInfo, DownloadResult downloadResult) {
        if (downloadResult != null) {
            DiskCache.Entry diskCacheEntry = downloadResult.getDiskCacheEntry();
            if (diskCacheEntry != null) {
                return new DiskCacheDataSource(diskCacheEntry, downloadResult.getImageFrom());
            }

            byte[] imageDataArray = downloadResult.getImageData();
            if (imageDataArray != null && imageDataArray.length > 0) {
                return new ByteArrayDataSource(imageDataArray, downloadResult.getImageFrom());
            }

            SLog.e(NAME, "Not found data from download result. %s", uriInfo.getUri());
            return null;
        } else {
            Configuration configuration = Sketch.with(context).getConfiguration();
            DiskCache.Entry diskCacheEntry = configuration.getDiskCache().get(uriInfo.getDiskCacheKey());
            if (diskCacheEntry != null) {
                return new DiskCacheDataSource(diskCacheEntry, ImageFrom.DISK_CACHE);
            }

            SLog.e(NAME, "Not found disk cache. %s", uriInfo.getUri());
            return null;
        }
    }
}
