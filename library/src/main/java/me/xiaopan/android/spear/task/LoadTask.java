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

package me.xiaopan.android.spear.task;

import android.graphics.Bitmap;

import java.util.concurrent.Callable;

import me.xiaopan.android.spear.decode.ImageDecoder;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.request.Request;

public class LoadTask extends Task {
	private LoadRequest loadRequest;
    private LoadListener.From from;
	
	public LoadTask(LoadRequest loadRequest, ImageDecoder.DecodeListener onDecodeListener, LoadListener.From from) {
		super(loadRequest, new LoadCallable(loadRequest, onDecodeListener));
		this.loadRequest = loadRequest;
        this.from = from;
	}
	
	@Override
	protected void done() {
        if(loadRequest.isCanceled()){
			if(loadRequest.getLoadListener() != null){
				loadRequest.getLoadListener().onCanceled();
			}
            return;
		}

        Bitmap bitmap = null;
        try {
            bitmap = (Bitmap) get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bitmap != null && !bitmap.isRecycled()){
            if(!(loadRequest instanceof DisplayRequest)){
                loadRequest.setStatus(Request.Status.COMPLETED);
            }
            if(loadRequest.getLoadListener() != null){
                loadRequest.getLoadListener().onCompleted(bitmap, from);
            }
        }else{
            if(!(loadRequest instanceof DisplayRequest)){
                loadRequest.setStatus(Request.Status.FAILED);
            }
            if(loadRequest.getLoadListener() != null){
                loadRequest.getLoadListener().onFailed(null);
            }
        }
	}

    private static class LoadCallable implements Callable<Object> {
        protected LoadRequest loadRequest;
        private ImageDecoder.DecodeListener onDecodeListener;

        public LoadCallable(LoadRequest displayRequest, ImageDecoder.DecodeListener onDecodeListener) {
            this.loadRequest = displayRequest;
            this.onDecodeListener = onDecodeListener;
        }

        @Override
        public Object call() throws Exception {
            if(loadRequest.isCanceled()){
                return null;
            }

            loadRequest.setStatus(Request.Status.LOADING);
            Bitmap bitmap = null;
            try{
                //解码
                bitmap = loadRequest.getSpear().getImageDecoder().decode(loadRequest.getSpear(), loadRequest.getMaxsize(), onDecodeListener);

                //处理位图
                if(bitmap != null && loadRequest.getImageProcessor() != null){
                    Bitmap newBitmap = loadRequest.getImageProcessor().process(bitmap, loadRequest.getResize(), loadRequest.getScaleType());
                    if(newBitmap != bitmap){
                        bitmap.recycle();
                        bitmap = newBitmap;
                    }
                }
            }catch(Throwable throwable){
                throwable.printStackTrace();
            }
            return bitmap;
        }
    }
}
