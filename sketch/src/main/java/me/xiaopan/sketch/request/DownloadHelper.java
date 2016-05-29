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

import android.util.Log;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feture.RequestFactory;
import me.xiaopan.sketch.util.SketchUtils;

public class DownloadHelper {
    protected String logName = "DownloadHelper";

    protected Sketch sketch;

    protected RequestAttrs requestAttrs = new RequestAttrs();
    protected DownloadOptions downloadOptions = new DownloadOptions();
    protected DownloadListener downloadListener;
    protected DownloadProgressListener progressListener;

    /**
     * 图片Uri，支持以下几种
     * <blockQuote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockQuote>
     */
    public DownloadHelper(Sketch sketch, String uri) {
        this.sketch = sketch;
        this.requestAttrs.reset(uri);
    }

    /**
     * 设置名称，用于在log总区分请求
     */
    public DownloadHelper name(String name) {
        this.requestAttrs.setName(name);
        return this;
    }

    /**
     * 关闭硬盘缓存
     */
    @SuppressWarnings("unused")
    public DownloadHelper disableDiskCache() {
        downloadOptions.setCacheInDisk(false);
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
     * 批量设置下载参数，这会是一个合并的过程，并不会完全覆盖
     */
    public DownloadHelper options(DownloadOptions newOptions) {
        downloadOptions.apply(newOptions);
        return this;
    }

    /**
     * 批量设置下载参数，你只需要提前将DownloadOptions通过Sketch.putDownloadOptions()方法存起来，
     * 然后在这里指定其名称即可，另外这会是一个合并的过程，并不会完全覆盖
     */
    @SuppressWarnings("unused")
    public DownloadHelper optionsByName(Enum<?> optionsName) {
        return options(Sketch.getDownloadOptions(optionsName));
    }

    /**
     * 设置进度监听器
     */
    @SuppressWarnings("unused")
    public DownloadHelper progressListener(DownloadProgressListener downloadProgressListener) {
        this.progressListener = downloadProgressListener;
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
     * 对属性进行预处理
     */
    protected void preProcess() {
        Configuration configuration = sketch.getConfiguration();

        if (!configuration.isCacheInDisk()) {
            downloadOptions.setCacheInDisk(false);
        }

        // 暂停下载对于下载请求并不起作用，就相当于暂停加载对加载请求并不起作用一样，因此这里不予处理

        // 根据URI和下载选项生成请求ID
        if (requestAttrs.getId() == null) {
            requestAttrs.setId(requestAttrs.generateId(downloadOptions));
        }

        // 没有设置名称的话就用uri作为名称，名称主要用来在log中区分请求的
        if (requestAttrs.getName() == null) {
            requestAttrs.setName(requestAttrs.getId());
        }
    }

    /**
     * 提交请求
     *
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public DownloadRequest commit() {
        CallbackHandler.postCallbackStarted(downloadListener);

        preProcess();

        if (!checkUri()) {
            return null;
        }

        if (!checkUriScheme()) {
            return null;
        }

        return submitRequest();
    }

    private boolean checkUri() {
        if (requestAttrs.getUri() == null || "".equals(requestAttrs.getUri().trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "uri is null or empty"));
            }
            CallbackHandler.postCallbackFailed(downloadListener, FailedCause.URI_NULL_OR_EMPTY);
            return false;
        }

        return true;
    }

    private boolean checkUriScheme() {
        if (requestAttrs.getUriScheme() == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "unknown uri scheme", " - ", requestAttrs.getName()));
            CallbackHandler.postCallbackFailed(downloadListener, FailedCause.URI_NO_SUPPORT);
            return false;
        }

        if (requestAttrs.getUriScheme() != UriScheme.NET) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "only support http ot https", " - ", requestAttrs.getName()));
            }
            CallbackHandler.postCallbackFailed(downloadListener, FailedCause.URI_NO_SUPPORT);
            return false;
        }

        return true;
    }

    private DownloadRequest submitRequest() {
        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        DownloadRequest request = requestFactory.newDownloadRequest(sketch, requestAttrs, downloadOptions, downloadListener, progressListener);
        request.submit();
        return request;
    }
}
