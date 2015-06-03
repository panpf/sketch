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

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import me.xiaopan.sketch.SketchImageViewInterface;

/**
 * 颜色渐入图片显示器
 */
public class ColorTransitionImageDisplayer implements ImageDisplayer {
	private static final String NAME = "ColorTransitionImageDisplayer";
	private int duration;
	private int color;

	public ColorTransitionImageDisplayer(int color, int duration){
		this.color = color;
		this.duration = duration;
	}

	public ColorTransitionImageDisplayer(int color){
		this(color, DEFAULT_ANIMATION_DURATION);
	}
	
	@Override
	public void display(SketchImageViewInterface sketchImageViewInterface, Drawable newDrawable) {
		if(newDrawable == null){
            return;
        }
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(color), newDrawable});
    	sketchImageViewInterface.clearAnimation();
		sketchImageViewInterface.setImageDrawable(transitionDrawable);
		transitionDrawable.setCrossFadeEnabled(true);
		transitionDrawable.startTransition(duration);
	}

	@Override
	public String getIdentifier() {
		return appendIdentifier(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder appendIdentifier(StringBuilder builder) {
		return builder.append(NAME)
				.append(" - ")
				.append("duration").append("=").append(duration)
				.append(", ")
				.append("color").append("=").append(color);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
