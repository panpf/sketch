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

package me.xiaoapn.easy.imageloader.decode;

import java.io.InputStream;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

/**
 * 位图解码器
 */
public class SimpleBitmapDecoder implements BitmapDecoder{
	private String logName;
	
	/**
	 * 创建位图解码器
	 */
	public SimpleBitmapDecoder(){
		this.logName = getClass().getSimpleName(); 
	}
	
	@Override
	public Bitmap decode(InputStream inputStream, ImageSize maxSize, ImageLoader imageLoader, String requestName) {
		Options options = new Options();
		
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, null, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		
		options.inSampleSize = calculateInSampleSize(options, maxSize.getWidth(), maxSize.getHeight());
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		
		if(imageLoader.getConfiguration().isDebugMode()){
			writeLog(imageLoader, requestName, bitmap != null, outWidth, outHeight, options.inSampleSize, options.outWidth, options.outHeight);
		}
		
		return bitmap;
	}
	
	/**
	 * 输出LOG
	 * @param imageLoader
	 * @param requestName
	 * @param success
	 * @param outWidth
	 * @param outHeight
	 * @param inSimpleSize
	 * @param finalWidth
	 * @param finalHeight
	 */
	private void writeLog(ImageLoader imageLoader, String requestName, boolean success, int outWidth, int outHeight, int inSimpleSize, int finalWidth, int finalHeight){
		String log = new StringBuffer(logName)
		.append("：").append(success?"解码成功":"解码失败")
		.append("：").append("原图尺寸").append("=").append(outWidth).append("x").append(outHeight)
		.append("；").append("缩小").append("=").append(inSimpleSize)
		.append("；").append("最终尺寸").append("=").append(finalWidth).append("x").append(finalHeight)
		.append("；").append(requestName)
		.toString();
		if(success){
			Log.d(imageLoader.getConfiguration().getLogTag(), log);
		}else{
			Log.w(imageLoader.getConfiguration().getLogTag(), log);
		}
	}
	
	/**
	 * 计算样本尺寸
	 * @param options
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
		int inSampleSize = 1;

		if (options.outWidth > maxWidth || options.outHeight > maxHeight) {
			do{
				inSampleSize *= 2;
			}while((options.outWidth / inSampleSize) > maxWidth || (options.outHeight / inSampleSize) > maxHeight);
		}
		
		return inSampleSize;
	}
}