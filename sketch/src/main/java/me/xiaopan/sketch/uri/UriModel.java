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
 * 负责某一类型 URI 的
 */
public abstract class UriModel {

    /**
     * 根据 uri 匹配能处理它的 UriModel
     *
     * @param sketch Sketch
     * @param uri    图片 uri
     * @return 能够处理这种 uri 的 UriModel
     */
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
    public static UriModel match(@NonNull Context context, @NonNull String uri) {
        return match(Sketch.with(context), uri);
    }

    /**
     * 当前 UriModel 能够处理指定 uri
     *
     * @param uri 图片 uri
     * @return true：能处理
     */
    protected abstract boolean match(@NonNull String uri);

    /**
     * 获取 uri 所真正包含的内容部分，例如 "drawable://1242141"，就会返回 "1242141"，不同的 UriModel 规则不一样
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "drawable://1242141"，就会返回 "1242141"，不同的 UriModel 规则不一样
     */
    public abstract String getUriContent(@NonNull String uri);

    @NonNull
    public String getDiskCacheKey(@NonNull String uri) {
        return uri;
    }

    public boolean isFromNet() {
        return false;
    }

    @Nullable
    public abstract DataSource getDataSource(@NonNull Context context, @NonNull String uri, @Nullable DownloadResult downloadResult);

    /**
     * 在生成 key时，是否需要将 uri 使用 md5 转一下转成短 uri，如果uri非常长，那么将会对生成的 key 有很大响应
     */
    public boolean isConvertShortUriForKey() {
        return false;
    }
}
