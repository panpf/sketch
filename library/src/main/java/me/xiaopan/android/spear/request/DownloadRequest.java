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

import java.io.File;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.task.Task;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.Scheme;

/**
 * 下载请求
 */
public class DownloadRequest implements Request {
    private static final String LOG_TAG = DownloadRequest.class.getSimpleName();

    /* 任务基础属性 */
    private Task task;	// 执行当前请求的任务，由于一个请求可能辗转被好几个任务处理
    private Status status = Status.WAITING;  // 状态

    /* 必须属性 */
    private File cacheFile;	// 缓存文件
    protected Spear spear;
    protected String uri;	// 图片地址
    protected String name;	// 名称，用于在输出LOG的时候区分不同的请求
    protected Scheme scheme;	// Uri协议格式
	private DownloadListener downloadListener;  // 下载监听器
    private ProgressCallback downloadProgressCallback;  // 下载进度回调

    /* 可配置属性 */
    protected long diskCachePeriodOfValidity;	// 磁盘缓存有效期，单位毫秒，小于等于0表示永不过期
    protected boolean enableDiskCache;	// 是否开启磁盘缓存

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isFinished() {
        return status == Status.COMPLETED || status == Status.FAILED || status == Status.CANCELED;
    }

    @Override
    public boolean isCanceled() {
        return status == Status.CANCELED;
    }

    @Override
    public boolean cancel() {
        if(isFinished()){
            return false;
        }
        status = Status.CANCELED;
        if(task != null){
            task.cancel(true);
        }
        return true;
    }

    /**
     * 获取Spear
     * @return Spear
     */
    public Spear getSpear() {
        return spear;
    }

    /**
     * 获取请求名称
     */
	public String getName() {
		return name;
	}

	/**
	 * 获取Uri协议类型
	 */
	public Scheme getScheme() {
		return scheme;
	}

	/**
	 * 获取缓存文件
	 */
	public File getCacheFile() {
		return cacheFile;
	}
	
	/**
	 * 设置缓存文件
	 */
	public void setCacheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}

    /**
     * 获取下载监听器
     */
	public DownloadListener getDownloadListener() {
		return downloadListener;
	}

    /**
     * 设置下载监听器
     * @param downloadListener 下载监听器
     */
    public DownloadRequest setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    /**
     * 获取磁盘缓存有效期
     * @return 磁盘缓存有效期，单位毫秒，小于等于0表示永不过期
     */
    public long getDiskCachePeriodOfValidity() {
        return diskCachePeriodOfValidity;
    }

    /**
     * 是否开启磁盘缓存
     * @return 是否开启磁盘缓存
     */
    public boolean isEnableDiskCache() {
        return enableDiskCache;
    }

    /**
     * 获取下载进度回调
     * @return 下载进度回调
     */
    public ProgressCallback getDownloadProgressCallback() {
        return downloadProgressCallback;
    }

    public void setDownloadProgressCallback(ProgressCallback downloadProgressCallback) {
        this.downloadProgressCallback = downloadProgressCallback;
    }

    /**
     * 生成器，用来生成下载请求
     */
    public static class Builder{
        private Spear spear;
        private String uri;

        private long diskCachePeriodOfValidity;
        private boolean enableDiskCache = true;

        private DownloadListener downloadListener;
        private ProgressCallback progressCallback;

        /**
         * 创建下载请求生成器
         * @param spear Spear
         * @param uri 支持以下2种类型
         * <blockquote>“http://site.com/image.png“  // from Web
         * <br>“https://site.com/image.png“ // from Web
         * </blockquote>
         */
        public Builder(Spear spear, String uri) {
            this.spear = spear;
            this.uri = uri;
        }

        /**
         * 设置监听器
         * @return Builder
         */
        public Builder listener(DownloadListener downloadListener){
            this.downloadListener = downloadListener;
            return this;
        }

        /**
         * 关闭硬盘缓存
         * @return Builder
         */
        public Builder disableDiskCache() {
            this.enableDiskCache = false;
            return this;
        }

        /**
         * 设置硬盘缓存有效期
         * @param diskCachePeriodOfValidity 硬盘缓存有效期，单位毫秒，小于等于0表示永不过期
         * @return Builder
         */
        public Builder diskCachePeriodOfValidity(long diskCachePeriodOfValidity) {
            this.diskCachePeriodOfValidity = diskCachePeriodOfValidity;
            return this;
        }

        /**
         * 设置进度回调
         * @param progressCallback 进度回调
         * @return Builder
         */
        public Builder progressCallback(ProgressCallback progressCallback){
            this.progressCallback = progressCallback;
            return this;
        }

        /**
         * 设置下载参数
         * @param options 下载参数
         * @return Builder
         */
        public Builder options(DownloadOptions options){
            if(options == null){
                return null;
            }

            this.enableDiskCache = options.isEnableDiskCache();
            this.diskCachePeriodOfValidity = options.getDiskCachePeriodOfValidity();

            return this;
        }

        /**
         * 设置下载参数，你只需要提前将DownloadOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
         * @param optionsName 参数名称
         * @return Builder
         */
        public Builder options(Enum<?> optionsName){
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
            request.diskCachePeriodOfValidity = diskCachePeriodOfValidity;

            request.downloadListener = downloadListener;
            request.downloadProgressCallback = progressCallback;

            spear.getRequestExecutor().execute(request);
            return new RequestFuture(request);
        }
    }
}