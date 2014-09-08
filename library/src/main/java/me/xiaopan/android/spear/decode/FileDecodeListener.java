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
            if(loadRequest.getSpear().isDebugMode()){
                Log.e(Spear.LOG_TAG, NAME+"；"+"不可读取"+"；"+file.getPath());
            }
            return null;
        }
    }

    @Override
    public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
        StringBuilder stringBuffer = new StringBuilder(NAME)
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
        	StringBuffer stringBuffer = new StringBuffer(NAME)
        	.append("；"+"解码失败")
        	.append("；"+"文件地址"+"="+file.getPath())
        	.append("；"+"文件长度"+"="+file.length());
        	Log.e(Spear.LOG_TAG, stringBuffer.toString());
        }
    }
}
