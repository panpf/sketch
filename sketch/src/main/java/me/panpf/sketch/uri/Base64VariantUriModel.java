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

import androidx.annotation.NonNull;
import android.text.TextUtils;

public class Base64VariantUriModel extends Base64UriModel {

    public static final String SCHEME = "data:img/";

    /**
     * 获取 uri 所真正包含的内容部分，例如 "data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z"，就会返回 "/9j/4QaORX...C8bg/U7T/in//Z"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z"，就会返回 "/9j/4QaORX...C8bg/U7T/in//Z"
     */
    @NonNull
    @Override
    public String getUriContent(@NonNull String uri) {
        return super.getUriContent(uri);
    }

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }
}
