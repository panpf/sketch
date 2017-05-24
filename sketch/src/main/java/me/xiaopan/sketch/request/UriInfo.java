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

package me.xiaopan.sketch.request;

import android.text.TextUtils;

public class UriInfo {
    private String uri;
    private String content;
    private UriScheme scheme;

    private UriInfo() {

    }

    public static UriInfo make(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        UriScheme uriScheme = UriScheme.valueOfUri(uri);

        UriInfo uriInfo = new UriInfo();
        uriInfo.uri = uri;
        uriInfo.scheme = uriScheme;
        uriInfo.content = uriScheme != null ? uriScheme.cropContent(uri) : null;
        return uriInfo;
    }

    /**
     * 获取uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * 获取uri内容，例如asset://test.png的内容就是test.png
     */
    public String getContent() {
        return content;
    }

    /**
     * 获取uri协议
     */
    public UriScheme getScheme() {
        return scheme;
    }

    /**
     * 获取磁盘缓存key
     */
    public String getDiskCacheKey() {
        return scheme == UriScheme.BASE64 && !TextUtils.isEmpty(content) ? content : uri;
    }
}
