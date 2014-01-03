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

import me.xiaoapn.easy.imageloader.cache.BitmapCacher;
import me.xiaoapn.easy.imageloader.cache.CacheConfig;
import me.xiaoapn.easy.imageloader.cache.LruBitmapCacher;
import me.xiaoapn.easy.imageloader.decode.BitmapLoader;
import me.xiaoapn.easy.imageloader.decode.PixelsBitmapLoader;
import me.xiaoapn.easy.imageloader.display.SimpleBitmapDisplayer;
import me.xiaoapn.easy.imageloader.execute.BaseTaskExecutor;
import me.xiaoapn.easy.imageloader.execute.TaskExecutor;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import me.xiaoapn.easy.imageloader.util.ImageSize;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
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
	private Options defaultOptions;	//默认加载选项
	private HttpClient httpClient;	//Http客户端
	private TaskExecutor taskExecutor;	//任务执行器
	private BitmapCacher bitmapCacher;	//位图缓存器
	private BitmapLoader bitmapLoader;	//位图加载器
	
	private Configuration(Context context){
		if(Looper.myLooper() != Looper.getMainLooper()){
			throw new IllegalStateException("你不能在异步线程中创建此对象");
		}
		
		this.context = context;
		this.logTag = ImageLoader.class.getSimpleName();
		this.handler = new Handler();
		this.defaultOptions = new Options.Builder()
		.setCacheConfig(new CacheConfig.Builder().setCacheInMemory(true).setCacheInDisk(true).build())
		.setBitmapDisplayer(new SimpleBitmapDisplayer())
		.setMaxImageSize(new ImageSize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels))
		.setMaxRetryCount(2)
		.create();
	}
	
	/**
	 * 获取上下文
	 * @return
	 */
	public Context getContext() {
		return context;
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
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * 获取默认的加载选项
	 * @return
	 */
	public Options getDefaultOptions() {
		return defaultOptions;
	}
	
	/**
	 * 设置默认的加载选项，当使用loadByDefault()方法的时候就会使用此加载选项
	 * @param defaultOptions
	 */
	public void setDefaultOptions(Options defaultOptions) {
		this.defaultOptions = defaultOptions;
	}
	
	/**
	 * 获取位图缓存器
	 * @return
	 */
	public BitmapCacher getBitmapCacher() {
		if(bitmapCacher == null){
			bitmapCacher = new LruBitmapCacher();
		}
		return bitmapCacher;
	}
	
	/**
	 * 设置位图缓存器
	 * @param bitmapCacher
	 */
	public void setBitmapCacher(BitmapCacher bitmapCacher) {
		this.bitmapCacher = bitmapCacher;
	}

	/**
	 * 获取位图加载器
	 * @return 位图加载器
	 */
	public BitmapLoader getBitmapLoader() {
		if(bitmapLoader == null){
			bitmapLoader = new PixelsBitmapLoader();
		}
		return bitmapLoader;
	}

	/**
	 * 设置位图加载器
	 * @param bitmapLoader 位图加载器
	 */
	public void setBitmapLoader(BitmapLoader bitmapLoader) {
		this.bitmapLoader = bitmapLoader;
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
	public void setLogTag(String logTag) {
		this.logTag = logTag;
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
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * 获取Http客户端
	 * @return
	 */
	public final HttpClient getHttpClient() {
		if(httpClient == null){
			BasicHttpParams httpParams = new BasicHttpParams();
			GeneralUtils.setConnectionTimeout(httpParams, 10000);
			GeneralUtils.setMaxConnections(httpParams, 100);
			GeneralUtils.setSocketBufferSize(httpParams, 8192);
	        HttpConnectionParams.setTcpNoDelay(httpParams, true);
	        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams); 
		}
		return httpClient;
	}

	/**
	 * 设置Http客户端
	 * @param httpClient
	 */
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * ImageLoadder配置创建器
	 */
	public static class Builder{
		Configuration configuration;
		
		public Builder(Context context){
			configuration = new Configuration(context);
		}

		/**
		 * 设置位图加载器
		 * @param bitmapLoader 位图加载器
		 */
		public Builder setBitmapLoader(BitmapLoader bitmapLoader) {
			configuration.setBitmapLoader(bitmapLoader);
			return this;
		}

		/**
		 * 设置Http客户端
		 * @param httpClient
		 */
		public Builder setHttpClient(HttpClient httpClient) {
			configuration.setHttpClient(httpClient);
			return this;
		}
	    
		/**
		 * 设置加载选项
		 * @param defaultOptions
		 */
		public Builder setDefaultOptions(Options defaultOptions) {
			configuration.setDefaultOptions(defaultOptions);
			return this;
		}
		
		/**
		 * 设置位图缓存器
		 * @param bitmapCacher
		 */
		public Builder setBitmapCacher(BitmapCacher bitmapCacher) {
			configuration.setBitmapCacher(bitmapCacher);
			return this;
		}

		/**
		 * 设置Log Tag
		 * @param logTag
		 */
		public Builder setLogTag(String logTag) {
			configuration.setLogTag(logTag);
			return this;
		}

		/**
		 * 设置是否开启调试模式，开启调试模式后会在控制台输出LOG
		 * @param debugMode
		 */
		public Builder setDebugMode(boolean debugMode) {
			configuration.setDebugMode(debugMode);
			return this;
		}

		/**
		 * 设置任务执行器
		 * @param taskExecutor
		 */
		public Builder setTaskExecutor(TaskExecutor taskExecutor) {
			configuration.setTaskExecutor(taskExecutor);
			return this;
		}
		
		/**
		 * 创建
		 * @return
		 */
		public Configuration build(){
			return configuration;
		}
	}
}