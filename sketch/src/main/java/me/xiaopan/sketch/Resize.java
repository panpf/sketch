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

package me.xiaopan.sketch;

import android.widget.ImageView;

public class Resize implements ImageSize{
	private int width;
	private int height;

	private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;

	public Resize(Resize sourceResize){
		this.width = sourceResize.width;
		this.height = sourceResize.height;
		this.scaleType = sourceResize.scaleType;
	}

	public Resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Resize(int width, int height, ImageView.ScaleType scaleType) {
		this(width, height);
		this.scaleType = scaleType;
	}

	public ImageView.ScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(ImageView.ScaleType scaleType) {
		this.scaleType = scaleType;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void set(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String getIdentifier(){
		return appendIdentifier(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder appendIdentifier(StringBuilder builder){
		builder.append("Resize(");
		builder.append(width);
		builder.append("x");
		builder.append(height);
		if(scaleType != null){
			builder.append(":");
			builder.append(scaleType.name());
		}
		builder.append(")");
		return builder;
	}
}
