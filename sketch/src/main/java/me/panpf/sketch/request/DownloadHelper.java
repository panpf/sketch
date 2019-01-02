/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

/**
 * 下载 Helper，负责组织、收集、初始化下载参数，最后执行 commit() 提交请求
 */
public class DownloadHelper {
    private static final String NAME = "DownloadHelper";

    private Sketch sketch;
    private boolean sync;

    private String uri;
    private UriModel uriModel;
    private String key;
    private DownloadOptions downloadOptions = new DownloadOptions();
    private DownloadListener downloadListener;
    private DownloadProgressListener downloadProgressListener;

    public DownloadHelper(@NonNull Sketch sketch, @NonNull String uri, @Nullable DownloadListener downloadListener) {
        this.sketch = sketch;
        this.uri = uri;
        this.downloadListener = downloadListener;
        this.uriModel = UriModel.match(sketch, uri);
    }

    /**
     * 设置请求 level，限制请求处理深度，参考 {@link RequestLevel}
     *
     * @param requestLevel {@link RequestLevel}
     * @return {@link DownloadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            downloadOptions.setRequestLevel(requestLevel);
        }
        return this;
    }

    /**
     * 禁用磁盘缓存
     *
     * @return {@link DownloadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper disableCacheInDisk() {
        downloadOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 批量设置下载参数（完全覆盖）
     *
     * @param newOptions {@link DownloadOptions}
     * @return {@link DownloadHelper}. 为了支持链式调用
     */
    @NonNull
    public DownloadHelper options(@Nullable DownloadOptions newOptions) {
        downloadOptions.copy(newOptions);
        return this;
    }

    /**
     * 设置下载进度监听器
     *
     * @return {@link DownloadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper downloadProgressListener(@Nullable DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    /**
     * 同步执行
     *
     * @return {@link DownloadHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper sync() {
        this.sync = true;
        return this;
    }

    /**
     * 提交
     *
     * @return {@link DownloadRequest}. 为了支持链式调用
     */
    @Nullable
    public DownloadRequest commit() {
        if (sync && SketchUtils.isMainThread()) {
            throw new IllegalStateException("Cannot sync perform the download in the UI thread ");
        }

        if (!checkParams()) {
            return null;
        }

        if (!checkDiskCache()) {
            return null;
        }

        return submitRequest();
    }

    private boolean checkParams() {
        sketch.getConfiguration().getOptionsFilterManager().filter(downloadOptions);

        if (TextUtils.isEmpty(uri)) {
            SLog.e(NAME, "Uri is empty");
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_INVALID, sync);
            return false;
        }

        if (uriModel == null) {
            SLog.e(NAME, "Not support uri. %s", uri);
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_NO_SUPPORT, sync);
            return false;
        }

        if (!uriModel.isFromNet()) {
            SLog.e(NAME, "Only support http ot https. %s", uri);
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_NO_SUPPORT, sync);
            return false;
        }

        // 根据 URI 和下载选项生成请求 key
        key = SketchUtils.makeRequestKey(uri, uriModel, downloadOptions.makeKey());

        return true;
    }

    private boolean checkDiskCache() {
        if (!downloadOptions.isCacheInDiskDisabled()) {
            DiskCache diskCache = sketch.getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(uriModel.getDiskCacheKey(uri));
            if (diskCacheEntry != null) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "Download image completed. %s", key);
                }
                if (downloadListener != null) {
                    DownloadResult result = new DownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE);
                    downloadListener.onCompleted(result);
                }
                return false;
            }
        }

        return true;
    }

    private DownloadRequest submitRequest() {
        CallbackHandler.postCallbackStarted(downloadListener, sync);

        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        DownloadRequest request = requestFactory.newDownloadRequest(sketch, uri, uriModel, key,
                downloadOptions, downloadListener, downloadProgressListener);
        request.setSync(sync);

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
            SLog.d(NAME, "Run dispatch submitted. %s", key);
        }
        request.submit();

        return request;
    }
}
