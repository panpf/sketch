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
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.LoadRequest;

public class DrawableDecodeListener implements ImageDecoder.DecodeListener {
    private static final String NAME = DrawableDecodeListener.class.getSimpleName();
	private String drawableIdString;
    private LoadRequest loadRequest;
	
	public DrawableDecodeListener(String drawableIdString, LoadRequest loadRequest) {
		this.drawableIdString = drawableIdString;
        this.loadRequest = loadRequest;
	}

    @Override
    public Bitmap onDecode(BitmapFactory.Options options) {
        return BitmapFactory.decodeResource(loadRequest.getSpear().getContext().getResources(), Integer.valueOf(drawableIdString), options);
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        StringBuffer stringBuffer = new StringBuffer(NAME)
        .append("；"+"解码成功");
        if(bitmap != null && loadRequest.getMaxsize() != null){
            stringBuffer.append("；"+"原始尺寸"+"="+originalSize.x+"x"+originalSize.y);
            stringBuffer.append("；"+"目标尺寸"+"="+loadRequest.getMaxsize().getWidth()+"x"+loadRequest.getMaxsize().getHeight());
            stringBuffer.append("；"+"缩放比例"+"="+inSampleSize);
            stringBuffer.append("；"+"最终尺寸"+"="+bitmap.getWidth()+"x"+bitmap.getHeight());
        }else{
        	stringBuffer.append("；"+"未缩放");
        }
        stringBuffer.append("；"+loadRequest.getName());
        Log.d(Spear.LOG_TAG, stringBuffer.toString());
    }

    @Override
    public void onDecodeFailure() {
        if(loadRequest.getSpear().isDebugMode()){
        	Log.e(Spear.LOG_TAG, NAME + "；" + "解码失败" + "；" + drawableIdString);
        }
    }
}
