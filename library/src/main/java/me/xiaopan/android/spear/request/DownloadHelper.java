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

package me.xiaopan.android.spear.request;

import android.util.Log;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.Scheme;

/**
 * DownloadHelper
 */
public class DownloadHelper {
    private static final String LOG_TAG = DownloadRequest.class.getSimpleName();

    private Spear spear;
    private String uri;

    private long diskCacheTimeout;
    private boolean enableDiskCache = true;

    private DownloadListener downloadListener;
    private ProgressListener progressListener;

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
     * 设置监听器
     * @return Helper
     */
    public DownloadHelper listener(DownloadListener downloadListener){
        this.downloadListener = downloadListener;
        return this;
    }

    /**
     * 关闭硬盘缓存
     * @return Helper
     */
    public DownloadHelper disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    /**
     * 设置磁盘缓存超时时间
     * @param diskCacheTimeout 磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
     * @return Helper
     */
    public DownloadHelper diskCacheTimeout(long diskCacheTimeout) {
        this.diskCacheTimeout = diskCacheTimeout;
        return this;
    }

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return Helper
     */
    public DownloadHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置下载参数
     * @param options 下载参数
     * @return Helper
     */
    public DownloadHelper options(DownloadOptions options){
        if(options == null){
            return null;
        }

        this.enableDiskCache = options.isEnableDiskCache();
        this.diskCacheTimeout = options.getDiskCacheTimeout();

        return this;
    }

    /**
     * 设置下载参数，你只需要提前将DownloadOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return Helper
     */
    public DownloadHelper options(Enum<?> optionsName){
        return options((DownloadOptions) Spear.getOptions(optionsName));
    }

    /**
     * 执行请求
     * @return RequestFuture 你可以通过RequestFuture来查看请求的状态或者取消这个请求
     */
    public RequestFuture fire(){
        // 执行请求
        if(downloadListener != null){
            downloadListener.onStarted();
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "uri不能为null或空");
            }
            if(downloadListener != null){
                downloadListener.onFailed(FailureCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        Scheme scheme = Scheme.valueOfUri(uri);
        if(!(scheme == Scheme.HTTP || scheme == Scheme.HTTPS)){
            if(spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "download()方法只能处理http或https协议" + " URI" + "=" + uri);
            }
            if(downloadListener != null){
                downloadListener.onFailed(FailureCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        DownloadRequest request = new DownloadRequest();

        request.uri = uri;
        request.name = uri;
        request.spear = spear;
        request.scheme = scheme;
        request.enableDiskCache = enableDiskCache;
        request.diskCacheTimeout = diskCacheTimeout;

        request.downloadListener = downloadListener;
        request.downloadProgressListener = progressListener;

        spear.getRequestExecutor().execute(request);
        return new RequestFuture(request);
    }
}
