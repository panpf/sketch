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
import me.xiaopan.android.imageloader.task.TaskRequest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DrawableDecodeListener implements BitmapDecoder.DecodeListener {
    private static final String NAME = DrawableDecodeListener.class.getSimpleName();
	private String drawableIdString;
    private TaskRequest taskRequest;
	
	public DrawableDecodeListener(String drawableIdString, TaskRequest taskRequest) {
		this.drawableIdString = drawableIdString;
        this.taskRequest = taskRequest;
	}

    @Override
    public Bitmap onDecode(BitmapFactory.Options options) {
        return BitmapFactory.decodeResource(taskRequest.getConfiguration().getContext().getResources(), Integer.valueOf(drawableIdString), options);
    }

    @Override
    public void onDecodeSuccess() {

    }

    @Override
    public void onDecodeFailure() {
        if(taskRequest.getConfiguration().isDebugMode()){
            Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("解码失败").append("：").append(drawableIdString).toString());
        }
    }
}
