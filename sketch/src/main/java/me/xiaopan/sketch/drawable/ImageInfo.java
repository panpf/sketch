/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.drawable;

public class ImageInfo {
    private String key;
    private String uri;
    private int originWidth;
    private int originHeight;
    private String mimeType;

    public ImageInfo(String key, String uri, String mimeType, int originWidth, int originHeight) {
        this.key = key;
        this.uri = uri;
        this.mimeType = mimeType;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
    }

    public String getKey() {
        return key;
    }

    public String getUri() {
        return uri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getOriginHeight() {
        return originHeight;
    }

    public int getOriginWidth() {
        return originWidth;
    }
}
