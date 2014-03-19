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

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.decode.ByteArrayInputStreamCreator;
import me.xiaopan.android.imageloader.decode.FileInputStreamCreator;
import me.xiaopan.android.imageloader.decode.InputStreamCreator;
import me.xiaopan.android.imageloader.task.BitmapLoadCallable;
import me.xiaopan.android.imageloader.task.BitmapLoadTask;
import me.xiaopan.android.imageloader.task.download.DownloadRequest.DownloadListener;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import android.util.Log;

public class HttpBitmapDisplayTask extends  BitmapLoadTask {
	private DisplayRequest displayRequest;
	
	public HttpBitmapDisplayTask(DisplayRequest displayRequest) {
		super(displayRequest, new HttpBitmapLoadCallable(displayRequest));
		this.displayRequest = displayRequest;
	}
	
	public boolean isFromNetworkLoad(){
		return !isAvailableOfFile(getCacheFile(), displayRequest);
	}

	public File getCacheFile() {
		return displayRequest.getDisplayOptions().isEnableDiskCache()?displayRequest.getConfiguration().getBitmapCacher().getDiskCacheFile(displayRequest.getConfiguration().getContext(), ImageLoaderUtils.encodeUrl(displayRequest.getImageUri())):null;
	}
	
	/**
	 * 判断给定文件是否可以使用
	 * @param file
	 * @param displayRequest
	 * @return
	 */
	public static boolean isAvailableOfFile(File file, DisplayRequest displayRequest){
		if(file ==null){
			if(displayRequest.getConfiguration().isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件为null").append("；").append(displayRequest.getName()).toString());
			}
			return false;
		}
		
		if(!file.exists()){
			if(displayRequest.getConfiguration().isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件不存在").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(displayRequest.getName()).toString());
			}
			return false;
		}
		
		if(file.length() <= 0){
			if(displayRequest.getConfiguration().isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件长度为0").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(displayRequest.getName()).toString());
			}
			return false;
		}
		
		if(displayRequest.getDisplayOptions().getDiskCachePeriodOfValidity() <= 0){
			if(displayRequest.getConfiguration().isDebugMode()){
				Log.d(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件永久有效").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(displayRequest.getName()).toString());
			}
			return true;
		}
		
		/* 判断是否过期 */
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MILLISECOND, -displayRequest.getDisplayOptions().getDiskCachePeriodOfValidity());
		if(calendar.getTimeInMillis() >= file.lastModified()){
			file.delete();
			if(displayRequest.getConfiguration().isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件过期已删除").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(displayRequest.getName()).toString());
			}
			return false;
		}
		
		if(displayRequest.getConfiguration().isDebugMode()){
			Log.d(ImageLoader.LOG_TAG, new StringBuffer("AvailableOfFile").append("：").append("文件未过期").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(displayRequest.getName()).toString());
		}
		return true;
	}
	
	private static class HttpBitmapLoadCallable extends BitmapLoadCallable {
		private File cacheFile = null;
		private InputStreamCreator inputStreamCreator = null;
		
		public HttpBitmapLoadCallable(DisplayRequest displayRequest) {
			super(displayRequest);
		}

		@Override
		public InputStreamCreator getInputStreamCreator() {
			if(inputStreamCreator == null){
				if(displayRequest.getDisplayOptions().isEnableDiskCache()){
					cacheFile = displayRequest.getConfiguration().getBitmapCacher().getDiskCacheFile(displayRequest.getConfiguration().getContext(), ImageLoaderUtils.encodeUrl(displayRequest.getImageUri()));
					if(HttpBitmapDisplayTask.isAvailableOfFile(cacheFile, displayRequest)){
						inputStreamCreator = new FileInputStreamCreator(cacheFile);
					}else{
						inputStreamCreator = getNetInputStreamCreator(displayRequest, cacheFile);
					}
				}else{
					inputStreamCreator = getNetInputStreamCreator(displayRequest, null);
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
	    private InputStreamCreator getNetInputStreamCreator(DisplayRequest displayRequest, File cacheFile){
	    	final NetInputStreamCreatorHolder holder = new NetInputStreamCreatorHolder();
	    	displayRequest.getConfiguration().getImageDownloader().execute(displayRequest, cacheFile, new DownloadListener() {
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
