/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.uri.UriModel;

/**
 * 负责创建 {@link DisplayRequest}、{@link LoadRequest}、{@link DownloadRequest}
 */
@SuppressWarnings("WeakerAccess")
public class RequestFactory {
    private static final String KEY = "RequestFactory";

    public DisplayRequest newDisplayRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel,
                                            @NonNull String key, @NonNull DisplayOptions displayOptions,
                                            @NonNull ViewInfo viewInfo, @NonNull RequestAndViewBinder requestAndViewBinder,
                                            @Nullable DisplayListener displayListener, @Nullable DownloadProgressListener downloadProgressListener) {
        // 由于DisplayHelper会被重复利用
        // 因此ViewInfo和DisplayOptions不能直接拿来用，要重新New一个
        return new FreeRideDisplayRequest(sketch, uri, uriModel, key, new DisplayOptions(displayOptions),
                new ViewInfo(viewInfo), requestAndViewBinder, displayListener, downloadProgressListener);
    }

    public LoadRequest newLoadRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel,
                                      @NonNull String key, @NonNull LoadOptions options,
                                      @Nullable LoadListener listener, @Nullable DownloadProgressListener downloadProgressListener) {
        return new LoadRequest(sketch, uri, uriModel, key, options, listener, downloadProgressListener);
    }

    public DownloadRequest newDownloadRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel,
                                              @NonNull String key, @NonNull DownloadOptions options,
                                              @Nullable DownloadListener listener, @Nullable DownloadProgressListener downloadProgressListener) {
        return new FreeRideDownloadRequest(sketch, uri, uriModel, key, options, listener, downloadProgressListener);
    }

    @NonNull
    @Override
    public String toString() {
        return KEY;
    }
}
