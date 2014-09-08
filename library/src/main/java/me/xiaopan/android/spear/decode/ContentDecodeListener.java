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
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.LoadRequest;

public class ContentDecodeListener implements ImageDecoder.DecodeListener {
    private static final String NAME = ContentDecodeListener.class.getSimpleName();
	private String contentUri;
	private LoadRequest loadRequest;
	
	public ContentDecodeListener(String contentUri, LoadRequest loadRequest) {
		this.contentUri = contentUri;
		this.loadRequest = loadRequest;
	}

    @Override
    public Bitmap onDecode(BitmapFactory.Options options) {
        InputStream inputStream = null;
        try {
            inputStream = loadRequest.getSpear().getContext().getContentResolver().openInputStream(Uri.parse(contentUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        if(inputStream != null){
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        StringBuilder stringBuffer = new StringBuilder(NAME)
        .append("；").append("解码成功");
        if(bitmap != null && loadRequest.getMaxsize() != null){
            stringBuffer.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
            stringBuffer.append("；").append("目标尺寸").append("=").append(loadRequest.getMaxsize().getWidth()).append("x").append(loadRequest.getMaxsize().getHeight());
            stringBuffer.append("；").append("缩放比例").append("=").append(inSampleSize);
            stringBuffer.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
        }else{
        	stringBuffer.append("；").append("未缩放");
        }
        stringBuffer.append("；").append(loadRequest.getName());
        Log.d(Spear.LOG_TAG, stringBuffer.toString());
    }

    @Override
    public void onDecodeFailure() {
        if(loadRequest.getSpear().isDebugMode()){
        	Log.e(Spear.LOG_TAG, NAME + "；" + "解码失败" + "；" + contentUri);
        }
    }
}
