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

package me.xiaoapn.easy.imageloader.execute;

import me.xiaoapn.easy.imageloader.Options;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class DisplayBitmapTask implements Runnable {
	private ImageView imageView;
	private Bitmap bitmap;
	private Options options;
	private boolean isFromMemoryCache;

	public DisplayBitmapTask(ImageView imageView, Bitmap bitmap, Options options, boolean isFromMemoryCache) {
		this.imageView = imageView;
		this.bitmap = bitmap;
		this.options = options;
		this.isFromMemoryCache = isFromMemoryCache;
	}

	@Override
	public void run() {
		if(bitmap != null && !bitmap.isRecycled()){
			options.getBitmapDisplayer().display(imageView, bitmap, isFromMemoryCache);
		}else{
			imageView.setImageBitmap(options.getLoadFailureBitmap());
		}
	}
}
