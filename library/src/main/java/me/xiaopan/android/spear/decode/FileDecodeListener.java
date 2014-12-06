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

import java.io.File;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.LoadRequest;

public class FileDecodeListener implements ImageDecoder.DecodeListener {
    private static final String NAME = FileDecodeListener.class.getSimpleName();
	private File file;
    private LoadRequest loadRequest;
	
	public FileDecodeListener(File file, LoadRequest loadRequest) {
		this.file = file;
        this.loadRequest = loadRequest;
	}

    @Override
    public Bitmap onDecode(BitmapFactory.Options options) {
        if(file.canRead()){
            return BitmapFactory.decodeFile(file.getPath(), options);
        }else{
            if(Spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, NAME+"；"+"不可读取"+"；"+file.getPath());
            }
            return null;
        }
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        StringBuilder stringBuilder = new StringBuilder(NAME)
        .append("；"+"解码成功");
        if(bitmap != null && loadRequest.getMaxsize() != null){
            stringBuilder.append("；" + "原始尺寸" + "=" + originalSize.x + "x" + originalSize.y);
            stringBuilder.append("；" + "目标尺寸" + "=" + loadRequest.getMaxsize().getWidth() + "x" + loadRequest.getMaxsize().getHeight());
            stringBuilder.append("；" + "缩放比例" + "=" + inSampleSize);
            stringBuilder.append("；" + "最终尺寸" + "=" + bitmap.getWidth() + "x" + bitmap.getHeight());
        }else{
        	stringBuilder.append("；" + "未缩放");
        }
        stringBuilder.append("；" + loadRequest.getName());
        Log.d(Spear.LOG_TAG, stringBuilder.toString());
    }

    @Override
    public void onDecodeFailure() {
        if(Spear.isDebugMode()){
        	Log.e(Spear.LOG_TAG, new StringBuilder(NAME)
                    .append("；"+"解码失败")
                    .append("；"+"文件地址"+"="+file.getPath())
                    .append("；"+"文件长度"+"="+file.length()).toString());
        }
    }
}
