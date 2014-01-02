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

package me.xiaoapn.easy.imageloader.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.Options;
import me.xiaoapn.easy.imageloader.decode.BitmapLoader;
import me.xiaoapn.easy.imageloader.decode.PixelsBitmapLoader;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class GeneralUtils {
	
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
	
	public static BitmapLoader getBitmapLoader(Options options){
		BitmapLoader bitmapLoader = null;
		if(options != null && options.getBitmapLoader() != null){
			bitmapLoader = options.getBitmapLoader();
		}else{
			bitmapLoader = new PixelsBitmapLoader();
		}
		return bitmapLoader;
	}
	
	/**
	 * 判断给定文件是否可以使用
	 * @param file
	 * @param periodOfValidity
	 * @param imageLoader
	 * @param requestName
	 * @return
	 */
	public static boolean isAvailableOfFile(File file, int periodOfValidity, ImageLoader imageLoader, String requestName){
		if(file ==null){
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag(), "文件为null："+requestName);
			}
			return false;
		}
		
		if(!file.exists()){
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag(), "文件不存在："+requestName);
			}
			return false;
		}
		
		if(file.length() <= 0){
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag(), "文件长度为0："+requestName);
			}
			return false;
		}
		
		if(periodOfValidity <= 0){
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.d(imageLoader.getConfiguration().getLogTag(), "文件永久有效："+requestName);
			}
			return true;
		}
		
		/* 判断是否过期 */
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MILLISECOND, -periodOfValidity);
		if(calendar.getTimeInMillis() >= file.lastModified()){
			file.delete();
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag(), "文件过期已删除："+requestName);
			}
			return false;
		}else{
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.d(imageLoader.getConfiguration().getLogTag(), "文件未过期："+requestName);
			}
			return true;
		}
	}
	
	public static String encodeUrl(String url){
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return url;
		}
	}
}
