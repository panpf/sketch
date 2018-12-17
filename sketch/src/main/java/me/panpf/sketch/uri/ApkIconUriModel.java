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
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.util.SketchUtils;

public class ApkIconUriModel extends AbsBitmapDiskCacheUriModel {

    public static final String SCHEME = "apk.icon://";
    private static final String NAME = "ApkIconUriModel";

    @NonNull
    public static String makeUri(@NonNull String filePath) {
        return SCHEME + filePath;
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "apk.icon:///sdcard/test.apk"，就会返回 "/sdcard/test.apk"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "apk.icon:///sdcard/test.apk"，就会返回 "/sdcard/test.apk"
     */
    @NonNull
    @Override
    public String getUriContent(@NonNull String uri) {
        return match(uri) ? uri.substring(SCHEME.length()) : uri;
    }

    @NonNull
    @Override
    public String getDiskCacheKey(@NonNull String uri) {
        return SketchUtils.createFileUriDiskCacheKey(uri, getUriContent(uri));
    }

    @NonNull
    @Override
    protected Bitmap getContent(@NonNull Context context, @NonNull String uri) throws GetDataSourceException {
        BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
        Bitmap iconBitmap = SketchUtils.readApkIcon(context, getUriContent(uri), false, NAME, bitmapPool);

        if (iconBitmap == null || iconBitmap.isRecycled()) {
            String cause = String.format("Apk icon bitmap invalid. %s", uri);
            SLog.e(NAME, cause);
            throw new GetDataSourceException(cause);
        }
        return iconBitmap;
    }
}
