/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaoapn.easy.imageloader;

import java.io.File;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 圆角位图加载器，此加载器会把原始位图处理成宽高同其ImageView的宽高一样，并且还是圆角的
 */
public class RoundedBitmapLoader extends PixelsBitmapLoader {
	private int roundPixels;
	
	public RoundedBitmapLoader(int defaultMaxNumOfPixels, int roundPixels){
		super(defaultMaxNumOfPixels);
		this.roundPixels = roundPixels;
	}
	
	public RoundedBitmapLoader(int roundPixels){
		this.roundPixels = roundPixels;
	}
	
	public RoundedBitmapLoader(){
		this(18);
	}
	
	@Override
	public Bitmap onDecodeFile(File localFile, ImageView showImageView, ImageLoader imageLoader) {
		return GeneralUtils.roundCorners(super.onDecodeFile(localFile, showImageView, imageLoader), showImageView, roundPixels);
	}

	@Override
	public Bitmap onDecodeByteArray(byte[] byteArray, ImageView showImageView, ImageLoader imageLoader) {
		return GeneralUtils.roundCorners(super.onDecodeByteArray(byteArray, showImageView, imageLoader), showImageView, roundPixels);
	}

	public int getRoundPixels() {
		return roundPixels;
	}

	public void setRoundPixels(int roundPixels) {
		this.roundPixels = roundPixels;
	}
}
