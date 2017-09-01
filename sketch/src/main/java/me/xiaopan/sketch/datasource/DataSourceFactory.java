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

package me.xiaopan.sketch.datasource;

import android.content.Context;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.ProcessedImageCache;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.uri.UriModel;

// TODO: 2017/8/31 重构
public class DataSourceFactory {

    /**
     * 创建数据源，可用于解码
     *
     * @param context        Context
     * @param uri            图片uri
     * @param uriModel       uri model
     * @param downloadResult 下载结果
     * @return DataSource
     */
    public static DataSource makeDataSource(Context context, String uri, UriModel uriModel, DownloadResult downloadResult) {
        DataSource dataSource = uriModel.getDataSource(context, uri, downloadResult);
        if (dataSource != null) {
            return dataSource;
        }

        return null;
    }

    /**
     * 创建数据源时已处理缓存优先
     *
     * @param context                    Context
     * @param uri                        图片uri
     * @param uriModel                   uri model
     * @param options                    加载选项
     * @param downloadResult             下载结果
     * @param processedImageDiskCacheKey 已处理缓存key
     * @return DataSource
     */
    public static DataSource processedCacheFirstMakeDataSource(Context context, String uri, UriModel uriModel, DownloadResult downloadResult,
                                                               LoadOptions options, String processedImageDiskCacheKey) {
        Configuration configuration = Sketch.with(context).getConfiguration();
        ProcessedImageCache processedImageCache = configuration.getProcessedImageCache();

        if (processedImageCache.canUse(options)) {
            DiskCache diskCache = configuration.getDiskCache();
            DataSource dataSource = processedImageCache.getDiskCache(diskCache, processedImageDiskCacheKey);
            if (dataSource != null) {
                return dataSource;
            }
        }

        return makeDataSource(context, uri, uriModel, downloadResult);
    }
}
