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

package me.xiaopan.android.spear.execute;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;

import me.xiaopan.android.spear.decode.ImageDecoder;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.request.Request;

public class LoadTask implements Runnable {
	private LoadRequest loadRequest;
    private LoadListener.From from;
    private ImageDecoder.DecodeListener onDecodeListener;
	
	public LoadTask(LoadRequest loadRequest, ImageDecoder.DecodeListener onDecodeListener, LoadListener.From from) {
		this.loadRequest = loadRequest;
		this.onDecodeListener = onDecodeListener;
        this.from = from;
	}
	
	@Override
	public void run() {
        if(loadRequest.isCanceled()){
            if(loadRequest.getLoadListener() != null){
				loadRequest.getLoadListener().onCanceled();
			}
            return;
		}

        loadRequest.setStatus(Request.Status.LOADING);

        // 解码
        Bitmap bitmap = null;
        if(loadRequest.getUri().endsWith(".apk")){
            PackageManager packageManager = loadRequest.getSpear().getConfiguration().getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(loadRequest.getUri(), PackageManager.GET_CONFIGURATIONS);
            if(packageInfo != null){
                packageInfo.applicationInfo.sourceDir = loadRequest.getUri();
                packageInfo.applicationInfo.publicSourceDir = loadRequest.getUri();
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                if(drawable != null && drawable instanceof BitmapDrawable){
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                }
            }
        }else{
            try{
                bitmap = loadRequest.getSpear().getConfiguration().getImageDecoder().decode(loadRequest.getSpear(), loadRequest.getMaxsize(), onDecodeListener);
            }catch(IOException e1){
                e1.printStackTrace();
            }
        }

        //处理
        if(bitmap != null){
            ImageProcessor imageProcessor = loadRequest.getImageProcessor();
            if(imageProcessor == null && loadRequest.getResize() != null){
                imageProcessor = loadRequest.getSpear().getConfiguration().getDefaultCutImageProcessor();
            }
            if(imageProcessor != null){
                Bitmap newBitmap = imageProcessor.process(bitmap, loadRequest.getResize(), loadRequest.getScaleType());
                if(newBitmap != bitmap){
                    bitmap.recycle();
                    bitmap = newBitmap;
                }
            }
        }

        if(loadRequest.isCanceled()){
            if(loadRequest.getLoadListener() != null){
				loadRequest.getLoadListener().onCanceled();
			}
            return;
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
}