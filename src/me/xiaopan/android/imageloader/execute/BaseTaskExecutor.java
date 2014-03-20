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
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
	private Map<String, ReentrantLock> uriLocks;	//uri锁池
	
	public BaseTaskExecutor(Executor netTaskExecutor, Executor localTaskExecutor){
		this.uriLocks = new WeakHashMap<String, ReentrantLock>();
		this.taskDispatchExecutor = Executors.newCachedThreadPool();
		this.netTaskExecutor = netTaskExecutor;
		this.localTaskExecutor = localTaskExecutor;
	}
	
	public BaseTaskExecutor(Executor netTaskExecutor){
		this(netTaskExecutor, new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy()));
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
					netTaskExecutor.execute(new DownloadTask((DownloadRequest) taskRequest));
					if(taskRequest.getConfiguration().isDebugMode()){
						Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("DOWNLOAD - 网络").append("；").append(taskRequest.getName()).toString());
					}
				}else if(taskRequest instanceof DisplayRequest){
					Scheme scheme = Scheme.ofUri(taskRequest.getUri());
					switch(scheme){
						case HTTP :
						case HTTPS : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
							File cacheFile = taskRequest.getConfiguration().getBitmapCacher().getCacheFile(taskRequest);
							if(cacheFile != null && cacheFile.exists()){
								localTaskExecutor.execute(new HttpBitmapDisplayTask((DisplayRequest) taskRequest));
								if(taskRequest.getConfiguration().isDebugMode()){
									Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("HTTP - 本地").append("；").append(taskRequest.getName()).toString());
								}
							}else{
								netTaskExecutor.execute(new HttpBitmapDisplayTask((DisplayRequest) taskRequest));
								if(taskRequest.getConfiguration().isDebugMode()){
									Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("HTTP - 网络").append("；").append(taskRequest.getName()).toString());
								}
							}
							break;
						case FILE : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
							localTaskExecutor.execute(new FileBitmapDisplayTask((DisplayRequest) taskRequest));
							if(taskRequest.getConfiguration().isDebugMode()){
								Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("FILE - 本地").append("；").append(taskRequest.getName()).toString());
							}
							break;
						case ASSETS : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
							localTaskExecutor.execute(new AssetsBitmapDisplayTask((DisplayRequest) taskRequest));
							if(taskRequest.getConfiguration().isDebugMode()){
								Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("ASSETS - 本地").append("；").append(taskRequest.getName()).toString());
							}
							break;
						case CONTENT : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
							localTaskExecutor.execute(new ContentBitmapDisplayTask((DisplayRequest) taskRequest));
							if(taskRequest.getConfiguration().isDebugMode()){
								Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("CONTENT - 本地").append("；").append(taskRequest.getName()).toString());
							}
							break;
						case DRAWABLE : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
							localTaskExecutor.execute(new DrawableBitmapDisplayTask((DisplayRequest) taskRequest));
							if(taskRequest.getConfiguration().isDebugMode()){
								Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("DRAWABLE - 本地").append("；").append(taskRequest.getName()).toString());
							}
							break;
						default:
							break;
					}
				}
			}
		});
	}

	@Override
	public ReentrantLock getLockByRequestId(String requestId) {
		ReentrantLock lock = uriLocks.get(requestId);
		if (lock == null) {
			lock = new ReentrantLock();
			uriLocks.put(requestId, lock);
		}
		return lock;
	}
}
