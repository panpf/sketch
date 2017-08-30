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

import android.content.Context;
import android.text.TextUtils;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.uri.UriModel;
import me.xiaopan.sketch.uri.UriModelRegistry;

// TODO: 2017/8/30 采用 UriModel 之后似乎就没有存在的必要了
@Deprecated
public class UriInfo {
    private String uri;
    private String content;
    private UriModel uriModel;

    private UriInfo() {

    }

    public static UriInfo make(UriModelRegistry uriModelRegistry, String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        UriModel uriModel = uriModelRegistry.match(uri);

        UriInfo uriInfo = new UriInfo();
        uriInfo.uri = uri;
        uriInfo.uriModel = uriModel;
        uriInfo.content = uriModel != null ? uriModel.getUriContent(uri) : null;
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
    @Deprecated
    public UriScheme getScheme() {
        return uriModel != null ? uriModel.getUriScheme() : null;
    }

    /**
     * 获取磁盘缓存key
     */
    @Deprecated
    public String getDiskCacheKey() {
        return uriModel != null ? uriModel.getDiskCacheKey(uri) : uri;
    }
}
