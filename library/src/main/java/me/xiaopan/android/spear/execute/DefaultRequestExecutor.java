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

package me.xiaopan.android.spear.execute;

import android.util.Log;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.decode.AssetsDecodeListener;
import me.xiaopan.android.spear.decode.CacheFileDecodeListener;
import me.xiaopan.android.spear.decode.ContentDecodeListener;
import me.xiaopan.android.spear.decode.DrawableDecodeListener;
import me.xiaopan.android.spear.decode.FileDecodeListener;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.DownloadRequest;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.request.Request;
import me.xiaopan.android.spear.task.DisplayJoinLoadListener;
import me.xiaopan.android.spear.task.DisplayJoinLoadProgressListener;
import me.xiaopan.android.spear.task.DownloadTask;
import me.xiaopan.android.spear.task.LoadJoinDownloadListener;
import me.xiaopan.android.spear.task.LoadJoinDownloadProgressListener;
import me.xiaopan.android.spear.task.LoadTask;
import me.xiaopan.android.spear.util.Scheme;

/**
 * 默认的请求执行器
 */
public class DefaultRequestExecutor implements RequestExecutor {
	private static final String NAME= DefaultRequestExecutor.class.getSimpleName();
	private Executor taskDispatchExecutor;	//任务调度执行器
	private Executor netTaskExecutor;	//网络任务执行器
	private Executor localTaskExecutor;	//本地任务执行器
	
	private DefaultRequestExecutor(Builder builder){
		this.taskDispatchExecutor = builder.taskDispatchExecutor;
        this.netTaskExecutor = builder.netTaskExecutor;
        this.localTaskExecutor = builder.localTaskExecutor;
	}
	
	@Override
	public void execute(final Request request) {
		taskDispatchExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (request instanceof DisplayRequest) {
                    executeDisplayRequest((DisplayRequest) request);
                } else if (request instanceof LoadRequest) {
                    executeLoadRequest((LoadRequest) request);
                } else if (request instanceof DownloadRequest) {
                    executeDownloadRequest((DownloadRequest) request);
                }
            }
        });
	}
	
	/**
	 * 执行下载请求
	 * @param downloadRequest 下载请求
	 */
	private void executeDownloadRequest(DownloadRequest downloadRequest){
		// 根据需求生成缓存文件
        if(downloadRequest.isEnableDiskCache()){
            downloadRequest.setCacheFile(downloadRequest.getSpear().getDiskCache().createCacheFile(downloadRequest));
        }

        // 如果需要缓存并且缓存文件已存在就考虑从本地读取
        File cacheFile = downloadRequest.getCacheFile();
		if(cacheFile != null && cacheFile.exists()){
            // 如果缓存文件虽然已存在，但是正在下载中，就从网络下载
            if(downloadRequest.getSpear().getImageDownloader().isDownloadingByCacheFilePath(cacheFile.getPath())){
                netTaskExecutor.execute(new DownloadTask(downloadRequest));
                if(downloadRequest.getSpear().isDebugMode()){
                    Log.d(Spear.LOG_TAG, NAME + "：" + "DOWNLOAD - 网络 - 正在下载" + "；" + downloadRequest.getName());
                }
                return;
            }

            // 如果缓存文件可用就从本地读取
            localTaskExecutor.execute(new DownloadTask(downloadRequest));
			if(downloadRequest.getSpear().isDebugMode()){
				Log.d(Spear.LOG_TAG, NAME + "：" + "DOWNLOAD - 本地" + "；" + downloadRequest.getName());
			}
            return;
		}

        // 不需要缓存或缓存文件不存在就从网络下载
        netTaskExecutor.execute(new DownloadTask(downloadRequest));
        if(downloadRequest.getSpear().isDebugMode()){
            Log.d(Spear.LOG_TAG, NAME + "：" + "DOWNLOAD - 网络" + "；" + downloadRequest.getName());
        }
	}
	
	/**
	 * 执行加载请求
	 * @param loadRequest 记载请求
	 */
	private void executeLoadRequest(LoadRequest loadRequest){
		switch(loadRequest.getScheme()){
			case HTTP :
			case HTTPS :
                // 根据需求生成缓存文件
                if(loadRequest.isEnableDiskCache()){
                    loadRequest.setCacheFile(loadRequest.getSpear().getDiskCache().createCacheFile(loadRequest));
                }

                // 如果需要缓存并且缓存文件已存在就考虑从本地读取
				File cacheFile = loadRequest.getCacheFile();
                if(cacheFile != null && cacheFile.exists()){
                	// 如果缓存文件虽然已存在，但是正在下载中，就从网络下载
                    if(loadRequest.getSpear().getImageDownloader().isDownloadingByCacheFilePath(cacheFile.getPath())){
                        loadRequest.setDownloadListener(new LoadJoinDownloadListener(localTaskExecutor, loadRequest));
                        if(loadRequest.getLoadProgressListener() != null){
                            loadRequest.setDownloadProgressListener(new LoadJoinDownloadProgressListener(loadRequest.getLoadProgressListener()));
                        }
                		netTaskExecutor.execute(new DownloadTask(loadRequest));
                        if(loadRequest.getSpear().isDebugMode()){
                            Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - HTTP - 网络 - 正在下载" + "；" + loadRequest.getName());
                        }
                        break;
                	}

                    // 如果缓存文件可用就从本地读取
                    localTaskExecutor.execute(new LoadTask(loadRequest, new CacheFileDecodeListener(cacheFile, loadRequest), LoadListener.From.LOCAL));
                    if(loadRequest.getSpear().isDebugMode()){
                        Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - HTTP - 本地" + "；" + loadRequest.getName());
                    }
                    break;
                }

                // 如果不需要缓存或缓存文件不存在就从网络下载
                loadRequest.setDownloadListener(new LoadJoinDownloadListener(localTaskExecutor, loadRequest));
                if(loadRequest.getLoadProgressListener() != null){
                    loadRequest.setDownloadProgressListener(new LoadJoinDownloadProgressListener(loadRequest.getLoadProgressListener()));
                }
                netTaskExecutor.execute(new DownloadTask(loadRequest));
                if(loadRequest.getSpear().isDebugMode()){
                    Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - HTTP - 网络" + "；" + loadRequest.getName());
                }
				break;
			case FILE :
                localTaskExecutor.execute(new LoadTask(loadRequest, new FileDecodeListener(new File(Scheme.FILE.crop(loadRequest.getUri())), loadRequest), LoadListener.From.LOCAL));
                if(loadRequest.getSpear().isDebugMode()){
                    Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - FILE" + "；" + loadRequest.getName());
                }
				break;
			case ASSETS :
                localTaskExecutor.execute(new LoadTask(loadRequest, new AssetsDecodeListener(Scheme.ASSETS.crop(loadRequest.getUri()), loadRequest), LoadListener.From.LOCAL));
                if(loadRequest.getSpear().isDebugMode()){
                    Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - ASSETS" + "；" + loadRequest.getName());
                }
				break;
			case CONTENT :
                localTaskExecutor.execute(new LoadTask(loadRequest, new ContentDecodeListener(loadRequest.getUri(), loadRequest), LoadListener.From.LOCAL));
                if(loadRequest.getSpear().isDebugMode()){
                    Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - CONTENT" + "；" + loadRequest.getName());
                }
				break;
			case DRAWABLE :
                localTaskExecutor.execute(new LoadTask(loadRequest, new DrawableDecodeListener(Scheme.DRAWABLE.crop(loadRequest.getUri()), loadRequest), LoadListener.From.LOCAL));
                if(loadRequest.getSpear().isDebugMode()){
                    Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - DRAWABLE" + "；" + loadRequest.getName());
                }
                break;
			default:
                if(loadRequest.getSpear().isDebugMode()){
                    Log.e(Spear.LOG_TAG, NAME + "：" + "LOAD - 未知的协议格式" + "：" + loadRequest.getUri());
                }
				break;
		}
	}

    /**
     * 执行显示请求
     * @param displayRequest 显示请求
     */
    private void executeDisplayRequest(DisplayRequest displayRequest){
        displayRequest.setLoadListener(new DisplayJoinLoadListener(displayRequest));
        if(displayRequest.getDisplayProgressListener() != null){
            displayRequest.setLoadProgressListener(new DisplayJoinLoadProgressListener(displayRequest));
        }
        executeLoadRequest(displayRequest);
    }

    public static class Builder{
        private Executor taskDispatchExecutor;	//任务调度执行器
        private Executor netTaskExecutor;	//网络任务执行器
        private Executor localTaskExecutor;	//本地任务执行器

        public Builder taskDispatchExecutor(BlockingQueue<Runnable> workQueue){
            if(workQueue != null){
                workQueue = new LinkedBlockingQueue<Runnable>(20);
            }
            this.taskDispatchExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
            return this;
        }

        public Builder netTaskExecutor(int maxPoolSize, BlockingQueue<Runnable> workQueue){
            if(maxPoolSize <= 0){
                maxPoolSize = 5;
            }
            if(workQueue == null){
                workQueue = new LinkedBlockingQueue<Runnable>(20);
            }
            this.netTaskExecutor = new ThreadPoolExecutor(maxPoolSize, maxPoolSize, 1, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
            return this;
        }

        public Builder localTaskExecutor(BlockingQueue<Runnable> workQueue){
            if(workQueue == null){
                workQueue = new LinkedBlockingQueue<Runnable>(20);
            }
            this.localTaskExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
            return this;
        }

        public DefaultRequestExecutor build(){
            if(taskDispatchExecutor == null){
                taskDispatchExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            if(netTaskExecutor == null){
                netTaskExecutor = new ThreadPoolExecutor(5, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            if(localTaskExecutor == null){
                localTaskExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            return new DefaultRequestExecutor(this);
        }
    }
}
