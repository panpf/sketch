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

package me.xiaoapn.easy.imageloader;

import java.util.HashMap;
import java.util.Map;

import me.xiaoapn.easy.imageloader.cache.BitmapCacher;
import me.xiaoapn.easy.imageloader.cache.BitmapLruCacher;
import me.xiaoapn.easy.imageloader.cache.CacheConfig;
import me.xiaoapn.easy.imageloader.decode.BitmapDecoder;
import me.xiaoapn.easy.imageloader.decode.SimpleBitmapDecoder;
import me.xiaoapn.easy.imageloader.display.FadeInBitmapDisplayer;
import me.xiaoapn.easy.imageloader.download.ImageDownloader;
import me.xiaoapn.easy.imageloader.download.LockImageDownloader;
import me.xiaoapn.easy.imageloader.execute.BaseTaskExecutor;
import me.xiaoapn.easy.imageloader.execute.TaskExecutor;
import me.xiaoapn.easy.imageloader.task.Options;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

/**
 * 配置
 */
public class Configuration {
	private boolean debugMode;	//调试模式，在控制台输出日志
	private String logTag;	//LogTag
	private Context context;	//上下文
	private Handler handler;	//消息处理器
	private Resources resources;	//资源
	private TaskExecutor taskExecutor;	//任务执行器
	private BitmapCacher bitmapCacher;	//位图缓存器
	private BitmapDecoder bitmapDecoder;	//位图解码器
	private ImageDownloader imageDownloader;	//图片下载器
	private Map<Object, Options> optionsMap;	//加载选项集合
	
	public Configuration(Context context){
		if(Looper.myLooper() != Looper.getMainLooper()){
			throw new IllegalStateException("你不能在异步线程中创建此对象");
		}
		
		this.logTag = ImageLoader.class.getSimpleName();
		this.context = context;
		this.handler = new Handler();
		this.resources = context.getResources();
		this.optionsMap = new HashMap<Object, Options>();
		putOptions(OptionsDefault.DEFAULT, new Options.Builder()
		.setCacheConfig(new CacheConfig.Builder().setCacheInMemory(true).setCacheInDisk(true).build())
		.setBitmapDisplayer(new FadeInBitmapDisplayer())
		.setMaxSize(new ImageSize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels))
		.setMaxRetryCount(2)
		.build());
	}
	
	/**
	 * 获取上下文
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * 获取资源
	 * @return
	 */
	public Resources getResources() {
		return resources;
	}

	/**
	 * 获取任务执行器
	 * @return
	 */
	public TaskExecutor getTaskExecutor() {
		if(taskExecutor == null){
			taskExecutor = new BaseTaskExecutor();
		}
		return taskExecutor;
	}

	/**
	 * 设置任务执行器
	 * @param taskExecutor
	 */
	public Configuration setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
		return this;
	}

	/**
	 * 获取默认的加载选项
	 * @return
	 */
	public Options getDefaultOptions() {
		return getOptions(OptionsDefault.DEFAULT);
	}
	
	/**
	 * 设置默认的加载选项
	 * @param defaultOptions
	 */
	public Configuration setDefaultOptions(Options defaultOptions) {
		putOptions(OptionsDefault.DEFAULT, defaultOptions);
		return this;
	}
	
	/**
	 * 获取位图缓存器
	 * @return
	 */
	public BitmapCacher getBitmapCacher() {
		if(bitmapCacher == null){
			bitmapCacher = new BitmapLruCacher();
		}
		return bitmapCacher;
	}
	
	/**
	 * 设置位图缓存器
	 * @param bitmapCacher
	 */
	public Configuration setBitmapCacher(BitmapCacher bitmapCacher) {
		this.bitmapCacher = bitmapCacher;
		return this;
	}

	/**
	 * 获取位图解码器
	 * @return 位图解码器
	 */
	public BitmapDecoder getBitmapDecoder() {
		if(bitmapDecoder == null){
			bitmapDecoder = new SimpleBitmapDecoder();
		}
		return bitmapDecoder;
	}

	/**
	 * 设置位图解码器
	 * @param bitmapDecoder 位图解码器
	 */
	public Configuration setBitmapLoader(BitmapDecoder bitmapDecoder) {
		this.bitmapDecoder = bitmapDecoder;
		return this;
	}

	/**
	 * 获取消息处理器
	 * @return
	 */
	public Handler getHandler() {
		return handler;
	}
	
	/**
	 * 获取Log Tag
	 * @return
	 */
	public String getLogTag() {
		return logTag;
	}

	/**
	 * 设置Log Tag
	 * @param logTag
	 */
	public Configuration setLogTag(String logTag) {
		this.logTag = logTag;
		return this;
	}

	/**
	 * 判断是否开启调试模式
	 * @return
	 */
	public boolean isDebugMode() {
		return debugMode;
	}
	
	/**
	 * 设置是否开启调试模式，开启调试模式后会在控制台输出LOG
	 * @param debugMode
	 */
	public Configuration setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
		return this;
	}
	
	/**
	 * 获取加载选项
	 * @param optionsName
	 * @return
	 */
	public Options getOptions(Enum<?> optionsName){
		return this.optionsMap.get(optionsName);
	}
	
	/**
	 * 放入加载选项
	 * @param optionsName
	 * @param options
	 */
	public Configuration putOptions(Enum<?> optionsName, Options options){
		this.optionsMap.put(optionsName, options);
		return this;
	}

	/**
	 * 获取图片下载器
	 * @return
	 */
	public ImageDownloader getImageDownloader() {
		if(imageDownloader == null){
			imageDownloader = new LockImageDownloader();
		}
		return imageDownloader;
	}

	/**
	 * 设置图片下载器
	 * @param imageDownloader
	 */
	public Configuration setImageDownloader(ImageDownloader imageDownloader) {
		this.imageDownloader = imageDownloader;
		return this;
	}

	private enum OptionsDefault{
		DEFAULT;
	}
}