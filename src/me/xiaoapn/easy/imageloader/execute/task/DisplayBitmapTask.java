/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaoapn.easy.imageloader.execute.task;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.Options;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class DisplayBitmapTask implements Runnable {
	private String name;
	private String requestName;
	private Bitmap bitmap;
	private Options options;
	private boolean isFromMemoryCache;
	private ImageView imageView;
	private ImageLoader imageLoader;

	public DisplayBitmapTask(ImageLoader imageLoader, ImageView imageView, Bitmap bitmap, Options options, String requestName, boolean isFromMemoryCache) {
		this.name = getClass().getSimpleName();
		this.bitmap = bitmap;
		this.options = options;
		this.imageView = imageView;
		this.requestName = requestName;
		this.imageLoader = imageLoader;
		this.isFromMemoryCache = isFromMemoryCache;
	}

	@Override
	public void run() {
		if(bitmap != null && !bitmap.isRecycled()){
			options.getBitmapDisplayer().display(imageView, bitmap, isFromMemoryCache);
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.i(imageLoader.getConfiguration().getLogTag(), new StringBuffer(name).append("：").append("显示成功").append("：").append(requestName).toString());
			}
		}else{
			imageView.setImageBitmap(options.getLoadFailureBitmap());
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag(), new StringBuffer(name).append("：").append("显示失败").append("：").append(requestName).toString());
			}
		}
	}
}
