/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

public abstract class BaseInfo {
    private String uri;
    private String realUri;    // 真正的图片地址，例如原图片uri是asset://test.png的，realUri就是test.png
    private UriScheme uriScheme;    // Uri协议类型

    private String key; // key是由uri和选项组成的

    public BaseInfo(BaseInfo info) {
        copy(info);
    }

    public BaseInfo() {

    }

    void reset(String uri) {
        if (uri != null) {
            this.uri = uri;
            this.uriScheme = UriScheme.valueOfUri(uri);
            this.realUri = uriScheme != null ? uriScheme.crop(uri) : null;

            this.key = null;
        } else {
            this.uri = null;
            this.uriScheme = null;
            this.realUri = null;

            this.key = null;
        }
    }

    void copy(BaseInfo info) {
        this.uri = info.uri;
        this.realUri = info.realUri;
        this.uriScheme = info.uriScheme;

        this.key = info.key;
    }

    public String getKey() {
        return key;
    }

    void setKey(String key) {
        this.key = key;
    }

    public String getUri() {
        return uri;
    }

    public String getRealUri() {
        return realUri;
    }

    public UriScheme getUriScheme() {
        return uriScheme;
    }
}
