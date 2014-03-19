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

package me.xiaopan.android.imageloader.task.display;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.decode.ByteArrayInputStreamCreator;
import me.xiaopan.android.imageloader.decode.FileInputStreamCreator;
import me.xiaopan.android.imageloader.decode.InputStreamCreator;
import me.xiaopan.android.imageloader.task.BitmapLoadCallable;
import me.xiaopan.android.imageloader.task.BitmapLoadTask;
import me.xiaopan.android.imageloader.task.download.DownloadListener;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import android.util.Log;

public class HttpBitmapDisplayTask extends  BitmapLoadTask {
	private DisplayRequest request;
	private Configuration configuration;
	
	public HttpBitmapDisplayTask(DisplayRequest displayRequest, ReentrantLock reentrantLock, Configuration configuration) {
		super(displayRequest, configuration, new HttpBitmapLoadCallable(displayRequest, reentrantLock, configuration));
		this.request = displayRequest;
		this.configuration = configuration;
	}
	
	public boolean isFromNetworkLoad(){
		return !isAvailableOfFile(getCacheFile(), request.getDisplayOptions().getDiskCachePeriodOfValidity(), configuration, request.getName());
	}

	public File getCacheFile() {
		return request.getDisplayOptions().isEnableDiskCache()?configuration.getBitmapCacher().getDiskCacheFile(configuration.getContext(), ImageLoaderUtils.encodeUrl(request.getImageUri())):null;
	}
	
	/**
	 * 判断给定文件是否可以使用
	 * @param file
	 * @param periodOfValidity
	 * @param configuration
	 * @param requestName
	 * @return
	 */
	public static boolean isAvailableOfFile(File file, int periodOfValidity, Configuration configuration, String requestName){
		if(file ==null){
			if(configuration.isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件为null").append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(!file.exists()){
			if(configuration.isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件不存在").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(file.length() <= 0){
			if(configuration.isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件长度为0").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(periodOfValidity <= 0){
			if(configuration.isDebugMode()){
				Log.d(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件永久有效").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return true;
		}
		
		/* 判断是否过期 */
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MILLISECOND, -periodOfValidity);
		if(calendar.getTimeInMillis() >= file.lastModified()){
			file.delete();
			if(configuration.isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件过期已删除").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(configuration.isDebugMode()){
			Log.d(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件未过期").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
		}
		return true;
	}
	
	private static class HttpBitmapLoadCallable extends BitmapLoadCallable {
		private File cacheFile = null;
		private InputStreamCreator inputStreamCreator = null;
		
		public HttpBitmapLoadCallable(DisplayRequest displayRequest, ReentrantLock reentrantLock, Configuration configuration) {
			super(displayRequest, reentrantLock, configuration);
		}

		@Override
		public InputStreamCreator getInputStreamCreator() {
			if(inputStreamCreator == null){
				if(displayRequest.getDisplayOptions().isEnableDiskCache()){
					cacheFile = configuration.getBitmapCacher().getDiskCacheFile(configuration.getContext(), ImageLoaderUtils.encodeUrl(displayRequest.getImageUri()));
					if(HttpBitmapDisplayTask.isAvailableOfFile(cacheFile, displayRequest.getDisplayOptions().getDiskCachePeriodOfValidity(), configuration, displayRequest.getName())){
						inputStreamCreator = new FileInputStreamCreator(cacheFile);
					}else{
						inputStreamCreator = getNetInputStreamCreator(configuration, displayRequest, cacheFile);
					}
				}else{
					inputStreamCreator = getNetInputStreamCreator(configuration, displayRequest, null);
				}
			}
			return inputStreamCreator;
		}

		@Override
		public void onFailed() {
			if(inputStreamCreator instanceof FileInputStreamCreator && cacheFile != null && cacheFile.exists()){
				cacheFile.delete();
			}
		}
		
		/**
	     * 获取网络输入流监听器
	     * @param requestName
	     * @param imageUrl
	     * @param cacheFile
	     * @param maxRetryCount
	     * @param httpClient
	     * @return
	     */
	    private InputStreamCreator getNetInputStreamCreator(Configuration configuration, DisplayRequest displayRequest, File cacheFile){
	    	final NetInputStreamCreatorHolder holder = new NetInputStreamCreatorHolder();
	    	configuration.getImageDownloader().execute(displayRequest, cacheFile, configuration, new DownloadListener() {
				@Override
				public void onFailed() {}
				
				@Override
				public void onComplete(final byte[] data) {
					holder.inputStreamCreator = new ByteArrayInputStreamCreator(data);
				}
				
				@Override
				public void onComplete(final File cacheFile) {
					holder.inputStreamCreator = new FileInputStreamCreator(cacheFile);
				}
			});
	    	return holder.inputStreamCreator;
	    }
		
		private class NetInputStreamCreatorHolder{
			InputStreamCreator inputStreamCreator;
		}
	}
}
