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

import android.graphics.Bitmap;
import me.xiaopan.android.imageloader.decode.BitmapDecoder;

public class BitmapLoadCallable implements Callable<Object> {
	protected LoadRequest loadRequest;
    private BitmapDecoder.InputStreamCreator inputStreamCreator;
	
	public BitmapLoadCallable(LoadRequest displayRequest, BitmapDecoder.InputStreamCreator inputStreamCreator) {
		this.loadRequest = displayRequest;
        this.inputStreamCreator = inputStreamCreator;
	}

	@Override
	public Object call() throws Exception {
		Bitmap bitmap = null;
		try{
			//解码
			if(inputStreamCreator != null){
				bitmap = loadRequest.getConfiguration().getBitmapDecoder().decode(loadRequest, inputStreamCreator);
			}

			//处理位图
			if(bitmap != null && !bitmap.isRecycled()){
				if(loadRequest.getLoadOptions().getBitmapProcessor() != null){
					Bitmap newBitmap = loadRequest.getLoadOptions().getBitmapProcessor().process(bitmap, loadRequest.getScaleType(), loadRequest.getMaxImageSize());
					if(newBitmap != bitmap){
						bitmap.recycle();
						bitmap = newBitmap;
					}
				}
			}else{
                bitmap = null;
            }
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return bitmap;
	}
}
