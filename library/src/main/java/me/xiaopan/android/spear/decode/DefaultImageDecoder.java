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

package me.xiaopan.android.spear.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * 默认的位图解码器
 */
public class DefaultImageDecoder implements ImageDecoder {

    @Override
	public Bitmap decode(Spear spear, ImageSize maxsize, DecodeListener decodeListener){
		Bitmap bitmap = null;
		Point originalSize = null;
        int inSampleSize = 1;

        if(maxsize != null){
		    // 只解码宽高
            Options options = new Options();
            options.inJustDecodeBounds = true;
            decodeListener.onDecode(options);
            if(!(options.outWidth == 1 && options.outHeight == 1)){
                originalSize = new Point(options.outWidth, options.outHeight);

                // 计算缩放倍数
                inSampleSize = spear.getConfiguration().getImageSizeCalculator().calculateInSampleSize(options.outWidth, options.outHeight, maxsize.getWidth(), maxsize.getHeight());
                options.inSampleSize = inSampleSize;

                // 再次解码
                options.inJustDecodeBounds = false;
                bitmap = decodeListener.onDecode(options);
            }
        }else{
            bitmap = decodeListener.onDecode(null);
            if(bitmap != null){
                if(!(bitmap.getWidth()==1 && bitmap.getHeight() == 1)){
                    originalSize = new Point(bitmap.getWidth(), bitmap.getHeight());
                }else{
                    if(!bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                }
            }
        }

        // 回调
    	if(bitmap != null && originalSize != null){
    		if(!bitmap.isRecycled()){
    			decodeListener.onDecodeSuccess(bitmap, originalSize, inSampleSize);
    		}else{
    			bitmap = null;
    			decodeListener.onDecodeFailure();
    		}
    	}else{
    		decodeListener.onDecodeFailure();
    	}

		return bitmap;
	}
}