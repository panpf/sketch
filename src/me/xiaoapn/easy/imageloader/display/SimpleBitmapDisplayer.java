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

import me.xiaoapn.easy.imageloader.Options;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.widget.ImageView;

public class SimpleBitmapDisplayer implements BitmapDisplayer {

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void display(Resources resources, ImageView imageView, BitmapDrawable bitmapDrawable, Options options, boolean isFromMemoryCache) {
		if(!isFromMemoryCache){
			TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(android.R.color.transparent), bitmapDrawable});
			if(GeneralUtils.hasJellyBean()){
				imageView.setBackground(options.getLoadingBitmap());
			}else{
				imageView.setBackgroundDrawable(options.getLoadingBitmap());
			}
			imageView.setImageDrawable(transitionDrawable);
			transitionDrawable.startTransition(200);
		}else{
			imageView.setImageDrawable(bitmapDrawable);
		}
	}
}
