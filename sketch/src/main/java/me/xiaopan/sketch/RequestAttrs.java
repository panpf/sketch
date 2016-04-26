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

package me.xiaopan.sketch;

public class RequestAttrs {
    private Sketch sketch;  // Sketch
    private String uri;    // 图片地址
    private String name;    // 名称，用于在输出LOG的时候区分不同的请求
    private UriScheme uriScheme;    // Uri协议格式

    public RequestAttrs(Sketch sketch, String uri, UriScheme uriScheme, String name) {
        this.sketch = sketch;
        this.uri = uri;
        this.uriScheme = uriScheme;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Sketch getSketch() {
        return sketch;
    }

    public String getUri() {
        return uri;
    }

    public UriScheme getUriScheme() {
        return uriScheme;
    }

    public Configuration getConfiguration() {
        return sketch.getConfiguration();
    }
}
