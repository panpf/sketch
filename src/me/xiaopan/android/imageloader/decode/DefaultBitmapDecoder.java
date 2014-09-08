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

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.load.LoadRequest;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;

/**
 * 默认的位图解码器
 */
public class DefaultBitmapDecoder implements BitmapDecoder{
	private static final String NAME= DefaultBitmapDecoder.class.getSimpleName();
	
	@Override
	public Bitmap decode(LoadRequest loadRequest, DecodeListener decodeListener){
		Bitmap bitmap = null;
		Point originalSize = null;
		Options options = new Options();
		
		// 解码其宽高
        options.inJustDecodeBounds = true;
        decodeListener.onDecode(options);
        if(options.outWidth > 0 && options.outHeight > 0){
        	originalSize = new Point(options.outWidth, options.outHeight);
        	if(loadRequest.getDecodeMaxSize() != null){
        		options.inSampleSize = calculateInSampleSize(options, loadRequest.getDecodeMaxSize().getWidth(), loadRequest.getDecodeMaxSize().getHeight());
        	}
        	
        	//解码
        	options.inJustDecodeBounds = false;
        	if (ImageLoaderUtils.hasHoneycomb()) {
        		addInBitmapOptions(loadRequest, options);
        	}
        	bitmap = decodeListener.onDecode(options);
        }
        
        // 回调
    	if(bitmap != null){
    		if(!bitmap.isRecycled()){
    			decodeListener.onDecodeSuccess(bitmap, originalSize, options.inSampleSize);
    		}else{
    			bitmap = null;
    			decodeListener.onDecodeFailure();
    		}
    	}else{
    		decodeListener.onDecodeFailure();
    	}

		return bitmap;
	}

    /**
     * 尝试重复利用Bitmap
     * @param loadRequest 加载请求
     * @param options 解码选项
     */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addInBitmapOptions(LoadRequest loadRequest, BitmapFactory.Options options) {
	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            // Try to find a bitmap to use for inBitmap.
            Bitmap inBitmap = loadRequest.getConfiguration().getMemoryCache().getBitmapFromReusableSet(options);
            if (inBitmap != null && !inBitmap.isRecycled()) {
	    	    options.inMutable = true;// inBitmap only works with mutable bitmaps, so force the decoder to
                options.inBitmap = inBitmap;// return mutable bitmaps.
                if(loadRequest.getConfiguration().isDebugMode()){
                    Log.w(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("回收利用了尚未被回收的Bitmap").toString());
                }
            }
	    }
	}

	/**
	 * 计算样本尺寸
	 * @param options 解码选项
	 * @param targetWidth 目标宽
	 * @param targetHeight 目标高
	 * @return 缩放比例
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