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

package me.xiaoapn.easy.imageloader.task;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.display.BitmapDisplayer.BitmapType;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class BitmapDisplayRunnable implements Runnable {
	private Request request;
	private Configuration configuration;
	private BitmapDrawable bitmapDrawable;
	private BitmapType bitmapType;
	private ImageViewAware imageViewAware;

	public BitmapDisplayRunnable(ImageViewAware imageViewAware, BitmapDrawable bitmapDrawable, BitmapType bitmapType, Request request, Configuration configuration) {
		this.request = request;
		this.configuration = configuration;
		this.bitmapDrawable = bitmapDrawable;
		this.bitmapType = bitmapType;
		this.imageViewAware = imageViewAware;
	}

	@Override
	public void run() {
		ImageView imageView = imageViewAware.getImageView();
		if(imageView != null){
			request.getOptions().getBitmapDisplayer().display(imageView, bitmapDrawable, bitmapType, request, configuration);
		}
	}
}
