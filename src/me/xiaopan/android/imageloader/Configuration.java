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

package me.xiaopan.android.imageloader;

import java.util.HashMap;
import java.util.Map;

import me.xiaopan.android.imageloader.cache.disk.DiskCache;
import me.xiaopan.android.imageloader.cache.disk.LruDiskCache;
import me.xiaopan.android.imageloader.cache.memory.LruMemoryCache;
import me.xiaopan.android.imageloader.cache.memory.MemoryCache;
import me.xiaopan.android.imageloader.decode.BitmapDecoder;
import me.xiaopan.android.imageloader.decode.DefaultBitmapDecoder;
import me.xiaopan.android.imageloader.download.Downloader;
import me.xiaopan.android.imageloader.download.LockDownloader;
import me.xiaopan.android.imageloader.execute.DefaultRequestExecutor;
import me.xiaopan.android.imageloader.execute.RequestExecutor;
import me.xiaopan.android.imageloader.task.TaskOptions;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * 配置
 */
public class Configuration {
	private boolean debugMode;	//调试模式，在控制台输出日志
	private Handler handler;	//消息处理器
	private Context context;	//上下文
    private DiskCache diskCache;    // 磁盘缓存器
    private Downloader downloader;	//下载器
	private MemoryCache memoryCache;	//位图缓存器
	private BitmapDecoder bitmapDecoder;	//位图解码器
	private RequestExecutor requestExecutor;	//请求执行器
	private Map<Object, TaskOptions> optionsMap;	//显示选项集合
	
	public Configuration(Context context){
		if(Looper.myLooper() != Looper.getMainLooper()){
			throw new IllegalStateException("你不能在异步线程中创建此对象");
		}
		
		this.handler = new Handler();
		this.context = context;
		this.diskCache = new LruDiskCache(context);
		this.downloader = new LockDownloader();
		this.optionsMap = new HashMap<Object, TaskOptions>();
		this.memoryCache = new LruMemoryCache();
		this.bitmapDecoder = new DefaultBitmapDecoder();
		this.requestExecutor = new DefaultRequestExecutor();
	}
	
	/**
	 * 获取上下文
	 * @return 上下文
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * 获取请求执行器
	 * @return 请求执行器
	 */
	public RequestExecutor getRequestExecutor() {
		return requestExecutor;
	}

	/**
	 * 设置请求执行器
	 * @param requestExecutor 请求执行器
	 */
	public Configuration setRequestExecutor(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
		return this;
	}

    /**
     * 获取磁盘缓存器
     * @return 磁盘缓存器
     */
    public DiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * 设置磁盘缓存器
     * @param diskCache 磁盘缓存器
     */
    public Configuration setDiskCache(DiskCache diskCache) {
        this.diskCache = diskCache;
        return this;
    }

    /**
	 * 获取内存缓存器
	 * @return 内存缓存器
	 */
	public MemoryCache getMemoryCache() {
		return memoryCache;
	}
	
	/**
	 * 设置内存缓存器
	 * @param memoryCache 内存缓存器
	 */
	public Configuration setMemoryCache(MemoryCache memoryCache) {
		this.memoryCache = memoryCache;
		return this;
	}

	/**
	 * 获取位图解码器
	 * @return 位图解码器
	 */
	public BitmapDecoder getBitmapDecoder() {
		return bitmapDecoder;
	}

	/**
	 * 设置位图解码器
	 * @param bitmapDecoder 位图解码器
	 */
	public Configuration setBitmapDecoder(BitmapDecoder bitmapDecoder) {
		this.bitmapDecoder = bitmapDecoder;
		return this;
	}

	/**
	 * 获取消息处理器
	 * @return 消息处理器，用来实现在主线程显示图片
	 */
	public Handler getHandler() {
		return handler;
	}
	
	/**
	 * 是否开启调试模式
	 * @return 是否开启调试模式，开启调试模式后会在控制台输出LOG
	 */
	public boolean isDebugMode() {
		return debugMode;
	}
	
	/**
	 * 设置是否开启调试模式
	 * @param debugMode 是否开启调试模式，开启调试模式后会在控制台输出LOG
	 */
	public Configuration setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
		return this;
	}
	
	/**
	 * 获取选项
	 * @param optionsName 选项名称
	 * @return 选项
	 */
	public TaskOptions getOptions(Enum<?> optionsName){
		return this.optionsMap.get(optionsName);
	}
	
	/**
	 * 放入选项
	 * @param optionsName 选项名称
	 * @param options 选项
	 */
	public Configuration putOptions(Enum<?> optionsName, TaskOptions options){
		this.optionsMap.put(optionsName, options);
		return this;
	}

    /**
     * 获取下载器
     */
	public Downloader getDownloader() {
		return downloader;
	}

	/**
	 * 设置下载器
	 * @param downloader
	 */
	public void setDownloader(Downloader downloader) {
		this.downloader = downloader;
	}
}