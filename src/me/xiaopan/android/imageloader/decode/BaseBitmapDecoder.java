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

package me.xiaopan.android.imageloader.decode;

import java.io.InputStream;

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.util.LoadIOUtils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build;
import android.util.Log;

/**
 * 位图解码器
 */
public class BaseBitmapDecoder implements BitmapDecoder{
	private static final String NAME= BaseBitmapDecoder.class.getSimpleName();
	
	@Override
	public Bitmap decode(DisplayRequest displayRequest,  InputStreamCreator onNewBitmapInputStreamListener) {
		Bitmap bitmap = null;
		Options options = new Options();
		int outWidth = 0;
		int outHeight = 0;
		
		InputStream inputStream = onNewBitmapInputStreamListener.onCreateInputStream();
		if(inputStream != null){
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(inputStream, null, options);
			LoadIOUtils.close(inputStream);
			
			inputStream = onNewBitmapInputStreamListener.onCreateInputStream();
			if(inputStream != null){
				outWidth = options.outWidth;
				outHeight = options.outHeight;
				options.inSampleSize = calculateInSampleSize(options, displayRequest.getTargetSize().getWidth(), displayRequest.getTargetSize().getHeight());
				options.inJustDecodeBounds = false;
//				if (ImageLoaderUtils.hasHoneycomb()) {
//			        addInBitmapOptions(displayRequest, options);
//			    }
				bitmap = BitmapFactory.decodeStream(inputStream, null, options);
				LoadIOUtils.close(inputStream);
			}
		}
		
		if(displayRequest.getConfiguration().isDebugMode()){
			writeLog(displayRequest, bitmap != null, outWidth, outHeight, options.inSampleSize, bitmap);
		}
		
		return bitmap;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addInBitmapOptions(DisplayRequest displayRequest, BitmapFactory.Options options) {
	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
	    	// inBitmap only works with mutable bitmaps, so force the decoder to
	    	// return mutable bitmaps.
	    	options.inMutable = true;
	    	
	    	if (displayRequest.getConfiguration().getBitmapCacher() != null) {
	    		// Try to find a bitmap to use for inBitmap.
	    		Bitmap inBitmap = displayRequest.getConfiguration().getBitmapCacher().getBitmapFromReusableSet(options);
	    		if (inBitmap != null && !inBitmap.isRecycled()) {
	    			// If a suitable bitmap has been found, set it as the value of
	    			// inBitmap.
	    			options.inBitmap = inBitmap;
	    			if(displayRequest.getConfiguration().isDebugMode()){
	    				Log.w(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("回收利用了尚未被回收的Bitmap").toString());
	    			}
	    		}
	    	}
	    }
	}
	
	/**
	 * 输出LOG
	 * @param displayRequest
	 * @param success
	 * @param outWidth
	 * @param outHeight
	 * @param inSimpleSize
	 * @param bitmap
	 */
	private void writeLog(DisplayRequest displayRequest, boolean success, int outWidth, int outHeight, int inSimpleSize, Bitmap bitmap){
		StringBuffer stringBuffer = new StringBuffer(NAME)
		.append("：").append(success?"解码成功":"解码失败")
		.append("；").append("原图尺寸").append("=").append(outWidth).append("x").append(outHeight)
		.append("；").append("目标尺寸").append("=").append(displayRequest.getTargetSize().getWidth()).append("x").append(displayRequest.getTargetSize().getHeight())
		.append("；").append("缩小").append("=").append(inSimpleSize);
		if(bitmap != null){
			stringBuffer.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
		}
		stringBuffer.append("；").append(displayRequest.getName());
		String log = stringBuffer.toString();
		if(success){
			Log.d(ImageLoader.LOG_TAG, log);
		}else{
			Log.w(ImageLoader.LOG_TAG, log);
		}
	}
	
	/**
	 * 计算样本尺寸
	 * @param options
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight) {
		int inSampleSize = 1;

		final int height = options.outHeight;
	    final int width = options.outWidth;
	    if (height > targetHeight || width > targetWidth) {
	        do{
	            inSampleSize *= 2;
	        }while ((height/inSampleSize) > targetHeight && (width/inSampleSize) > targetWidth); 
	    }

	    return inSampleSize;
	}
}