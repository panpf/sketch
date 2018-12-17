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
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Base64UriModel extends AbsStreamDiskCacheUriModel {

    public static final String SCHEME = "data:image/";

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z"，就会返回 "/9j/4QaORX...C8bg/U7T/in//Z"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z"，就会返回 "/9j/4QaORX...C8bg/U7T/in//Z"
     */
    @NonNull
    @Override
    public String getUriContent(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) ? uri.substring(uri.indexOf(";") + ";base64,".length()) : uri;
    }

    @NonNull
    @Override
    public String getDiskCacheKey(@NonNull String uri) {
        return getUriContent(uri);
    }

    @Override
    public boolean isConvertShortUriForKey() {
        return true;
    }

    @NonNull
    @Override
    protected InputStream getContent(@NonNull Context context, @NonNull String uri) throws GetDataSourceException {
        return new ByteArrayInputStream(Base64.decode(getUriContent(uri), Base64.DEFAULT));
    }
}
