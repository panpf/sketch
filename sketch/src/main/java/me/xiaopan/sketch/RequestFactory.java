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

/**
 * Request创建工厂
 */
public class RequestFactory {
    private static final String NAME = "RequestFactory";

    public DisplayRequest newDisplayRequest(Sketch sketch, String uri, UriScheme uriScheme, String memoryCacheId, SketchImageViewInterface sketchImageViewInterface) {
        return new DefaultDisplayRequest(sketch, uri, uriScheme, memoryCacheId, sketchImageViewInterface);
    }

    public LoadRequest newLoadRequest(Sketch sketch, String uri, UriScheme uriScheme) {
        return new DefaultLoadRequest(sketch, uri, uriScheme);
    }

    public DownloadRequest newDownloadRequest(Sketch sketch, String uri, UriScheme uriScheme) {
        return new DefaultDownloadRequest(sketch, uri, uriScheme);
    }

    /**
     * 获取标识符
     *
     * @return 标识符
     */
    public String getIdentifier() {
        return NAME;
    }

    /**
     * 追加标识符
     */
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }
}
