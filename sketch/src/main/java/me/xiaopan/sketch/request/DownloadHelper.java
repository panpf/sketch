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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.uri.UriModel;
import me.xiaopan.sketch.util.SketchUtils;

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
     * 禁用磁盘缓存
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper disableCacheInDisk() {
        downloadOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 设置请求 Level
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            downloadOptions.setRequestLevel(requestLevel);
            downloadOptions.setRequestLevelFrom(null);
        }
        return this;
    }

    /**
     * 批量设置下载参数（完全覆盖）
     */
    @NonNull
    public DownloadHelper options(@Nullable DownloadOptions newOptions) {
        downloadOptions.copy(newOptions);
        return this;
    }

    /**
     * 设置下载进度监听器
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper downloadProgressListener(@Nullable DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    /**
     * 同步处理
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper sync() {
        this.sync = true;
        return this;
    }

    /**
     * 提交
     */
    @Nullable
    public DownloadRequest commit() {
        if (sync && SketchUtils.isMainThread()) {
            throw new IllegalStateException("Cannot sync perform the download in the UI thread ");
        }

        if (!checkParam()) {
            return null;
        }

        preProcess();

        if (!checkDiskCache()) {
            return null;
        }

        return submitRequest();
    }

    private boolean checkParam() {
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

        return true;
    }

    /**
     * 对属性进行预处理
     */
    protected void preProcess() {
        // 暂停下载对于下载请求并不起作用，就相当于暂停加载对加载请求并不起作用一样，因此这里不予处理

        // 根据 URI 和下载选项生成请求 key
        key = SketchUtils.makeRequestKey(uri, uriModel, downloadOptions);
    }

    private boolean checkDiskCache() {
        if (!downloadOptions.isCacheInDiskDisabled()) {
            DiskCache diskCache = sketch.getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(uriModel.getDiskCacheKey(uri));
            if (diskCacheEntry != null) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "image download completed. %s", key);
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
        request.submit();

        return request;
    }
}
