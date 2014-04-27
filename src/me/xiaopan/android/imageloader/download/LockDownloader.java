package me.xiaopan.android.imageloader.download;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
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

import android.util.Log;

public class LockDownloader implements Downloader {
	private static final String NAME = Downloader.class.getSimpleName();
	public static final int DEFAULT_CONNECTION_TIME_OUT = 20000;
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	private HttpClient httpClient;
	private Set<String> downloadingFiles;
	private Map<String, ReentrantLock> urlLocks;

	public LockDownloader() {
		this.urlLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());
		this.downloadingFiles = Collections.synchronizedSet(new HashSet<String>());
		BasicHttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, DEFAULT_CONNECTION_TIME_OUT);
        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_CONNECTION_TIME_OUT);
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_CONNECTION_TIME_OUT);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_MAX_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, String.format("Android-ImageLoader/%s (https://github.com/xiaopansky/Android-ImageLoader)", "2.3.3"));	//设置浏览器标识
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
	}

    /**
     * 获取一个URL锁，通过此锁可以防止重复下载
     * @param url 下载地址
     * @return URL锁
     */
	public synchronized ReentrantLock getUrlLock(String url){
		ReentrantLock urlLock = urlLocks.get(url);
		if(urlLock == null){
			urlLock = new ReentrantLock();
			urlLocks.put(url, urlLock);
		}
		return urlLock;
	}

	@Override
	public synchronized boolean isDownloadingByCacheFilePath(String cacheFilePath) {
		return downloadingFiles.contains(cacheFilePath);
	}

	@Override
	public Object down(DownloadRequest downloadRequest) {
		Object result = null;
		
		// 根据下载地址加锁，防止重复下载
		ReentrantLock urlLock = getUrlLock(downloadRequest.getUri());
		urlLock.lock();
		
		// 如果已经存在就直接返回原文件
		if(downloadRequest.getCacheFile() != null && downloadRequest.getCacheFile().exists()){
			result = downloadRequest.getCacheFile();
		}else{
			int numberOfLoaded = 0;	//已加载次数
			while(true){
				numberOfLoaded++;//加载次数加1
				HttpGet httpGet = null;
				InputStream inputStream = null;
				OutputStream outputStream = null;
				try {
					httpGet = new HttpGet(downloadRequest.getUri());
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					// 检查状态码
					if(httpResponse.getStatusLine().getStatusCode() != 200){
						throw new Exception("状态码异常："+httpResponse.getStatusLine().getStatusCode());
					}
					
					// 检查ContentType
					HttpEntity httpEntity = httpResponse.getEntity();
					String contentTypeValue = httpEntity.getContentType().getValue();
					if(!contentTypeValue.startsWith("image")){
						throw new Exception("ContentType异常："+contentTypeValue);
					}
					
					// 检查ContentLength
					long contentLength = httpEntity.getContentLength();
					if(contentLength < 0){
						throw new Exception("ContentLength异常："+contentTypeValue);
					}
					
					inputStream = new BufferedHttpEntity(httpEntity).getContent();
					
					// 如果需要缓存到本地
					if(downloadRequest.getCacheFile() != null && downloadRequest.getConfiguration().getDiskCache().applyForSpace(contentLength) && ImageLoaderUtils.createFile(downloadRequest.getCacheFile())){
						downloadingFiles.add(downloadRequest.getCacheFile().getPath());	// 标记为正在下载
						outputStream = new BufferedOutputStream(new FileOutputStream(downloadRequest.getCacheFile(), false), 8*1024);
						long completedLength = ImageLoaderUtils.copy(inputStream, outputStream, downloadRequest.getDownloadListener(), contentLength);
						ImageLoaderUtils.close(inputStream);
						ImageLoaderUtils.close(outputStream);
						downloadingFiles.remove(downloadRequest.getCacheFile().getPath());	// 标记为下载已完成
						result = downloadRequest.getCacheFile();
						if(downloadRequest.getConfiguration().isDebugMode()){
							Log.d(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("下载成功 - FILE").append("；").append("文件长度").append(downloadRequest.getCacheFile().length()).append("/").append(completedLength).append("/").append(contentLength).append("；").append(downloadRequest.getName()).toString());
						}
						break;
					}
					
					// 直接读到内存
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					outputStream = new BufferedOutputStream(byteArrayOutputStream);
					long completedLength = ImageLoaderUtils.copy(inputStream, outputStream, downloadRequest.getDownloadListener(), contentLength);
					ImageLoaderUtils.close(inputStream);
					ImageLoaderUtils.close(outputStream);
					result = byteArrayOutputStream.toByteArray();
					if(downloadRequest.getConfiguration().isDebugMode()){
						Log.d(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("下载成功 - BYTE_ARRAY").append("；").append("字节数").append("=").append(byteArrayOutputStream.size()).append("/").append(completedLength).append("/").append(contentLength).append("；").append(downloadRequest.getName()).toString());
					}
					break;
				} catch (Throwable e2) {
					ImageLoaderUtils.close(inputStream);
					ImageLoaderUtils.close(outputStream);
					
					if(httpGet != null){
						httpGet.abort();
					}
					
					if(downloadRequest.getCacheFile() != null && downloadRequest.getCacheFile().exists()){
						downloadRequest.getCacheFile().delete();
					}
					
					boolean isRetry = false;	//如果尚未达到最大重试次数，那么就再尝试一次
					if(e2 instanceof ConnectTimeoutException || e2 instanceof SocketTimeoutException  || e2 instanceof  ConnectionPoolTimeoutException){
						if(downloadRequest.getDownloadOptions() != null && downloadRequest.getDownloadOptions().getMaxRetryCount() > 0){
							isRetry = numberOfLoaded < downloadRequest.getDownloadOptions().getMaxRetryCount();
						}
					}else{
						e2.printStackTrace();
					}
					
					if(downloadRequest.getConfiguration().isDebugMode()){
						Log.d(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("下载异常").append("；").append(downloadRequest.getName()).append("；").append("异常信息").append("=").append(e2.toString()).append("；").append(isRetry?"重新下载":"不再下载").toString());
					}
					
					if(!isRetry){
						break;
					}
				}
			}
		}
		
        // 释放锁
		urlLock.unlock();
		return result;
	}
}
