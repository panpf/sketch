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

package me.xiaopan.android.imageloader.task.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

public class DownloadCallable implements Callable<Object>{
    private static final int DEFAULT_CONNECTION_TIME_OUT = 2000;
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	private static final String NAME = DownloadCallable.class.getSimpleName();
	private static final Map<String, ReentrantLock> urlLocks = new WeakHashMap<String, ReentrantLock>();
	private DownloadRequest downloadRequest;
	
	public DownloadCallable(DownloadRequest downloadRequest) {
		this.downloadRequest = downloadRequest;
	}

	@Override
	public Object call(){
		ReentrantLock urlLock = getUrlLock(downloadRequest.getUri());
		urlLock.lock();
		Object result = download();
		urlLock.unlock();
		return result;
	}

    /**
     * 下载
     * @return 下载结果，可能是一个File也可能是一个byte[]
     */
	private Object download(){
		//如果已经存在就直接返回原文件
		if(downloadRequest.getCacheFile() != null && downloadRequest.getCacheFile().exists()){
			if(downloadRequest.getConfiguration().isDebugMode()){
				Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("文件已存在，无需下载").append("；").append(downloadRequest.getName()).toString());
			}
			return downloadRequest.getCacheFile();
		}
		
		if(downloadRequest.getConfiguration().isDebugMode()){
			Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载开始").append("；").append(downloadRequest.getName()).toString());
		}
		
		Object result = null;
		int numberOfLoaded = 0;	//已加载次数
		DefaultHttpClient defaultHttpClient = createHttpClient();
		while(true){
			numberOfLoaded++;//加载次数加1
			HttpGet httpGet = null;
			BufferedInputStream bufferedfInputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			try {
				//发送请求
				httpGet = new HttpGet(downloadRequest.getUri());
				HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
				long fileLength = parseContentLength(httpResponse);
				
				//读取数据
				bufferedfInputStream = new BufferedInputStream(httpResponse.getEntity().getContent(), ImageLoaderUtils.BUFFER_SIZE);
				if(downloadRequest.getCacheFile() != null && ImageLoaderUtils.createFile(downloadRequest.getCacheFile())){
					downloadRequest.getConfiguration().getBitmapCacher().setCacheFileLength(downloadRequest.getCacheFile(), fileLength);
					bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(downloadRequest.getCacheFile(), false));
					copy(bufferedfInputStream, bufferedOutputStream, fileLength);
                    if(downloadRequest.getCacheFile().length() == fileLength){
					    result = downloadRequest.getCacheFile();
                        if(downloadRequest.getConfiguration().isDebugMode()){
                            Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载成功 - FILE").append("；").append(downloadRequest.getName()).toString());
                        }
                    }else{
                        downloadRequest.getCacheFile().delete();
                        result = null;
                        if(downloadRequest.getConfiguration().isDebugMode()){
                            Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载失败 - FILE - 文件长度不匹配").append("；").append(downloadRequest.getName()).toString());
                        }
                    }
				}else{
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
					copy(bufferedfInputStream, bufferedOutputStream, fileLength);
					byte[] data = byteArrayOutputStream.toByteArray();
                    if(data.length == fileLength){
                        result = data;
                        if(downloadRequest.getConfiguration().isDebugMode()){
                            Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载成功 - BYTE_ARRAY").append("；").append(downloadRequest.getName()).toString());
                        }
                    }else{
                        result = null;
                        if(downloadRequest.getConfiguration().isDebugMode()){
                            Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载失败 - BYTE_ARRAY - 数据长度不匹配").append("；").append(downloadRequest.getName()).toString());
                        }
                    }
				}
				break;
			} catch (Throwable e2) {
				e2.printStackTrace();
				if(httpGet != null){
					httpGet.abort();
				}
				
				//删除文件
				if(downloadRequest.getCacheFile() != null && downloadRequest.getCacheFile().exists()){
					downloadRequest.getCacheFile().delete();
				}
				
				boolean isRetry = false;	//如果尚未达到最大重试次数，那么就再尝试一次
				if(e2 instanceof ConnectTimeoutException || e2 instanceof SocketTimeoutException  || e2 instanceof  ConnectionPoolTimeoutException){
					if(downloadRequest.getDownloadOptions() != null && downloadRequest.getDownloadOptions().getMaxRetryCount() > 0){
						isRetry = numberOfLoaded < downloadRequest.getDownloadOptions().getMaxRetryCount();
					}
				}
				
				if(downloadRequest.getConfiguration().isDebugMode()){
					Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("下载异常").append("；").append(downloadRequest.getName()).append("；").append("异常信息").append("=").append(e2.toString()).append("；").append(isRetry?"重新下载":"不再下载").toString());
				}
				
				if(!isRetry){
					break;
				}
			}finally{
				ImageLoaderUtils.close(bufferedfInputStream);
				ImageLoaderUtils.close(bufferedOutputStream);
			}
		}
		return result;
	}

    private long copy(InputStream inputStream, OutputStream outputStream, long totalLength) throws IOException{
        int readNumber;	//读取到的字节的数量
        long completedLength = 0;
        byte[] cacheBytes = new byte[1024];//数据缓存区
        while((readNumber = inputStream.read(cacheBytes)) != -1){
            completedLength += readNumber;
            outputStream.write(cacheBytes, 0, readNumber);
            if(downloadRequest.getDownloadListener() != null){
                downloadRequest.getDownloadListener().onUpdateProgress(totalLength, completedLength);
            }
        }
        outputStream.flush();
        return completedLength;
    }

    /**
     * 创建一个HTTP客户端
     * @return HTTP客户端
     */
	private static DefaultHttpClient createHttpClient(){
		BasicHttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, DEFAULT_CONNECTION_TIME_OUT);
        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_CONNECTION_TIME_OUT);
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_CONNECTION_TIME_OUT);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_MAX_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		return new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams); 
	}

    /**
     * 解析内容长度
     * @param httpResponse http响应
     * @return 内容长度
     * @throws Exception
     */
	private static long parseContentLength(HttpResponse httpResponse) throws Exception{
		Header[] contentTypeString = httpResponse.getHeaders("Content-Length");
        if(contentTypeString.length <= 0){
            throw new Exception("在Http响应中没有取到Content-Length参数");
        }

        long fileLength = Long.valueOf(contentTypeString[0].getValue());
        if(fileLength <= 0){
            throw new Exception("文件长度为0");
        }
		return fileLength;
	}

    /**
     * 获取一个URL锁，通过此锁可以过滤重复下载
     * @param url 下载地址
     * @return URL锁
     */
	private static ReentrantLock getUrlLock(String url){
		ReentrantLock urlLock = urlLocks.get(url);
		if(urlLock == null){
			urlLock = new ReentrantLock();
			urlLocks.put(url, urlLock);
		}
		return urlLock;
	}
}