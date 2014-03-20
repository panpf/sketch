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

package me.xiaopan.android.imageloader.execute;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.task.display.AssetsBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.ContentBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.task.display.DrawableBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.FileBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.HttpBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;
import me.xiaopan.android.imageloader.task.download.DownloadTask;
import me.xiaopan.android.imageloader.task.load.LoadRequest;
import me.xiaopan.android.imageloader.util.Scheme;
import android.util.Log;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private static final String NAME= BaseTaskExecutor.class.getSimpleName();
	private Executor taskDispatchExecutor;	//任务调度执行器
	private Executor netTaskExecutor;	//网络任务执行器
	private Executor localTaskExecutor;	//本地任务执行器
	
	public BaseTaskExecutor(Executor taskDispatchExecutor, Executor netTaskExecutor, Executor localTaskExecutor){
		this.taskDispatchExecutor = taskDispatchExecutor;
		this.netTaskExecutor = netTaskExecutor;
		this.localTaskExecutor = localTaskExecutor;
	}
	
	public BaseTaskExecutor(Executor netTaskExecutor){
		this(
			new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy()),
			netTaskExecutor, 
			new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy())
		);
	}
	
	public BaseTaskExecutor(){
		this(new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy()));
	}
	
	@Override
	public void execute(final TaskRequest taskRequest) {
		taskDispatchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(taskRequest instanceof DownloadRequest){
					executeDownloadRequest((DownloadRequest) taskRequest);
				}else if(taskRequest instanceof LoadRequest){
					executeLoadRequest((LoadRequest) taskRequest);
				}else if(taskRequest instanceof DisplayRequest){
					executeDisplayRequest((DisplayRequest) taskRequest);
				}
			}
		});
	}
	
	/**
	 * 执行下载请求
	 * @param downloadRequest
	 */
	private void executeDownloadRequest(DownloadRequest downloadRequest){
		File cacheFile = downloadRequest.getConfiguration().getBitmapCacher().getCacheFile(downloadRequest);
		downloadRequest.setCacheFile(cacheFile);
		if(cacheFile != null && cacheFile.exists()){
			localTaskExecutor.execute(new DownloadTask((DownloadRequest) downloadRequest));
			if(downloadRequest.getConfiguration().isDebugMode()){
				Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("DOWNLOAD - 本地").append("；").append(downloadRequest.getName()).toString());
			}
		}else{
			netTaskExecutor.execute(new DownloadTask((DownloadRequest) downloadRequest));
			if(downloadRequest.getConfiguration().isDebugMode()){
				Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("DOWNLOAD - 网络").append("；").append(downloadRequest.getName()).toString());
			}
		}
	}
	
	/**
	 * 执行加载请求
	 * @param loadRequest
	 */
	private void executeLoadRequest(LoadRequest loadRequest){
		
	}
	
	/**
	 * 执行显示请求
	 * @param displayRequest
	 */
	private void executeDisplayRequest(DisplayRequest displayRequest){
		Scheme scheme = Scheme.ofUri(displayRequest.getUri());
		switch(scheme){
			case HTTP :
			case HTTPS : 
				File cacheFile = displayRequest.getConfiguration().getBitmapCacher().getCacheFile(displayRequest);
				displayRequest.setCacheFile(cacheFile);
				if(cacheFile != null && cacheFile.exists()){
					localTaskExecutor.execute(new HttpBitmapDisplayTask((DisplayRequest) displayRequest));
					if(displayRequest.getConfiguration().isDebugMode()){
						Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("HTTP - 本地").append("；").append(displayRequest.getName()).toString());
					}
				}else{
					netTaskExecutor.execute(new HttpBitmapDisplayTask((DisplayRequest) displayRequest));
					if(displayRequest.getConfiguration().isDebugMode()){
						Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("HTTP - 网络").append("；").append(displayRequest.getName()).toString());
					}
				}
				break;
			case FILE : 
				localTaskExecutor.execute(new FileBitmapDisplayTask((DisplayRequest) displayRequest));
				if(displayRequest.getConfiguration().isDebugMode()){
					Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("FILE - 本地").append("；").append(displayRequest.getName()).toString());
				}
				break;
			case ASSETS : 
				localTaskExecutor.execute(new AssetsBitmapDisplayTask((DisplayRequest) displayRequest));
				if(displayRequest.getConfiguration().isDebugMode()){
					Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("ASSETS - 本地").append("；").append(displayRequest.getName()).toString());
				}
				break;
			case CONTENT : 
				localTaskExecutor.execute(new ContentBitmapDisplayTask((DisplayRequest) displayRequest));
				if(displayRequest.getConfiguration().isDebugMode()){
					Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("CONTENT - 本地").append("；").append(displayRequest.getName()).toString());
				}
				break;
			case DRAWABLE : 
				localTaskExecutor.execute(new DrawableBitmapDisplayTask((DisplayRequest) displayRequest));
				if(displayRequest.getConfiguration().isDebugMode()){
					Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("DRAWABLE - 本地").append("；").append(displayRequest.getName()).toString());
				}
				break;
			default:
				break;
		}
	}
}
