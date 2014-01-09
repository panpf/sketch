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
	private BitmapType bitmapType;
	private Configuration configuration;
	private BitmapDrawable bitmapDrawable;

	public BitmapDisplayRunnable(Request request, BitmapDrawable bitmapDrawable, BitmapType bitmapType, Configuration configuration) {
		this.request = request;
		this.configuration = configuration;
		this.bitmapDrawable = bitmapDrawable;
		this.bitmapType = bitmapType;
	}

	@Override
	public void run() {
		ImageView imageView = request.getImageViewAware().getImageView();
		if(imageView != null){
			request.getOptions().getBitmapDisplayer().display(imageView, bitmapDrawable, bitmapType, request, configuration);
			if(request.getImageLoadListener() != null){
				if(bitmapType == BitmapType.SUCCESS){
					request.getImageLoadListener().onComplete(request.getImageUri(), imageView, bitmapDrawable);
				}else{
					request.getImageLoadListener().onFailed(request.getImageUri(), imageView);
				}
			}
		}
	}
}
