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
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

class GeneralUtils {
	
	/**
	 * 设置连接超时
	 * @param client
	 * @param connectionTimeout
	 * @return
	 */
	static boolean setConnectionTimeout(HttpClient client, int connectionTimeout){
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
	static boolean setConnectionTimeout(HttpParams httpParams, int connectionTimeout){
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
	static boolean setMaxConnections(HttpClient client, int maxConnections){
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
	static boolean setMaxConnections(HttpParams httpParams, int maxConnections){
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
	static boolean setSocketBufferSize(HttpClient client, int socketBufferSize){
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
	static boolean setSocketBufferSize(HttpParams httpParams, int socketBufferSize){
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
	static boolean isEmpty(String string){
		return string == null || "".equals(string.trim());
	}
	
	/**
	 * 判断给定的字符串是否不为null且不为空
	 * @param string 给定的字符串
	 * @return 
	 */
	static boolean isNotEmpty(String string){
		return !isEmpty(string);
	}
	
	/**
	 * 获取SD卡的状态
	 * @return 
	 */
	static String getState(){
		return Environment.getExternalStorageState();
	}
	
	/**
	 * SD卡是否可用
	 * @return 只有当SD卡已经安装并且准备好了才返回true
	 */
	static boolean isAvailable(){
		return getState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 获取动态获取缓存目录
	 * @param context 上下文
	 * @return 如果SD卡可用，就返回外部缓存目录，否则返回机身自带缓存目录
	 */
	static File getDynamicCacheDir(Context context){
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
	static int[] getScreenSize(Context context){
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
	 * 处理圆角图片
	 * @param bitmap
	 * @param roundPixels
	 * @param srcRect
	 * @param destRect
	 * @param width
	 * @param height
	 * @return
	 */
	static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixels, Rect srcRect, Rect destRect, int width, int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final RectF destRectF = new RectF(destRect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFF000000);
		canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

		return output;
	}
	
	/**
	 * Process incoming {@linkplain Bitmap} to make rounded corners according to target {@link ImageView}.<br />
	 * This method <b>doesn't display</b> result bitmap in {@link ImageView}
	 * 
	 * @param bitmap Incoming Bitmap to process
	 * @param imageView Target {@link ImageView} to display bitmap in
	 * @param roundPixels
	 * @return Result bitmap with rounded corners
	 */
	static Bitmap roundCorners(Bitmap bitmap, ImageView imageView, int roundPixels) {
		Bitmap roundBitmap;

		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();
		int vw = imageView.getWidth();
		int vh = imageView.getHeight();
		if (vw <= 0) vw = bw;
		if (vh <= 0) vh = bh;

		int width, height;
		Rect srcRect;
		Rect destRect;
		switch (imageView.getScaleType()) {
			case CENTER_INSIDE:
				float vRation = (float) vw / vh;
				float bRation = (float) bw / bh;
				int destWidth;
				int destHeight;
				if (vRation > bRation) {
					destHeight = Math.min(vh, bh);
					destWidth = (int) (bw / ((float) bh / destHeight));
				} else {
					destWidth = Math.min(vw, bw);
					destHeight = (int) (bh / ((float) bw / destWidth));
				}
				int x = (vw - destWidth) / 2;
				int y = (vh - destHeight) / 2;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(x, y, x + destWidth, y + destHeight);
				width = vw;
				height = vh;
				break;
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			default:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				if (vRation > bRation) {
					width = (int) (bw / ((float) bh / vh));
					height = vh;
				} else {
					width = vw;
					height = (int) (bh / ((float) bw / vw));
				}
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER_CROP:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				int srcWidth;
				int srcHeight;
				if (vRation > bRation) {
					srcWidth = bw;
					srcHeight = (int) (vh * ((float) bw / vw));
					x = 0;
					y = (bh - srcHeight) / 2;
				} else {
					srcWidth = (int) (vw * ((float) bh / vh));
					srcHeight = bh;
					x = (bw - srcWidth) / 2;
					y = 0;
				}
				width = Math.min(vw, bw);
				height = Math.min(vh, bh);
				srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
				destRect = new Rect(0, 0, width, height);
				break;
			case FIT_XY:
				width = vw;
				height = vh;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER:
			case MATRIX:
				width = Math.min(vw, bw);
				height = Math.min(vh, bh);
				x = (bw - width) / 2;
				y = (bh - height) / 2;
				srcRect = new Rect(x, y, x + width, y + height);
				destRect = new Rect(0, 0, width, height);
				break;
		}

		try {
			roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect, destRect, width, height);
		} catch (OutOfMemoryError e) {
			roundBitmap = bitmap;
		}

		return roundBitmap;
	}
	
	static BitmapLoader getBitmapLoader(Options options){
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
	static boolean isAvailableOfFile(File file, int periodOfValidity, ImageLoader imageLoader, String requestName){
		boolean available = false;
		if(file !=null){
			if(file.exists()){
				if(file.length() > 0){
					if(periodOfValidity > 0){
						/* 判断是否过期 */
						Calendar calendar = new GregorianCalendar();
						calendar.add(Calendar.MILLISECOND, -periodOfValidity);
						if(calendar.getTimeInMillis() >= file.lastModified()){
							file.delete();
							imageLoader.getConfiguration().log("文件已过期："+requestName);
						}else{
							available = true;
							imageLoader.getConfiguration().log("文件未过期："+requestName);
						}
					}else{
						available = true;
						imageLoader.getConfiguration().log("文件永久有效："+requestName);
					}
				}else{
					imageLoader.getConfiguration().log("文件长度为0："+requestName, true);
				}
			}else{
				imageLoader.getConfiguration().log("文件不存在："+requestName, true);
			}
		}else{
			imageLoader.getConfiguration().log("文件为null："+requestName, true);
		}
		return available;
	}
}
