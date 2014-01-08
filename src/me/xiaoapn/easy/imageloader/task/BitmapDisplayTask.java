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
import me.xiaoapn.easy.imageloader.display.BitmapType;
import android.graphics.drawable.BitmapDrawable;

public class BitmapDisplayTask implements Runnable {
	private Request request;
	private boolean isFromMemoryCache;
	private BitmapType bitmapType;
	private Configuration configuration;
	private BitmapDrawable bitmapDrawable;
	private ImageViewAware imageViewAware;

	public BitmapDisplayTask(Configuration configuration, ImageViewAware imageViewAware, BitmapDrawable bitmapDrawable, BitmapType bitmapType, boolean isFromMemoryCache, Request request) {
		this.request = request;
		this.bitmapType = bitmapType;
		this.configuration = configuration;
		this.bitmapDrawable = bitmapDrawable;
		this.imageViewAware = imageViewAware;
		this.isFromMemoryCache = isFromMemoryCache;
	}

	@Override
	public void run() {
		request.getOptions().getBitmapDisplayer().display(imageViewAware, bitmapDrawable, bitmapType, isFromMemoryCache, configuration, request);
	}
}
