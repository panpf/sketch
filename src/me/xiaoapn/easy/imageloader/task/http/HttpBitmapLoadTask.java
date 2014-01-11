/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaoapn.easy.imageloader.task.http;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.task.BitmapLoadTask;
import me.xiaoapn.easy.imageloader.task.Options;
import me.xiaoapn.easy.imageloader.task.Request;
import me.xiaoapn.easy.imageloader.util.Utils;
import android.util.Log;

public class HttpBitmapLoadTask extends  BitmapLoadTask {
	private Request request;
	private Configuration configuration;
	
	public HttpBitmapLoadTask(Request request, ReentrantLock reentrantLock, Configuration configuration) {
		super(request, configuration, new HttpBitmapLoadCallable(request, reentrantLock, configuration));
		this.request = request;
		this.configuration = configuration;
	}
	
	public boolean isFromNetworkLoad(){
		return !isAvailableOfFile(getCacheFile(), request.getOptions().getCacheConfig().getDiskCachePeriodOfValidity(), configuration, request.getName());
	}

	public File getCacheFile() {
		return request.getOptions().getCacheConfig().isCacheInDisk()?getCacheFile(configuration, request.getOptions(), Utils.encodeUrl(request.getImageUri())):null;
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
				Log.w(configuration.getLogTag(), new StringBuffer("AvailableOfFile").append("：").append("文件为null").append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(!file.exists()){
			if(configuration.isDebugMode()){
				Log.w(configuration.getLogTag(), new StringBuffer("AvailableOfFile").append("：").append("文件不存在").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(file.length() <= 0){
			if(configuration.isDebugMode()){
				Log.w(configuration.getLogTag(), new StringBuffer("AvailableOfFile").append("：").append("文件长度为0").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(periodOfValidity <= 0){
			if(configuration.isDebugMode()){
				Log.d(configuration.getLogTag(), new StringBuffer("AvailableOfFile").append("：").append("文件永久有效").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return true;
		}
		
		/* 判断是否过期 */
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MILLISECOND, -periodOfValidity);
		if(calendar.getTimeInMillis() >= file.lastModified()){
			file.delete();
			if(configuration.isDebugMode()){
				Log.w(configuration.getLogTag(), new StringBuffer("AvailableOfFile").append("：").append("文件过期已删除").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
			}
			return false;
		}
		
		if(configuration.isDebugMode()){
			Log.d(configuration.getLogTag(), new StringBuffer("AvailableOfFile").append("：").append("文件未过期").append("；").append("文件地址").append("=").append(file.getPath()).append("；").append(requestName).toString());
		}
		return true;
	}

	/**
	 * 获取缓存文件，将优先考虑options指定的缓存目录，然后考虑当前configuration指定的缓存目录，然后考虑通过context获取默认的应用缓存目录，再然后就要返回null了
	 * @param context
	 * @param options
	 * @param fileName
	 * @return
	 */
	public static File getCacheFile(Configuration configuration, Options options, String fileName){
		if(options != null && Utils.isNotEmpty(options.getCacheConfig().getDiskCacheDirectory())){
			return new File(options.getCacheConfig().getDiskCacheDirectory() + File.separator + fileName);
		}else{
			return new File(Utils.getDynamicCacheDir(configuration.getContext()).getPath() + File.separator + "image_loader" + File.separator + fileName);
		}
	}
}
