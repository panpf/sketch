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

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 下载Helper，负责组织、收集、初始化下载参数，最后执行commit()提交请求
 */
public class DownloadHelper {
    private static final String LOG_NAME = "DownloadHelper";

    private Sketch sketch;
    private boolean sync;

    private UriInfo uriInfo;
    private String key;
    private DownloadOptions downloadOptions = new DownloadOptions();
    private DownloadListener downloadListener;
    private DownloadProgressListener downloadProgressListener;

    public DownloadHelper(Sketch sketch, String uri) {
        this.sketch = sketch;
        this.uriInfo = UriInfo.make(uri);
    }

    /**
     * 禁用磁盘缓存
     */
    @SuppressWarnings("unused")
    public DownloadHelper disableCacheInDisk() {
        downloadOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 设置请求Level
     */
    @SuppressWarnings("unused")
    public DownloadHelper requestLevel(RequestLevel requestLevel) {
        if (requestLevel != null) {
            downloadOptions.setRequestLevel(requestLevel);
            downloadOptions.setRequestLevelFrom(null);
        }
        return this;
    }

    /**
     * 批量设置下载参数（完全覆盖）
     */
    public DownloadHelper options(DownloadOptions newOptions) {
        downloadOptions.copy(newOptions);
        return this;
    }

    /**
     * 设置下载监听器
     */
    public DownloadHelper listener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    /**
     * 设置下载进度监听器
     */
    @SuppressWarnings("unused")
    public DownloadHelper downloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    /**
     * 同步处理
     */
    @SuppressWarnings("unused")
    public DownloadHelper sync() {
        this.sync = true;
        return this;
    }

    /**
     * 提交
     */
    public DownloadRequest commit() {
        if (sync && SketchUtils.isMainThread()) {
            throw new IllegalStateException("Cannot sync perform the download in the UI thread ");
        }

        CallbackHandler.postCallbackStarted(downloadListener, sync);

        if (!checkUri()) {
            return null;
        }

        preProcess();

        if (!checkDiskCache()) {
            return null;
        }

        return submitRequest();
    }

    private boolean checkUri() {
        if (uriInfo == null) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.e(SLogType.REQUEST, LOG_NAME, "uri is null or empty");
            }
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_NULL_OR_EMPTY, sync);
            return false;
        }

        if (uriInfo.getScheme() == null) {
            SLog.fe(SLogType.REQUEST, LOG_NAME, "unknown uri scheme. %s", uriInfo.getUri());
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_NO_SUPPORT, sync);
            return false;
        }

        if (uriInfo.getScheme() != UriScheme.NET) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.fe(SLogType.REQUEST, LOG_NAME, "only support http ot https. %s", uriInfo.getUri());
            }
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

        // 根据URI和下载选项生成请求key
        key = SketchUtils.makeRequestKey(uriInfo.getUri(), uriInfo.getScheme(), downloadOptions);
    }

    private boolean checkDiskCache() {
        if (!downloadOptions.isCacheInDiskDisabled()) {
            DiskCache diskCache = sketch.getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(uriInfo.getDiskCacheKey());
            if (diskCacheEntry != null) {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fi(SLogType.REQUEST, LOG_NAME, "image download completed. %s", key);
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
        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        DownloadRequest request = requestFactory.newDownloadRequest(sketch, uriInfo, key, downloadOptions,
                downloadListener, downloadProgressListener);
        request.setSync(sync);
        request.submit();
        return request;
    }
}
