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

package me.xiaopan.android.spear.display;

import android.graphics.drawable.BitmapDrawable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import me.xiaopan.android.spear.request.DisplayRequest;

/**
 * 由大到小图片显示器
 */
public class ZoomOutImageDisplayer implements ImageDisplayer {
	private int duration;
	private float fromX;
	private float fromY;
	private Interpolator interpolator;
	
	public ZoomOutImageDisplayer(float fromX, float fromY, Interpolator interpolator, int duration) {
		this.duration = duration;
		this.fromX = fromX;
		this.fromY = fromY;
		this.interpolator = interpolator;
	}
	
	public ZoomOutImageDisplayer(float fromX, float fromY, Interpolator interpolator) {
		this(fromX, fromY, interpolator, DEFAULT_ANIMATION_DURATION);
	}
	
	public ZoomOutImageDisplayer(float fromX, float fromY) {
		this(fromX, fromY, new AccelerateDecelerateInterpolator(), DEFAULT_ANIMATION_DURATION);
	}
	
	public ZoomOutImageDisplayer(Interpolator interpolator) {
		this(1.5f, 1.5f, interpolator, DEFAULT_ANIMATION_DURATION);
	}

	public ZoomOutImageDisplayer(int duration){
		this(1.5f, 1.5f, new AccelerateDecelerateInterpolator(), duration);
	}
	
	public ZoomOutImageDisplayer(){
		this(1.5f, 1.5f, new AccelerateDecelerateInterpolator(), DEFAULT_ANIMATION_DURATION);
	}
	
	@Override
	public void display(ImageView imageView, BitmapDrawable bitmapDrawable, BitmapType bitmapType, DisplayRequest displayRequest) {
        if(bitmapDrawable == null){
            return;
        }
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, 1.0f, fromY, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setInterpolator(interpolator);
		scaleAnimation.setDuration(duration);
    	imageView.clearAnimation();
		imageView.setImageDrawable(bitmapDrawable);
		imageView.startAnimation(scaleAnimation);
	}
}
