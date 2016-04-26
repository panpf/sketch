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
public class RequestFactory implements Identifier{
    private static final String NAME = "RequestFactory";

    public DisplayRequest newDisplayRequest(RequestAttrs attrs, String memoryCacheId, FixedSize fixedSize, SketchImageViewInterface sketchImageViewInterface, DisplayOptions options, DisplayListener listener) {
        // 由于DisplayHelper会被重复利用，因此其DisplayOptions不能直接拿来用，要重新New一个
        return new DefaultDisplayRequest(attrs, memoryCacheId, fixedSize, sketchImageViewInterface, new DisplayOptions(options), listener);
    }

    public LoadRequest newLoadRequest(RequestAttrs attrs, LoadOptions options, LoadListener listener) {
        return new DefaultLoadRequest(attrs, options, listener);
    }

    public DownloadRequest newDownloadRequest(RequestAttrs attrs, DownloadOptions options, DownloadListener listener) {
        return new DefaultDownloadRequest(attrs, options, listener);
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }
}
