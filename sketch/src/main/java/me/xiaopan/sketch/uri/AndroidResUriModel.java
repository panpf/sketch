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
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.xiaopan.sketch.datasource.ContentDataSource;
import me.xiaopan.sketch.datasource.DataSource;
import me.xiaopan.sketch.request.DownloadResult;

public class AndroidResUriModel extends UriModel {

    public static final String SCHEME = "android.resource://";

    /**
     * 根据资源名称和类型创建 uri
     *
     * @param packageName     包名
     * @param resType         资源类型，例如 "drawable" 或 "mipmap"
     * @param drawableResName 图片资源名称
     * @return 例如：android.resource://me.xiaopan.sketchsample/mipmap/ic_launch
     */
    public static String makeUriByName(@NonNull String packageName, @NonNull String resType, @NonNull String drawableResName) {
        return SCHEME + packageName + "/" + resType + "/" + drawableResName;
    }

    /**
     * 根据资源 ID 创建 uri
     *
     * @param packageName   包名
     * @param drawableResId 图片资源ID
     * @return 例如：android.resource://me.xiaopan.sketchsample/1031232
     */
    @SuppressWarnings("unused")
    public static String makeUriById(@NonNull String packageName, int drawableResId) {
        return SCHEME + packageName + "/" + String.valueOf(drawableResId);
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    @NonNull
    @Override
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, @Nullable DownloadResult downloadResult) {
        return new ContentDataSource(context, Uri.parse(uri));
    }
}
