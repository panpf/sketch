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

package me.xiaopan.sketch.feture;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.request.DisplayAttrs;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.DownloadListener;
import me.xiaopan.sketch.request.DownloadOptions;
import me.xiaopan.sketch.request.DownloadProgressListener;
import me.xiaopan.sketch.request.DownloadRequest;
import me.xiaopan.sketch.request.LoadListener;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.RequestAttrs;

/**
 * Request创建工厂
 */
public class RequestFactory implements Identifier {
    private static final String NAME = "RequestFactory";

    public DisplayRequest newDisplayRequest(RequestAttrs attrs, DisplayAttrs displayAttrs, DisplayOptions options, DisplayListener listener, DownloadProgressListener progressListener) {
        // 由于DisplayHelper会被重复利用，因此其DisplayOptions不能直接拿来用，要重新New一个
        return new DisplayRequest(attrs, displayAttrs, new DisplayOptions(options), listener, progressListener);
    }

    public LoadRequest newLoadRequest(RequestAttrs attrs, LoadOptions options, LoadListener listener, DownloadProgressListener progressListener) {
        return new LoadRequest(attrs, options, listener, progressListener);
    }

    public DownloadRequest newDownloadRequest(RequestAttrs attrs, DownloadOptions options, DownloadListener listener, DownloadProgressListener progressListener) {
        return new DownloadRequest(attrs, options, listener, progressListener);
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
