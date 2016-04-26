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

import android.util.Log;

import me.xiaopan.sketch.util.SketchUtils;

public class DownloadHelper {
    private static final String NAME = "DownloadHelper";

    protected Sketch sketch;
    protected String uri;
    protected String name;

    protected DownloadOptions options;

    protected DownloadProgressListener progressListener;
    protected DownloadListener downloadListener;

    /**
     * 图片Uri，支持以下几种
     * <blockQuote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockQuote>
     */
    public DownloadHelper(Sketch sketch, String uri) {
        this.sketch = sketch;
        this.uri = uri;
        this.options = new DownloadOptions();
    }

    /**
     * 设置名称，用于在log总区分请求
     */
    public DownloadHelper name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 关闭硬盘缓存
     */
    @SuppressWarnings("unused")
    public DownloadHelper disableDiskCache() {
        options.setCacheInDisk(false);
        return this;
    }

    /**
     * 批量设置下载参数，这会是一个合并的过程，并不会完全覆盖
     */
    public DownloadHelper options(DownloadOptions newOptions) {
        options.apply(newOptions);
        return this;
    }

    /**
     * 批量设置下载参数，你只需要提前将DownloadOptions通过Sketch.putDownloadOptions()方法存起来，然后在这里指定其名称即可，另外这会是一个合并的过程，并不会完全覆盖
     */
    @SuppressWarnings("unused")
    public DownloadHelper options(Enum<?> optionsName) {
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
            options.setCacheInDisk(false);
        }

        // 暂停下载对于下载请求并不起作用，就相当于暂停加载对加载请求并不起作用一样，因此这里不予处理

        // 没有设置名称的话就用uri作为名称，名称主要用来在log中区分请求的
        if (name == null) {
            name = uri;
        }
    }

    /**
     * 提交请求
     *
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public DownloadRequest commit() {
        preProcess();

        if (downloadListener != null) {
            downloadListener.onStarted();
        }

        // 验证uri参数
        if (uri == null || "".equals(uri.trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if (downloadListener != null) {
                downloadListener.onFailed(FailedCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        if (name == null) {
            name = uri;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if (uriScheme == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme", " - ", name));
            if (downloadListener != null) {
                downloadListener.onFailed(FailedCause.URI_NO_SUPPORT);
            }
            return null;
        }

        if (!(uriScheme == UriScheme.HTTP || uriScheme == UriScheme.HTTPS)) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "only support http ot https", " - ", name));
            }
            if (downloadListener != null) {
                downloadListener.onFailed(FailedCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        RequestAttrs attrs = new RequestAttrs(sketch, uri, uriScheme, name);
        DownloadRequest request = sketch.getConfiguration().getRequestFactory().newDownloadRequest(attrs, options, downloadListener, progressListener);

        request.submit();

        return request;
    }
}
