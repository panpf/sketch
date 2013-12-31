package me.xiaoapn.easy.imageloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * 图片下载器
 */
public class ImageDownloader {
	private int maxRetryCount;
	private File cacheFile;
	private String url;
	private String requestName;
	private HttpClient httpClient;
	private ImageLoader imageLoader;
	private OnCompleteListener onCompleteListener;
	
	public ImageDownloader(String requestName, String url, File cacheFile, int maxRetryCount, HttpClient httpClient, ImageLoader imageLoader, OnCompleteListener onCompleteListener) {
		this.url = url;
		this.cacheFile = cacheFile;
		this.httpClient = httpClient;
		this.imageLoader = imageLoader;
		this.requestName = requestName;
		this.maxRetryCount = maxRetryCount;
		this.onCompleteListener = onCompleteListener;
	}
	
	/**
	 * 执行
	 */
	public void execute(){
		int numberOfLoaded = 0;	//已加载次数
		byte[] data = null;
		boolean running = true;
		Result result = Result.FAILURE;
		
		while(running){
			numberOfLoaded++;//加载次数加1
			boolean createNewFile = false;	//true：保存图片的文件之前不存在是现在才创建的，当发生异常时需要删除
			HttpGet httpGet = null;
			File localCacheParentDir = null;	//本地缓存文件的父目录
			BufferedInputStream bufferedfInputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			
			try {
				httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);//请求数据
				
				//读取响应体长度，如果没有响应体长度字段或者长度为0就抛出异常
				long fileLength;
				Header[] contentTypeString = httpResponse.getHeaders("Content-Length");
				if(contentTypeString.length > 0){
					fileLength = Long.valueOf(contentTypeString[0].getValue());
					if(fileLength <= 0){
						throw new Exception("文件长度为0");
					}
				}else{
					throw new Exception("在Http响应中没有取到Content-Length参数");
				}
				
				if(cacheFile != null){
					//如果缓存文件不存在就尝试创建
					if(!cacheFile.exists()){
						File parentDir = cacheFile.getParentFile();
						if(!parentDir.exists()){
							localCacheParentDir = parentDir;
							parentDir.mkdirs();
						}
						createNewFile = cacheFile.createNewFile();
					}
					
					//如果依然没有创建成功，就抛出异常
					if(!cacheFile.exists()){
						throw new Exception("文件"+cacheFile.getPath()+"创建失败");
					}
					
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
				imageLoader.getConfiguration().log(requestName+"加载失败，异常信息："+e2.toString(), true);
				
				if(httpGet != null){
					httpGet.abort();
				}
				
				//尝试关闭输入流
				if(bufferedfInputStream != null){
					try {
						bufferedfInputStream.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				//尝试关闭输出流
				if(bufferedOutputStream != null){
					try {
						bufferedOutputStream.flush();
						bufferedOutputStream.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				//如果创建了新文件就删除
				if(createNewFile && cacheFile != null && cacheFile.exists()){
					cacheFile.delete();
				}
				
				//如果创建了新目录就删除
				if(localCacheParentDir != null && localCacheParentDir.exists()){
					localCacheParentDir.delete();
				}
				
				//如果是请求超时异常，就尝试再请求一次
				if((e2 instanceof ConnectTimeoutException || e2 instanceof SocketTimeoutException  || e2 instanceof  ConnectionPoolTimeoutException) && maxRetryCount > 0){
					running = numberOfLoaded < maxRetryCount;	//如果尚未达到最大重试次数，那么就再尝试一次
				}else{
					running = false;
				}
			}
		}
		
		switch(result){
			case FILE : 
				if(onCompleteListener != null){
					if(cacheFile != null && cacheFile.exists() && cacheFile.length() > 0){
						onCompleteListener.onComplete(cacheFile);
					}else{
						onCompleteListener.onFailed();
					}
				}
				break;
			case BYTE_ARRAY : 
				if(onCompleteListener != null){
					if(data != null && data.length > 0){
						onCompleteListener.onComplete(data);
					}else{
						onCompleteListener.onFailed();
					}
				}
				break;
			default : 
				if(onCompleteListener != null){
					onCompleteListener.onFailed();
				}
				break;
		}
	}
	
	private enum Result{
		FILE, BYTE_ARRAY, FAILURE;
	}
	
	public interface OnCompleteListener{
		public void onComplete(File cacheFile);
		public void onComplete(byte[] data);
		public void onFailed();
	}
}