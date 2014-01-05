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

package me.xiaoapn.easy.imageloader.display;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.execute.task.Request;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/**
 * 渐入位图显示器
 */
public class FadeInBitmapDisplayer implements BitmapDisplayer {

	@Override
	public void display(ImageView imageView, BitmapDrawable bitmapDrawable, BitmapType bitmapType, boolean isFromMemoryCache, ImageLoader imageLoader, Request request) {
		switch(bitmapType){
			case EMPTY : 
				imageView.setImageDrawable(bitmapDrawable);
				break;
			case FAILURE : 
				if(bitmapDrawable != null){
					fadeIn(imageView, bitmapDrawable);
				}else{
					imageView.setImageDrawable(bitmapDrawable);
				}
				break;
			case DISPLAY : 
				if(!isFromMemoryCache && bitmapDrawable != null){
					fadeIn(imageView, bitmapDrawable);
				}else{
					imageView.setImageDrawable(bitmapDrawable);
				}
				break;
		}
	}
	
	/**
	 * 渐入
	 * @param imageView
	 * @param bitmapDrawable
	 */
	private void fadeIn(ImageView imageView, BitmapDrawable bitmapDrawable){
		Drawable oldDrawable = imageView.getDrawable();
		Drawable firstDrawable  = oldDrawable != null?oldDrawable:new ColorDrawable(android.R.color.transparent);
		TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{firstDrawable, bitmapDrawable});
		imageView.setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(200);
	}
}
