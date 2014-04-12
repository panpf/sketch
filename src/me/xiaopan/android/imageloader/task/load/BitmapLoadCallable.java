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

package me.xiaopan.android.imageloader.task.load;

import java.util.concurrent.Callable;

import me.xiaopan.android.imageloader.decode.BitmapDecoder;
import android.graphics.Bitmap;

public class BitmapLoadCallable implements Callable<Object> {
	protected LoadRequest loadRequest;
    private BitmapDecoder.DecodeListener onDecodeListener;
	
	public BitmapLoadCallable(LoadRequest displayRequest, BitmapDecoder.DecodeListener onDecodeListener) {
		this.loadRequest = displayRequest;
        this.onDecodeListener = onDecodeListener;
	}

	@Override
	public Object call() throws Exception {
		Bitmap bitmap = null;
		try{
            //解码
            bitmap = loadRequest.getConfiguration().getBitmapDecoder().decode(loadRequest, onDecodeListener);

            //处理位图
            if(bitmap != null){
                onDecodeListener.onDecodeSuccess();
                if(loadRequest.getLoadOptions().getProcessor() != null){
                    Bitmap newBitmap = loadRequest.getLoadOptions().getProcessor().process(bitmap, loadRequest.getScaleType(), loadRequest.getMaxSize());
                    if(newBitmap != bitmap){
                        bitmap.recycle();
                        bitmap = newBitmap;
                    }
                }
            }else{
                onDecodeListener.onDecodeFailure();
                bitmap = null;
            }
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return bitmap;
	}
}
