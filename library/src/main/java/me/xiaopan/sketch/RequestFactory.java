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
public interface RequestFactory {
    DisplayRequest newDisplayRequest(Sketch sketch, String uri, UriScheme uriScheme, String memoryCacheId, SketchImageViewInterface sketchImageViewInterface);
    LoadRequest newLoadRequest(Sketch sketch, String uri, UriScheme uriScheme);
    DownloadRequest newDownloadRequest(Sketch sketch, String uri, UriScheme uriScheme);

    /**
     * 获取标识符
     * @return 标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(StringBuilder builder);
}
