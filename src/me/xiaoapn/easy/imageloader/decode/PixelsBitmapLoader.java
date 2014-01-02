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

package me.xiaoapn.easy.imageloader.decode;

import java.io.File;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.util.BitmapDecoder;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 根据最大像素数来读取位图的位图加载器，默认最大像素数为屏幕的宽乘以屏幕的高
 * <br>如果通过ImageView.getLayoutParams().width乘以ImageView.getLayoutParams().height得到的值大于0并且小于默认最大像素数的话就会临时使用得到的值作为最大像素数来读取图片
 */
public class PixelsBitmapLoader implements BitmapLoader{
	private int defaultMaxNumOfPixels;	//默认最大像素数
	
	/**
	 * 创建根据像素数来读取位图的位图加载器
	 * @param defaultMaxNumOfPixels 默认最大像素数，可以以此来限制读取的位图的尺寸
	 */
	public PixelsBitmapLoader(int defaultMaxNumOfPixels){
		setDefaultMaxNumOfPixels(defaultMaxNumOfPixels);
	}
	
	/**
	 * 创建位图加载器，默认的最大像素数是当前屏幕的宽乘以高
	 */
	public PixelsBitmapLoader(){}
	
	@Override
	public Bitmap onFromByteArrayLoad(byte[] byteArray, ImageView showImageView, ImageLoader imageLoader) {
		if(defaultMaxNumOfPixels == 0){
			int[] screenSize = GeneralUtils.getScreenSize(showImageView.getContext());
			defaultMaxNumOfPixels = (screenSize[0] * screenSize[1]);
		}
		showImageView.getMeasuredWidth();
		int currentNumOfPixels;
		if(showImageView.getLayoutParams().width > 0 && showImageView.getLayoutParams().height > 0){
			currentNumOfPixels = showImageView.getLayoutParams().width * showImageView.getLayoutParams().height;
			if(currentNumOfPixels > defaultMaxNumOfPixels){
				currentNumOfPixels = defaultMaxNumOfPixels;
			}
		}else{
			currentNumOfPixels = defaultMaxNumOfPixels;
		}
		return new BitmapDecoder(currentNumOfPixels, imageLoader).decodeByteArray(byteArray);
	}
	
	@Override
	public Bitmap onFromFileLoad(File localFile, ImageView showImageView, ImageLoader imageLoader) {
		if(defaultMaxNumOfPixels == 0){
			int[] screenSize = GeneralUtils.getScreenSize(showImageView.getContext());
			defaultMaxNumOfPixels = (screenSize[0] * screenSize[1]);
		}
		int currentNumOfPixels;
		if(showImageView.getLayoutParams().width > 0 && showImageView.getLayoutParams().height > 0){
			currentNumOfPixels = showImageView.getLayoutParams().width * showImageView.getLayoutParams().height;
			if(currentNumOfPixels > defaultMaxNumOfPixels){
				currentNumOfPixels = defaultMaxNumOfPixels;
			}
		}else{
			currentNumOfPixels = defaultMaxNumOfPixels;
		}
		return new BitmapDecoder(currentNumOfPixels, imageLoader).decodeFile(localFile.getPath());
	}

	/**
	 * 获取默认的最大像素数
	 * @return
	 */
	public int getDefaultMaxNumOfPixels() {
		return defaultMaxNumOfPixels;
	}

	/**
	 * 设置默认的最大像素数
	 * @param defaultMaxNumOfPixels，当小于等于0时会抛出IllegalArgumentException异常
	 */
	public void setDefaultMaxNumOfPixels(int defaultMaxNumOfPixels) {
		if(defaultMaxNumOfPixels <= 0){
			throw new IllegalArgumentException("defaultMaxNumOfPixels 不能小于等于0");
		}
		this.defaultMaxNumOfPixels = defaultMaxNumOfPixels;
	}
}