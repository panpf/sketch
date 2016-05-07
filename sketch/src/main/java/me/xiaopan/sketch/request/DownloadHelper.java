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
    protected static final String NAME = "DownloadHelper";

    protected Sketch sketch;

    protected RequestAttrs attrs = new RequestAttrs();
    protected DownloadOptions options = new DownloadOptions();

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
        this.attrs.reset(uri);
    }

    /**
     * 设置名称，用于在log总区分请求
     */
    public DownloadHelper name(String name) {
        this.attrs.setName(name);
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
     * 设置请求Level
     */
    @SuppressWarnings("unused")
    public DownloadHelper requestLevel(RequestLevel requestLevel) {
        if (requestLevel != null) {
            options.setRequestLevel(requestLevel);
            options.setRequestLevelFrom(null);
        }
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
            options.setCacheInDisk(false);
        }

        // 暂停下载对于下载请求并不起作用，就相当于暂停加载对加载请求并不起作用一样，因此这里不予处理

        // 没有设置名称的话就用uri作为名称，名称主要用来在log中区分请求的
        if (attrs.getName() == null) {
            attrs.setName(attrs.getUri());
        }
    }

    /**
     * 提交请求
     *
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public DownloadRequest commit() {
        if (downloadListener != null) {
            downloadListener.onStarted();
        }

        preProcess();

        if(!checkUri()){
            return null;
        }

        if(!checkUriScheme()){
            return null;
        }

        return submitRequest();
    }

    private boolean checkUri(){
        if (attrs.getUri() == null || "".equals(attrs.getUri().trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if (downloadListener != null) {
                downloadListener.onFailed(FailedCause.URI_NULL_OR_EMPTY);
            }
            return false;
        }

        return true;
    }

    private boolean checkUriScheme(){
        if (attrs.getUriScheme() == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme", " - ", attrs.getName()));
            if (downloadListener != null) {
                downloadListener.onFailed(FailedCause.URI_NO_SUPPORT);
            }
            return false;
        }

        if (attrs.getUriScheme() != UriScheme.NET) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "only support http ot https", " - ", attrs.getName()));
            }
            if (downloadListener != null) {
                downloadListener.onFailed(FailedCause.URI_NO_SUPPORT);
            }
            return false;
        }

        return true;
    }

    private DownloadRequest submitRequest(){
        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        DownloadRequest request = requestFactory.newDownloadRequest(sketch, attrs, options, downloadListener, progressListener);
        request.submit();
        return request;
    }
}
