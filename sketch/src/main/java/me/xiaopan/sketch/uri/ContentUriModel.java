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
import android.text.TextUtils;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ByteArrayDataSource;
import me.xiaopan.sketch.decode.CacheFileDataSource;
import me.xiaopan.sketch.decode.ContentDataSource;
import me.xiaopan.sketch.decode.DataSource;
import me.xiaopan.sketch.decode.DecodeException;
import me.xiaopan.sketch.preprocess.ImagePreprocessor;
import me.xiaopan.sketch.preprocess.PreProcessResult;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.UriInfo;

public class ContentUriModel implements UriModel {

    public static final String SCHEME = "content://";
    private static final String NAME = "ContentUriModel";

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
        return false;
    }

    @Override
    public DataSource getDataSource(Context context, UriInfo uriInfo, DownloadResult downloadResult) throws DecodeException {
        if (context == null || uriInfo == null) {
            return null;
        }

        // TODO: 2017/8/31 特殊文件预处理要被 专用的uri 替代
        Configuration configuration = Sketch.with(context).getConfiguration();
        ImagePreprocessor imagePreprocessor = configuration.getImagePreprocessor();
        if (imagePreprocessor.match(context, uriInfo)) {

            PreProcessResult prePrecessResult = imagePreprocessor.process(context, uriInfo);
            if (prePrecessResult != null && prePrecessResult.diskCacheEntry != null) {
                return new CacheFileDataSource(prePrecessResult.diskCacheEntry, prePrecessResult.imageFrom);
            }

            if (prePrecessResult != null && prePrecessResult.imageData != null) {
                return new ByteArrayDataSource(prePrecessResult.imageData, prePrecessResult.imageFrom);
            }

            SLog.w(NAME, "pre process result is null", uriInfo.getUri());
            throw new DecodeException("Pre process result is null", ErrorCause.PRE_PROCESS_RESULT_IS_NULL);
        }
        return new ContentDataSource(context, Uri.parse(uriInfo.getContent()));
    }
}
