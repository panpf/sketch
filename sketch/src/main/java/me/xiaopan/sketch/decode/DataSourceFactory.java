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

import me.xiaopan.sketch.Configuration;
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
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.UriScheme;

public class DataSourceFactory {

    private static final String LOG_NAME = "DataSourceFactory";

    /**
     * 创建数据源，可用于解码
     *
     * @param context        Context
     * @param imageUri       图片uri
     * @param uriScheme      图片类型
     * @param uriContent     uri内容
     * @param options        加载选项
     * @param downloadResult 下载结果
     * @param diskCacheKey   磁盘缓存key
     * @return DataSource
     * @throws DecodeException 无法创建数据源
     */
    // TODO: 2017/5/24 改造成依赖DownloadInfo
    public static DataSource makeDataSource(Context context, String imageUri, UriScheme uriScheme, String uriContent,
                                            LoadOptions options, DownloadResult downloadResult, String diskCacheKey) throws DecodeException {
        // 特殊文件预处理
        Configuration configuration = Sketch.with(context).getConfiguration();
        ImagePreprocessor imagePreprocessor = configuration.getImagePreprocessor();
        if (imagePreprocessor.match(context, imageUri, uriScheme, uriContent, options)) {

            PreProcessResult prePrecessResult = imagePreprocessor.process(context, imageUri, uriScheme, uriContent, options);
            if (prePrecessResult != null && prePrecessResult.diskCacheEntry != null) {
                return new CacheFileDataSource(prePrecessResult.diskCacheEntry, prePrecessResult.imageFrom);
            }

            if (prePrecessResult != null && prePrecessResult.imageData != null) {
                return new ByteArrayDataSource(prePrecessResult.imageData, prePrecessResult.imageFrom);
            }

            SLog.w(SLogType.REQUEST, LOG_NAME, "pre process result is null", imageUri);
            throw new DecodeException("Pre process result is null", ErrorCause.PRE_PROCESS_RESULT_IS_NULL);
        }

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

                SLog.w(SLogType.REQUEST, LOG_NAME, "download result exception. %s", imageUri);
                throw new DecodeException("Download result exception", ErrorCause.DOWNLOAD_RESULT_IS_NULL);
            } else {
                DiskCache.Entry diskCacheEntry = configuration.getDiskCache().get(diskCacheKey);
                if (diskCacheEntry != null) {
                    return new CacheFileDataSource(diskCacheEntry, ImageFrom.DISK_CACHE);
                }

                throw new DecodeException(String.format("Not found disk cache: %s", imageUri), ErrorCause.DOWNLOAD_RESULT_IS_NULL);
            }
        }

        if (uriScheme == UriScheme.FILE) {
            return new FileDataSource(new File(uriContent));
        }

        if (uriScheme == UriScheme.CONTENT) {
            return new ContentDataSource(context, Uri.parse(uriContent));
        }

        if (uriScheme == UriScheme.ASSET) {
            return new AssetsDataSource(context, uriContent);
        }

        if (uriScheme == UriScheme.DRAWABLE) {
            return new DrawableDataSource(context, Integer.valueOf(uriContent));
        }

        SLog.w(SLogType.REQUEST, LOG_NAME, "unknown uri is %s", imageUri);
        throw new DecodeException(String.format("Unknown uri is %s", imageUri), ErrorCause.NOT_FOUND_DATA_SOURCE_BY_UNKNOWN_URI);
    }

    /**
     * 创建数据源时已处理缓存优先
     *
     * @param context                    Context
     * @param imageUri                   图片uri
     * @param uriScheme                  图片类型
     * @param uriContent                 uri内容
     * @param options                    加载选项
     * @param diskCacheKey   磁盘缓存key
     * @param downloadResult             下载结果
     * @param processedImageDiskCacheKey 已处理缓存key
     * @return DataSource
     * @throws DecodeException 无法创建数据源
     */
    public static DataSource processedCacheFirstMakeDataSource(Context context, String imageUri, UriScheme uriScheme,
                                                               String uriContent, LoadOptions options, DownloadResult downloadResult, String diskCacheKey,
                                                               String processedImageDiskCacheKey) throws DecodeException {
        Configuration configuration = Sketch.with(context).getConfiguration();
        ProcessedImageCache processedImageCache = configuration.getProcessedImageCache();

        if (processedImageCache.canUse(options)) {
            DiskCache diskCache = configuration.getDiskCache();
            DataSource dataSource = processedImageCache.getDiskCache(diskCache, processedImageDiskCacheKey);
            if (dataSource != null) {
                return dataSource;
            }
        }

        return makeDataSource(context, imageUri, uriScheme, uriContent, options, downloadResult, diskCacheKey);
    }
}
