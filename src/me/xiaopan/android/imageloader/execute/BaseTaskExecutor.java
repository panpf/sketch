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

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.Task;
import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.task.display.BitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.task.display.DrawableBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.HttpBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;
import me.xiaopan.android.imageloader.task.download.DownloadTask;
import android.util.Log;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private static final String NAME= BitmapDisplayTask.class.getSimpleName();
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
	public void execute(final Task futureTask, final Configuration configuration) {
		taskDispatchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(futureTask instanceof DrawableBitmapDisplayTask || (futureTask instanceof HttpBitmapDisplayTask && ((HttpBitmapDisplayTask) futureTask).isFromNetworkLoad())){
					if(configuration.isDebugMode()){
						Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("放到网络线程池中加载").append("；").append(futureTask.getTaskRequest().getName()).toString());
					}
					netTaskExecutor.execute(futureTask);
				}else if(futureTask instanceof DownloadTask){
					
				}else if(configuration.isDebugMode()){
					Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("放到本地线程池中加载").append("；").append(futureTask.getTaskRequest().getName()).toString());
					localTaskExecutor.execute(futureTask);
				}
			}
		});
	}

	@Override
	public void execute(final TaskRequest taskRequest) {
		taskDispatchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(taskRequest instanceof DownloadRequest){
					netTaskExecutor.execute(new DownloadTask((DownloadRequest) taskRequest));
				}else if(taskRequest instanceof DisplayRequest){
//					switch(scheme){
//						case HTTP :
//						case HTTPS : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
//							bitmapLoadTask = new HttpBitmapDisplayTask(displayRequest);
//							break;
//						case FILE : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
//							bitmapLoadTask = new FileBitmapDisplayTask(displayRequest);
//							break;
//						case ASSETS : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
//							bitmapLoadTask = new AssetsBitmapDisplayTask(displayRequest);
//							break;
//						case CONTENT : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
//							bitmapLoadTask = new ContentBitmapDisplayTask(displayRequest);
//							break;
//						case DRAWABLE : 
//							displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
//							bitmapLoadTask = new DrawableBitmapDisplayTask(displayRequest);
//							break;
//						default:
//							break;
//					}
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
