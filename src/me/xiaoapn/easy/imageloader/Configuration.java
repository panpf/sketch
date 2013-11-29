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

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

/**
 * 配置
 */
public class Configuration {
	private int maxThreadNumber = 20;	//最大线程数
	private int maxWaitingNumber = 10;	//最大等待数
	private int connectionTimeout = 10000;	//连接超时时间
	private int maxConnections = 10;	//最大连接数
	private int socketBufferSize = 8192;	//Socket缓存池大小
	private String logTag = "ImageLoader";	//LogTag
	private String defaultCacheDirectory;	//默认的缓存目录
	private Options defaultOptions;	//默认加载选项
	private Handler handler = new Handler();;	//任务结果处理器
	private BitmapCacher bitmapCacher;	//位图缓存器
	private HttpClient httpClient;	//Http客户端
	private ThreadPoolExecutor threadPool;	//线程池
	private CircleList<LoadRequest> waitingRequestCircle;	//等待处理的加载请求
	
	private Configuration(){
		
	}
	
	/**
	 * 获取Http客户端
	 * @return
	 */
	public final HttpClient getHttpClient() {
		if(httpClient == null){
			BasicHttpParams httpParams = new BasicHttpParams();
			GeneralUtils.setConnectionTimeout(httpParams, connectionTimeout);
			GeneralUtils.setMaxConnections(httpParams, maxConnections);
			GeneralUtils.setSocketBufferSize(httpParams, socketBufferSize);
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
     * 获取线程池
     * @return
     */
    public ThreadPoolExecutor getThreadPool() {
        if(threadPool == null){
            threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }
        return threadPool;
    }
    
	/**
	 * 设置线程池
	 * @param threadPool
	 */
	public void setThreadPool(ThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}


	/**
	 * 获取最大线程数
	 * @return
	 */
	public int getMaxThreadNumber() {
		return maxThreadNumber;
	}

	/**
	 * 设置最大线程数
	 * @param maxThreadNumber
	 */
	public void setMaxThreadNumber(int maxThreadNumber) {
		if(maxThreadNumber > 0){
			this.maxThreadNumber = maxThreadNumber;
		}
	}
	
	/**
	 * 获取最大等待数，即等待区的最大容量
	 * @return
	 */
	public int getMaxWaitingNumber() {
		return maxWaitingNumber;
	}

	/**
	 * 设置最大等待数
	 * @param maxWaitingNumber
	 */
	public void setMaxWaitingNumber(int maxWaitingNumber) {
		if(maxWaitingNumber > 0){
			this.maxWaitingNumber = maxWaitingNumber;
			getWaitingRequestCircle().setMaxSize(maxWaitingNumber);
		}
	}
	
	/**
	 * 获取默认加载选项，当没有单独指定加载选项时，将默认使用此加载选项
	 * @return
	 */
	public Options getDefaultOptions() {
		if(defaultOptions == null){
			defaultOptions = new Options.Builder().setCacheInLocal(true).setShowAnimationListener(new AlphaShowAnimationListener()).setBitmapLoader(new PixelsBitmapLoader()).create();
		}
		return defaultOptions;
	}
	
	/**
	 * 设置加载选项，当没有单独指定加载选项时，将默认使用此加载选项
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
			bitmapCacher = new BitmapLruCacher();
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
	 * 获取默认的缓存目录，当没有指定单独的缓存目录时将使用此缓存目录
	 * @return
	 */
	public String getDefaultCacheDirectory() {
		return defaultCacheDirectory;
	}

	/**
	 * 设置默认的缓存目录，当没有指定单独的缓存目录时将使用此缓存目录
	 * @param defaultCacheDirectory
	 */
	public void setDefaultCacheDirectory(String defaultCacheDirectory) {
		if(GeneralUtils.isNotEmpty(defaultCacheDirectory)){
			this.defaultCacheDirectory = defaultCacheDirectory;
		}
	}
	
	/**
	 * 获取消息处理器
	 * @return
	 */
	Handler getHandler() {
		return handler;
	}

	/**
	 * 获取缓存文件，将优先考虑options指定的缓存目录，然后考虑当前configuration指定的缓存目录，然后考虑通过context获取默认的应用缓存目录，再然后就要返回null了
	 * @param context
	 * @param options
	 * @param fileName
	 * @return
	 */
	public File getCacheFile(Context context, Options options, String fileName){
		if(options != null && GeneralUtils.isNotEmpty(options.getCacheDirectory())){
			return new File(options.getCacheDirectory() + File.separator + fileName);
		}else if(GeneralUtils.isNotEmpty(getDefaultCacheDirectory())){
			return new File(getDefaultCacheDirectory() + File.separator + fileName);
		}else if(context != null){
			return new File(GeneralUtils.getDynamicCacheDir(context).getPath() + File.separator + "image_loader" + File.separator + fileName);
		}else{
			return null;
		}
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
	 * 获取连接超时时间，单位毫秒
	 * @return
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * 设置连接超时间，单位毫秒
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		if(GeneralUtils.setConnectionTimeout(getHttpClient(), connectionTimeout)){
			this.connectionTimeout = connectionTimeout;
		}
	}

	/**
	 * 获取最大连接数
	 * @return
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * 设置最大连接数
	 * @param maxConnections
	 */
	public void setMaxConnections(int maxConnections) {
		if(GeneralUtils.setMaxConnections(getHttpClient(), maxConnections)){
			this.maxConnections = maxConnections;
		}
	}

	/**
	 * 获取Socket缓存池大小
	 * @return
	 */
	public int getSocketBufferSize() {
		return socketBufferSize;
	}

	/**
	 * 设置Socket缓存池大小
	 * @param socketBufferSize
	 */
	public void setSocketBufferSize(int socketBufferSize) {
		if(GeneralUtils.setSocketBufferSize(getHttpClient(), socketBufferSize)){
			this.socketBufferSize = socketBufferSize;
		}
	}

	/**
	 * 获取等待请求集合
	 * @return 等待请求集合
	 */
	final CircleList<LoadRequest> getWaitingRequestCircle() {
		if(waitingRequestCircle == null){
			waitingRequestCircle = new CircleList<LoadRequest>(getMaxWaitingNumber());//初始化等待处理的加载请求集合
		}
		return waitingRequestCircle;
	}
	
	/**
	 * ImageLoadder配置创建器
	 */
	public static class Builder{
		Configuration configuration;
		
		public Builder(){
			configuration = new Configuration();
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
		 * 设置线程池
		 * @param threadPool
		 */
		public Builder setThreadPool(ThreadPoolExecutor threadPool) {
			configuration.setThreadPool(threadPool);
			return this;
		}

		/**
		 * 设置最大线程数
		 * @param maxThreadNumber
		 */
		public Builder setMaxThreadNumber(int maxThreadNumber) {
			configuration.setMaxThreadNumber(maxThreadNumber);
			return this;
		}

		/**
		 * 设置最大等待数
		 * @param maxWaitingNumber
		 */
		public Builder setMaxWaitingNumber(int maxWaitingNumber) {
			configuration.setMaxWaitingNumber(maxWaitingNumber);
			return this;
		}
		
		/**
		 * 设置加载选项，当没有单独指定加载选项时，将默认使用此加载选项
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
		 * 设置默认的缓存目录，当没有指定单独的缓存目录时将使用此缓存目录
		 * @param defaultCacheDirectory
		 */
		public Builder setDefaultCacheDirectory(String defaultCacheDirectory) {
			configuration.setDefaultCacheDirectory(defaultCacheDirectory);
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
		 * 设置连接超时间，单位毫秒
		 * @param connectionTimeout
		 */
		public Builder setConnectionTimeout(int connectionTimeout) {
			configuration.setConnectionTimeout(connectionTimeout);
			return this;
		}

		/**
		 * 设置最大连接数
		 * @param maxConnections
		 */
		public Builder setMaxConnections(int maxConnections) {
			configuration.setMaxConnections(maxConnections);
			return this;
		}

		/**
		 * 设置Socket缓存池大小
		 * @param socketBufferSize
		 */
		public Builder setSocketBufferSize(int socketBufferSize) {
			configuration.setSocketBufferSize(socketBufferSize);
			return this;
		}
		
		/**
		 * 创建
		 * @return
		 */
		public Configuration create(){
			return configuration;
		}
	}
}