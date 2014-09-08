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

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.decode.BitmapDecoder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

public class DrawableDecodeListener implements BitmapDecoder.DecodeListener {
    private static final String NAME = DrawableDecodeListener.class.getSimpleName();
	private String drawableIdString;
    private LoadRequest loadRequest;
	
	public DrawableDecodeListener(String drawableIdString, LoadRequest loadRequest) {
		this.drawableIdString = drawableIdString;
        this.loadRequest = loadRequest;
	}

    @Override
    public Bitmap onDecode(BitmapFactory.Options options) {
        return BitmapFactory.decodeResource(loadRequest.getConfiguration().getContext().getResources(), Integer.valueOf(drawableIdString), options);
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        StringBuilder stringBuffer = new StringBuilder(NAME)
        .append("；").append("解码成功");
        if(bitmap != null && loadRequest.getDecodeMaxSize() != null){
            stringBuffer.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
            stringBuffer.append("；").append("目标尺寸").append("=").append(loadRequest.getDecodeMaxSize().getWidth()).append("x").append(loadRequest.getDecodeMaxSize().getHeight());
            stringBuffer.append("；").append("缩放比例").append("=").append(inSampleSize);
            stringBuffer.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
        }else{
        	stringBuffer.append("；").append("未缩放");
        }
        stringBuffer.append("；").append(loadRequest.getName());
        Log.d(ImageLoader.LOG_TAG, stringBuffer.toString());
    }

    @Override
    public void onDecodeFailure() {
        if(loadRequest.getConfiguration().isDebugMode()){
            StringBuilder stringBuilder = new StringBuilder(NAME)
            .append("；").append("解码失败")
            .append("；").append(drawableIdString);
        	Log.e(ImageLoader.LOG_TAG, stringBuilder.toString());
        }
    }
}
