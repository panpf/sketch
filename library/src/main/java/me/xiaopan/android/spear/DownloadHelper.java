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

package me.xiaopan.android.spear;

import android.util.Log;

import me.xiaopan.android.spear.request.DownloadListener;
import me.xiaopan.android.spear.request.DownloadRequest;
import me.xiaopan.android.spear.request.FailCause;
import me.xiaopan.android.spear.request.ProgressListener;
import me.xiaopan.android.spear.request.Request;
import me.xiaopan.android.spear.request.UriScheme;

/**
 * DownloadHelper
 */
public class DownloadHelper {
    private static final String NAME = "DownloadHelper";

    protected Spear spear;
    protected String uri;
    protected String name;
    protected boolean enableDiskCache = DownloadRequest.DEFAULT_ENABLE_DISK_CACHE;
    protected ProgressListener progressListener;

    protected DownloadListener downloadListener;

    /**
     * 创建下载请求生成器
     * @param spear Spear
     * @param uri 支持以下2种类型
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockquote>
     */
    public DownloadHelper(Spear spear, String uri) {
        this.spear = spear;
        this.uri = uri;
    }

    /**
     * 设置名称，用于在log总区分请求
     * @param name 名称
     * @return DownloadHelper
     */
    public DownloadHelper name(String name){
        this.name = name;
        return this;
    }

    /**
     * 设置监听器
     * @return DownloadHelper
     */
    public DownloadHelper listener(DownloadListener downloadListener){
        this.downloadListener = downloadListener;
        return this;
    }

    /**
     * 关闭硬盘缓存
     * @return DownloadHelper
     */
    public DownloadHelper disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return DownloadHelper
     */
    public DownloadHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置下载参数
     * @param options 下载参数
     * @return DownloadHelper
     */
    public DownloadHelper options(DownloadOptions options){
        if(options == null){
            return this;
        }

        if(options.isEnableDiskCache() != DownloadRequest.DEFAULT_ENABLE_DISK_CACHE){
            this.enableDiskCache = options.isEnableDiskCache();
        }

        return this;
    }

    /**
     * 设置下载参数，你只需要提前将DownloadOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return DownloadHelper
     */
    public DownloadHelper options(Enum<?> optionsName){
        return options((DownloadOptions) Spear.getOptions(optionsName));
    }

    /**
     * 执行请求
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public Request fire(){
        // 执行请求
        if(downloadListener != null){
            downloadListener.onStarted();
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "uri不能为null或空");
            }
            if(downloadListener != null){
                downloadListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if(!(uriScheme == UriScheme.HTTP || uriScheme == UriScheme.HTTPS)){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "download()方法只能处理http或https协议" + " URI" + "=" + uri);
            }
            if(downloadListener != null){
                downloadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        DownloadRequest request = new DownloadRequest(spear, uri, uriScheme);

        request.setName(name != null ? name : uri);
        request.setEnableDiskCache(enableDiskCache);

        request.setDownloadListener(downloadListener);
        request.setProgressListener(progressListener);

        request.runDispatch();

        return request;
    }
}
