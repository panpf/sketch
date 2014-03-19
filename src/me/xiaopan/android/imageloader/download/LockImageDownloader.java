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

package me.xiaopan.android.imageloader.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.Request;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.LoadIOUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * 支持URL锁定的下载器
 */
public class LockImageDownloader implements ImageDownloader {
	private static final String NAME = LockImageDownloader.class.getSimpleName();
	private HttpClient httpClient;	//Http客户端
	private Map<String, ReentrantLock> urlLocks;
	
	public LockImageDownloader(){
		this.urlLocks = new WeakHashMap<String, ReentrantLock>();
		BasicHttpParams httpParams = new BasicHttpParams();
		ImageLoaderUtils.setConnectionTimeout(httpParams, 10000);
		ImageLoaderUtils.setMaxConnections(httpParams, 100);
		ImageLoaderUtils.setSocketBufferSize(httpParams, 8192);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams); 
	}
	
	private ReentrantLock getUrlLock(String url){
		ReentrantLock urlLock = urlLocks.get(url);
		if(urlLock == null){
			urlLock = new ReentrantLock();
			urlLocks.put(url, urlLock);
		}
		return urlLock;
	}

	@Override
	public void execute(Request request, File cacheFile, Configuration configuration, DownloadListener onCompleteListener) {
		boolean running = true;
		boolean exists = false;
		Result result = Result.FAILURE;
		ReentrantLock urlLock = null;
		
		if(cacheFile != null){
			urlLock = getUrlLock(request.getImageUri());
			urlLock.lock();
			if(exists = cacheFile.exists()){
				running = false;
				result = Result.FILE;
				if(configuration.isDebugMode()) Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("文件已存在，无需下载").append("；").append(request.getName()).toString());
			}else{
				if(configuration.isDebugMode()) Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载开始").append("；").append(request.getName()).toString());
			}
		}else{
			if(configuration.isDebugMode()) Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载开始").append("；").append(request.getName()).toString());
		}
		
		long fileLength = 0;
		int numberOfLoaded = 0;	//已加载次数
		byte[] data = null;
		while(running){
			numberOfLoaded++;//加载次数加1
			boolean createNewFile = false;	//true：保存图片的文件之前不存在是现在才创建的，当发生异常时需要删除
			HttpGet httpGet = null;
			File parentDir = null;	//本地缓存文件的父目录
			BufferedInputStream bufferedfInputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			
			try {
				httpGet = new HttpGet(request.getImageUri());
				HttpResponse httpResponse = httpClient.execute(httpGet);//请求数据
				
				//读取响应体长度，如果没有响应体长度字段或者长度为0就抛出异常
				Header[] contentTypeString = httpResponse.getHeaders("Content-Length");
				if(contentTypeString.length <= 0){
					throw new Exception("在Http响应中没有取到Content-Length参数");
				}
				
				fileLength = Long.valueOf(contentTypeString[0].getValue());
				if(fileLength <= 0){
					throw new Exception("文件长度为0");
				}
				
				if(cacheFile != null){
					//如果缓存文件不存在就尝试创建
					if(!cacheFile.exists()){
						parentDir = cacheFile.getParentFile();
						if(!parentDir.exists()){
							parentDir.mkdirs();
						}else{
							parentDir = null;
						}
						createNewFile = cacheFile.createNewFile();
					}
					
					//如果依然没有创建成功，就抛出异常
					if(!cacheFile.exists()){
						throw new Exception("文件 "+cacheFile.getPath()+" 创建失败");
					}
					
					//申请空间
					configuration.getBitmapCacher().setCacheFileLength(cacheFile, fileLength);
					
					/* 读取数据并写入缓存文件 */
					bufferedfInputStream = new BufferedInputStream(httpResponse.getEntity().getContent());
					bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(cacheFile, false));
					int readNumber;	//读取到的字节的数量
					byte[] cacheBytes = new byte[1024];//数据缓存区
					while((readNumber = bufferedfInputStream.read(cacheBytes)) != -1){
						bufferedOutputStream.write(cacheBytes, 0, readNumber);
					}
					bufferedfInputStream.close();
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					
					result = Result.FILE;
				}else{
					data = EntityUtils.toByteArray(new BufferedHttpEntity(httpResponse.getEntity()));
					result = Result.BYTE_ARRAY;
				}
				
				running = false;
			} catch (Throwable e2) {
				e2.printStackTrace();
				if(httpGet != null) httpGet.abort();
				LoadIOUtils.close(bufferedfInputStream);
				LoadIOUtils.close(bufferedOutputStream);
				if(createNewFile && cacheFile != null && cacheFile.exists()) cacheFile.delete();	//如果创建了新文件就删除
				if(parentDir != null && parentDir.exists()) parentDir.delete();	//如果创建了新目录就删除
				running = ((e2 instanceof ConnectTimeoutException || e2 instanceof SocketTimeoutException  || e2 instanceof  ConnectionPoolTimeoutException) && request.getDisplayOptions().getMaxRetryCount() > 0)?numberOfLoaded < request.getDisplayOptions().getMaxRetryCount():false;	//如果尚未达到最大重试次数，那么就再尝试一次
				
				if(configuration.isDebugMode()){
					Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载异常").append("；").append(request.getName()).append("；").append("异常信息").append("=").append(e2.toString()).append("；").append(running?"重新下载":"不再下载").toString());
				}
			}
		}
		
		if(urlLock != null){
			urlLock.unlock();
		}
		
		switch(result){
			case FILE : 
				if(onCompleteListener != null){
					if(cacheFile != null && cacheFile.exists() && (exists || (fileLength >0 && cacheFile.length() == fileLength))){
						if(configuration.isDebugMode() && !exists) Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载成功 - FILE").append("；").append(request.getName()).toString());
						onCompleteListener.onComplete(cacheFile);
					}else{
						if(configuration.isDebugMode()) Log.w(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载失败 - FILE").append("；").append(request.getName()).toString());
						onCompleteListener.onFailed();
					}
				}
				break;
			case BYTE_ARRAY : 
				if(onCompleteListener != null){
					if(data != null && (exists || (fileLength >0 && data.length == fileLength))){
						if(configuration.isDebugMode() && !exists) Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载成功 - BYTE_ARRAY").append("；").append(request.getName()).toString());
						onCompleteListener.onComplete(data);
					}else{
						if(configuration.isDebugMode()) Log.w(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载失败 - BYTE_ARRAY").append("；").append(request.getName()).toString());
						onCompleteListener.onFailed();
					}
				}
				break;
			default : 
				if(onCompleteListener != null){
					if(configuration.isDebugMode()) Log.w(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载失败").append("；").append(request.getName()).toString());
					onCompleteListener.onFailed();
				}
				break;
		}
	}
	
	private enum Result{
		FILE, BYTE_ARRAY, FAILURE;
	}
}
