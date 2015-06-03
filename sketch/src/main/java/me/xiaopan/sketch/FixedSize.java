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

public class FixedSize implements ImageSize{
	private int width;
	private int height;

	public FixedSize(int width, int height) {
		this.width = width;
		this.height = height;
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
		builder.append("MaxSize(");
		builder.append(width);
		builder.append("x");
		builder.append(height);
		builder.append(")");
		return builder;
	}
}
