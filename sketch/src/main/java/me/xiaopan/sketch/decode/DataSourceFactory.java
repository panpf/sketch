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

package me.xiaopan.sketch.decode;

import android.content.Context;
import android.net.Uri;

import java.io.File;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.feature.PreProcessResult;
import me.xiaopan.sketch.feature.ProcessedImageCache;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.UriScheme;

public class DataSourceFactory {

    public static DataSource makeDataSourceByRequest(LoadRequest request, boolean ignoreProcessedCache, String logName) throws DecodeException {
        // 缓存的处理过的图片，可直接读取
        if (!ignoreProcessedCache) {
            ProcessedImageCache processedImageCache = request.getConfiguration().getProcessedImageCache();
            if (processedImageCache.canUse(request.getOptions())) {
                DataSource dataSource = processedImageCache.checkProcessedImageDiskCache(request);
                if (dataSource != null) {
                    return dataSource;
                }
            }
        }

        // 特殊文件的预处理
        ImagePreprocessor imagePreprocessor = request.getConfiguration().getImagePreprocessor();
        if (imagePreprocessor.isSpecific(request)) {
            PreProcessResult prePrecessResult = request.doPreProcess();
            if (prePrecessResult != null && prePrecessResult.diskCacheEntry != null) {
                return new CacheFileDataSource(prePrecessResult.diskCacheEntry, prePrecessResult.imageFrom);
            }

            if (prePrecessResult != null && prePrecessResult.imageData != null) {
                return new ByteArrayDataSource(prePrecessResult.imageData, prePrecessResult.imageFrom);
            }

            SLog.w(SLogType.REQUEST, logName, "pre process result is null", request.getUri());
            throw new DecodeException("pre process result is null", ErrorCause.PRE_PROCESS_RESULT_IS_NULL);
        }

        return makeDataSource(request.getContext(), request.getUri(), request.getUriScheme(),
                request.getRealUri(), request.getDownloadResult(), logName);
    }

    public static DataSource makeDataSource(Context context, String imageUri, UriScheme uriScheme, String realUri,
                                            DownloadResult downloadResult, String logName) throws DecodeException {
        if (uriScheme == UriScheme.NET) {
            if (downloadResult != null) {
                DiskCache.Entry diskCacheEntry = downloadResult.getDiskCacheEntry();
                if (diskCacheEntry != null) {
                    return new CacheFileDataSource(diskCacheEntry, downloadResult.getImageFrom());
                }

                byte[] imageDataArray = downloadResult.getImageData();
                if (imageDataArray != null && imageDataArray.length > 0) {
                    return new ByteArrayDataSource(imageDataArray, downloadResult.getImageFrom());
                }

                SLog.w(SLogType.REQUEST, logName, "download result exception. %s", imageUri);
                throw new DecodeException("download result exception", ErrorCause.DOWNLOAD_RESULT_IS_NULL);
            } else {
                DiskCache.Entry diskCacheEntry = Sketch.with(context).getConfiguration().getDiskCache().get(imageUri);
                if (diskCacheEntry != null) {
                    return new CacheFileDataSource(diskCacheEntry, ImageFrom.DISK_CACHE);
                }

                throw new DecodeException(String.format("Not found disk cache: %s", imageUri), ErrorCause.DOWNLOAD_RESULT_IS_NULL);
            }
        }

        if (uriScheme == UriScheme.FILE) {
            return new FileDataSource(new File(realUri));
        }

        if (uriScheme == UriScheme.CONTENT) {
            return new ContentDataSource(context, Uri.parse(realUri));
        }

        if (uriScheme == UriScheme.ASSET) {
            return new AssetsDataSource(context, realUri);
        }

        if (uriScheme == UriScheme.DRAWABLE) {
            return new DrawableDataSource(context, Integer.valueOf(realUri));
        }

        SLog.w(SLogType.REQUEST, logName, "unknown uri is %s", imageUri);
        throw new DecodeException(String.format("unknown uri is %s", imageUri), ErrorCause.NOT_FOUND_DATA_SOURCE_BY_UNKNOWN_URI);
    }
}
