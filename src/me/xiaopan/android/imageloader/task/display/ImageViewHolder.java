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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.widget.ImageView;

/**
 * ImageView持有器，以弱引用的方式持有关联的ImageView
 */
public class ImageViewHolder{
	protected Reference<ImageView> imageViewReference;
	protected boolean checkRealViewSize;
	private DisplayRequest displayRequest;

	public ImageViewHolder(ImageView imageView) {
		this(imageView, true);
	}

	public ImageViewHolder(ImageView imageView, boolean checkRealViewSize) {
		this.imageViewReference = new WeakReference<ImageView>(imageView);
		this.checkRealViewSize = checkRealViewSize;
	}

	public ImageView getImageView() {
		final ImageView imageView = imageViewReference.get();
		if (displayRequest != null) {
			DisplayRequest holderDisplayRequest = AsyncDrawable.getDisplayRequestByAsyncDrawable(imageView);
            if(holderDisplayRequest != null && holderDisplayRequest == displayRequest){
            	return imageView;
            }else{
            	return null;
            }
        }else{
        	return imageView;
        }
	}

	public boolean isCollected() {
		return getImageView() == null;
	}
	
	public void setDisplayRequest(DisplayRequest displayRequest) {
		this.displayRequest = displayRequest;
	}
}
