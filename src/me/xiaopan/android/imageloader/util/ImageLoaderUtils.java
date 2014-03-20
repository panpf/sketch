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

package me.xiaopan.android.imageloader.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.xiaopan.android.imageloader.process.BitmapProcessor;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

public class ImageLoaderUtils {
	
	public static final boolean createFile(File file){
		//如果缓存文件不存在就尝试创建
		if(!file.exists()){
			File parentDir = file.getParentFile();
			if(!parentDir.exists()){
				parentDir.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file.exists();
	}
	
	/**
	 * 设置连接超时
	 * @param client
	 * @param connectionTimeout
	 * @return
	 */
	public static boolean setConnectionTimeout(HttpClient client, int connectionTimeout){
		if(client != null){
			HttpParams httpParams = client.getParams();
			ConnManagerParams.setTimeout(httpParams, connectionTimeout);
			HttpConnectionParams.setSoTimeout(httpParams, connectionTimeout);
			HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 设置连接超时
	 * @param httpParams
	 * @param connectionTimeout
	 * @return
	 */
	public static boolean setConnectionTimeout(HttpParams httpParams, int connectionTimeout){
		if(httpParams != null && connectionTimeout > 0){
			ConnManagerParams.setTimeout(httpParams, connectionTimeout);
			HttpConnectionParams.setSoTimeout(httpParams, connectionTimeout);
			HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 设置最大连接数
	 * @param client
	 * @param maxConnections
	 * @return
	 */
	public static boolean setMaxConnections(HttpClient client, int maxConnections){
		if(client != null){
			HttpParams httpParams = client.getParams();
			ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
			ConnManagerParams.setMaxTotalConnections(httpParams, maxConnections);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 *  设置最大连接数
	 * @param httpParams
	 * @param maxConnections
	 * @return
	 */
	public static boolean setMaxConnections(HttpParams httpParams, int maxConnections){
		if(httpParams != null && maxConnections > 0){
			ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
			ConnManagerParams.setMaxTotalConnections(httpParams, maxConnections);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 设置Socket缓存大小
	 * @param client
	 * @param socketBufferSize
	 * @return
	 */
	public static boolean setSocketBufferSize(HttpClient client, int socketBufferSize){
		if(client != null){
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setSocketBufferSize(httpParams, socketBufferSize);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 设置Socket缓存大小
	 * @param httpParams
	 * @param socketBufferSize
	 * @return
	 */
	public static boolean setSocketBufferSize(HttpParams httpParams, int socketBufferSize){
		if(httpParams != null && socketBufferSize > 0){
			HttpConnectionParams.setSocketBufferSize(httpParams, socketBufferSize);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断给定的字符串是否为null或者是空的
	 * @param string 给定的字符串
	 * @return 
	 */
	public static boolean isEmpty(String string){
		return string == null || "".equals(string.trim());
	}
	
	/**
	 * 判断给定的字符串是否不为null且不为空
	 * @param string 给定的字符串
	 * @return 
	 */
	public static boolean isNotEmpty(String string){
		return !isEmpty(string);
	}
	
	/**
	 * 获取SD卡的状态
	 * @return 
	 */
	public static String getState(){
		return Environment.getExternalStorageState();
	}
	
	/**
	 * SD卡是否可用
	 * @return 只有当SD卡已经安装并且准备好了才返回true
	 */
	public static boolean isAvailable(){
		return getState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 获取动态获取缓存目录
	 * @param context 上下文
	 * @return 如果SD卡可用，就返回外部缓存目录，否则返回机身自带缓存目录
	 */
	public static File getDynamicCacheDir(Context context){
		if(isAvailable()){
			File dir = context.getExternalCacheDir();
			if(dir == null){
				dir = context.getCacheDir();
			}
			return dir;
		}else{
			return context.getCacheDir();
		}
	}

	/**
	 * 获取屏幕尺寸
	 * @param context
	 * @return 返回一个长度为2的int数组，int[0]是宽；int[1]是高
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static int[] getScreenSize(Context context){
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
			return new int[]{display.getWidth(), display.getHeight()};
		}else{
			Point point = new Point();
			display.getSize(point);
			return new int[]{point.x, point.y};
		}
	}
	
	/**
	 * 编码URL
	 * @param url
	 * @return
	 */
	public static String encodeUrl(String url){
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return url;
		}
	}
	
	/**
	 * 创建ID
	 * @param uri
	 * @param targetSize
	 * @param bitmapProcessor
	 * @return
	 */
	public static String createId(String uri, ImageSize targetSize, BitmapProcessor bitmapProcessor){
		StringBuffer stringBuffer = new StringBuffer(uri);
		if(targetSize != null){
			stringBuffer.append("_")
			.append(targetSize.getWidth())
			.append("x")
			.append(targetSize.getHeight());
		}
		if(bitmapProcessor != null){
			String tag = bitmapProcessor.getTag();
			if(tag != null){
				stringBuffer.append("_");
				stringBuffer.append(tag);
			}
		}
		return stringBuffer.toString();
	}
	
    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    
    public static Bitmap bitmapCopy(Bitmap bitmap){
		return bitmap.copy(bitmap.getConfig(), true);
    }

	public static final int BUFFER_SIZE = 8 * 1024; // 8 KB 

	/**
	 * 关闭流
	 * @param closeable
	 */
	public static void close(Closeable closeable) {
		if(closeable != null){
			if(closeable instanceof OutputStream){
				try {
					((OutputStream) closeable).flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除给定的文件，如果当前文件是目录则会删除其包含的所有的文件或目录
	 * @param file 给定的文件
	 * @return 删除是否成功
	 */
	public static void delete(File file){
		if(file != null && file.exists()){
			if(file.isFile()){
				file.delete();
			}else{
				File[] files = file.listFiles();
				if(files != null){
					for(File tempFile : files){
						delete(tempFile);
					}
				}
				file.delete();
			}
		}
	}
}
