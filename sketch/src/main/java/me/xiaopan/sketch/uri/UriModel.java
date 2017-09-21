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
import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.datasource.DataSource;
import me.xiaopan.sketch.request.DownloadResult;

/**
 * 负责某一类型 URI 的全部功能，你可通过此类实现扩展 Sketch 支持的 uri
 */
public abstract class UriModel {

    /**
     * 根据 uri 匹配能处理它的 UriModel
     *
     * @param sketch Sketch
     * @param uri    图片 uri
     * @return 能够处理这种 uri 的 UriModel
     */
    @Nullable
    public static UriModel match(@NonNull Sketch sketch, @NonNull String uri) {
        return !TextUtils.isEmpty(uri) ? sketch.getConfiguration().getUriModelRegistry().match(uri) : null;
    }

    /**
     * 根据 uri 匹配能处理它的 UriModel
     *
     * @param context Context
     * @param uri     图片 uri
     * @return 能够处理这种 uri 的 UriModel
     */
    @Nullable
    public static UriModel match(@NonNull Context context, @NonNull String uri) {
        return match(Sketch.with(context), uri);
    }

    /**
     * 如果返回true，那么后续将使用这个 UriModel 来处理 uri
     *
     * @param uri 图片 uri
     * @return true：能处理
     */
    protected abstract boolean match(@NonNull String uri);

    /**
     * 获取指定 uri 的数据，用于后续解码读取图片
     *
     * @param context        Context
     * @param uri            图片 uri
     * @param downloadResult 下载结果，只对 {@link #isFromNet()} 为 true 的 UriModel 有用
     * @return DataSource
     */
    @NonNull
    public abstract DataSource getDataSource(@NonNull Context context, @NonNull String uri,
                                             @Nullable DownloadResult downloadResult) throws GetDataSourceException;

    /**
     * 获取 uri 中的内容部分，默认是它自己
     *
     * @param uri 图片 uri
     * @return uri 中的内容部分，默认是它自己
     */
    @NonNull
    public String getUriContent(@NonNull String uri) {
        return uri;
    }

    /**
     * 获取指定 uri 的磁盘缓存key，默认返回 uri 自己
     *
     * @param uri 图片 uri
     * @return 指定 uri 的磁盘缓存 key
     */
    @NonNull
    public String getDiskCacheKey(@NonNull String uri) {
        return uri;
    }

    /**
     * 当前类型 uri 的数据是否来自网络
     */
    public boolean isFromNet() {
        return false;
    }

    /**
     * 在生成 key 时，是否需要将 uri 使用 md5 转成短 uri，适用于非常长的 uri，例如 base64 格式的 uri
     */
    public boolean isConvertShortUriForKey() {
        return false;
    }
}
