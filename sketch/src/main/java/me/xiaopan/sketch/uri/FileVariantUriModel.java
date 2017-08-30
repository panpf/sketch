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

import android.text.TextUtils;

public class FileVariantUriModel extends FileUriModel {

    public static final String SCHEME = "file://";

    @SuppressWarnings("unused")
    public static String makeUri(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return !filePath.startsWith(SCHEME) ? SCHEME + filePath : filePath;
    }

    @Override
    public boolean match(String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    @Override
    public String getUriContent(String uri) {
        return !TextUtils.isEmpty(uri) ? uri.substring(SCHEME.length()) : uri;
    }
}
