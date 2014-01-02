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

import me.xiaoapn.easy.imageloader.display.animation.AlphaAnimationGenerator;
import me.xiaoapn.easy.imageloader.display.animation.AnimationGenerator;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SimpleBitmapDisplayer implements BitmapDisplayer {

	private AnimationGenerator animationGenerator;
	
	public SimpleBitmapDisplayer(AnimationGenerator animationGenerator){
		this.animationGenerator = animationGenerator;
	}
	
	public SimpleBitmapDisplayer(){
		this(new AlphaAnimationGenerator());
	}

	@Override
	public void display(ImageView imageView, Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
		if(animationGenerator != null){
			Animation animation = animationGenerator.generateAnimation();
			if(animation != null){
				imageView.startAnimation(animation);
			}
		}
	}
}