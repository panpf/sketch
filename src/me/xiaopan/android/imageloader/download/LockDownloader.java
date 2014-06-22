package me.xiaopan.android.imageloader.download;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class LockDownloader implements Downloader {
	private static final String NAME = Downloader.class.getSimpleName();
	public static final int DEFAULT_CONNECTION_TIME_OUT = 20000;
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.0; WOW64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.16 Safari/534.24";
	private DefaultHttpClient httpClient;
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
		HttpProtocolParams.setUserAgent(httpParams, DEFAULT_USER_AGENT);	//设置浏览器标识
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
        httpClient.addRequestInterceptor(new GzipProcessRequestInterceptor());
        httpClient.addResponseInterceptor(new GzipProcessResponseInterceptor());
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
	public Object download(DownloadRequest downloadRequest) {
		// 根据下载地址加锁，防止重复下载
		ReentrantLock urlLock = getUrlLock(downloadRequest.getImageUri());
		urlLock.lock();
		
		if(downloadRequest.isCanceled()){
			if(downloadRequest.getConfiguration().isDebugMode()){
				Log.w(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("已取消下载").append("；").append("lock()").append("；").append(downloadRequest.getName()).toString());
			}
			urlLock.unlock();
			return null;
		}
		
		// 如果已经存在就直接返回原文件
		File cacheFile = downloadRequest.getCacheFile();
		if(cacheFile != null && cacheFile.exists()){
			urlLock.unlock();
			return cacheFile;
		}
		
		// 下载
		int numberOfDownload = 0;		// 已下载次数
		Object result = null;
		HttpGet httpGet = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		while(true){
			numberOfDownload++;//加载次数加1
			try {
				httpGet = new HttpGet(downloadRequest.getImageUri());
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
				if(downloadRequest.isCanceled()){
					if(downloadRequest.getConfiguration().isDebugMode()){
						Log.w(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("已取消下载").append("；").append("HttpResponse").append("；").append(downloadRequest.getName()).toString());
					}
					break;
				}
				
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
				
//				inputStream = new BufferedHttpEntity(httpEntity).getContent();
				inputStream = httpEntity.getContent();
				
				// 如果需要缓存到本地
				if(cacheFile != null && downloadRequest.getConfiguration().getDiskCache().applyForSpace(contentLength) && ImageLoaderUtils.createFile(cacheFile)){
					downloadingFiles.add(cacheFile.getPath());	// 标记为正在下载
					outputStream = new BufferedOutputStream(new FileOutputStream(cacheFile, false), 8*1024);
					long completedLength = ImageLoaderUtils.copy(inputStream, outputStream, downloadRequest, contentLength);
					if(!downloadRequest.isCanceled()){
						result = cacheFile;
						if(downloadRequest.getConfiguration().isDebugMode()){
							Log.d(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("下载成功").append("；").append("FILE").append("；").append("文件长度").append(cacheFile.length()).append("/").append(completedLength).append("/").append(contentLength).append("；").append(downloadRequest.getName()).toString());
						}
					}else{
						if(cacheFile != null && cacheFile.exists()){
							cacheFile.delete();
						}
						if(downloadRequest.getConfiguration().isDebugMode()){
							Log.w(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("已取消下载").append("；").append("FILE").append("；").append(downloadRequest.getName()).toString());
						}
					}
					break;
				}
				
				// 直接读到内存
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				outputStream = new BufferedOutputStream(byteArrayOutputStream);
				long completedLength = ImageLoaderUtils.copy(inputStream, outputStream, downloadRequest, contentLength);
				if(!downloadRequest.isCanceled()){
					result = byteArrayOutputStream.toByteArray();
					if(downloadRequest.getConfiguration().isDebugMode()){
						Log.d(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("下载成功").append("；").append("BYTE_ARRAY").append("；").append("字节数").append("=").append(byteArrayOutputStream.size()).append("/").append(completedLength).append("/").append(contentLength).append("；").append(downloadRequest.getName()).toString());
					}
				}else{
					if(downloadRequest.getConfiguration().isDebugMode()){
						Log.w(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("已取消下载").append("；").append("BYTE_ARRAY").append("；").append(downloadRequest.getName()).toString());
					}
				}
				break;
			} catch (Throwable e2) {
				e2.printStackTrace();
				if(httpGet != null) httpGet.abort();
				if(cacheFile != null && cacheFile.exists()) cacheFile.delete();
				//如果是链接超时异常也开启了失败重试，并且重试次数尚未超过限制
				boolean isRetry = (e2 instanceof ConnectTimeoutException || e2 instanceof SocketTimeoutException  || e2 instanceof  ConnectionPoolTimeoutException) && downloadRequest.getDownloadOptions() != null && downloadRequest.getDownloadOptions().getMaxRetryCount() > 0 && numberOfDownload < downloadRequest.getDownloadOptions().getMaxRetryCount();
				if(downloadRequest.getConfiguration().isDebugMode()) Log.e(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("下载异常").append("；").append(downloadRequest.getName()).append("；").append("异常信息").append("=").append(e2.toString()).append("；").append(isRetry?"重新下载":"不再下载").toString());
				if(!isRetry) break;
			}finally{
				ImageLoaderUtils.close(inputStream);
				ImageLoaderUtils.close(outputStream);
			}
		}
		
		if(cacheFile != null){
			downloadingFiles.remove(cacheFile.getPath());	// 标记为下载已结束
		}
		
        // 释放锁
		urlLock.unlock();
		return result;
	}
}
