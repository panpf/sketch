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

package me.xiaopan.android.imageloader.task.display;

import me.xiaopan.android.imageloader.display.BitmapDisplayer.BitmapType;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class DisplayRunnable implements Runnable {
	private DisplayRequest displayRequest;
	private BitmapType bitmapType;
	private BitmapDrawable bitmapDrawable;

	public DisplayRunnable(DisplayRequest displayRequest, BitmapDrawable bitmapDrawable, BitmapType bitmapType) {
		this.displayRequest = displayRequest;
		this.bitmapDrawable = bitmapDrawable;
		this.bitmapType = bitmapType;
	}

	@Override
	public void run() {
		ImageView imageView = displayRequest.getImageViewHolder().getImageView();
		if(imageView != null){
			displayRequest.getDisplayOptions().getDisplayer().display(imageView, bitmapDrawable, bitmapType, displayRequest);
			if(displayRequest.getDisplayListener() != null){
				if(bitmapType == BitmapType.SUCCESS){
					displayRequest.getDisplayListener().onSuccess(displayRequest.getImageUri(), imageView, bitmapDrawable);
				}else{ 
					displayRequest.getDisplayListener().onFailure();
				}
			}
		}
	}
}
