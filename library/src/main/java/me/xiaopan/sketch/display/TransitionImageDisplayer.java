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

package me.xiaopan.sketch.display;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import me.xiaopan.sketch.RecycleGifDrawable;

/**
 * 过渡图片显示器
 */
public class TransitionImageDisplayer implements ImageDisplayer {
	private int duration;

	public TransitionImageDisplayer(int duration){
		this.duration = duration;
	}
	
	public TransitionImageDisplayer(){
		this(500);
	}
	
	@Override
	public void display(ImageView imageView, Drawable newDrawable) {
		if(newDrawable == null){
            return;
        }
		if(newDrawable instanceof RecycleGifDrawable){
        	imageView.clearAnimation();
			imageView.setImageDrawable(newDrawable);
		}else{
			Drawable oldDrawable = imageView.getDrawable();
			if(oldDrawable == null){
				new ColorDrawable(Color.TRANSPARENT);
			}
			TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{oldDrawable, newDrawable});
        	imageView.clearAnimation();
			imageView.setImageDrawable(transitionDrawable);
			transitionDrawable.setCrossFadeEnabled(true);
			transitionDrawable.startTransition(duration);
		}
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
