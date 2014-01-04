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
package me.xiaoapn.easy.imageloader.decode;

import java.io.InputStream;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import android.graphics.Bitmap;

/**
 * 位图加载器
 */
public interface BitmapLoader{
	/**
	 * 解码
	 * @param inputStream
	 * @param targetSize
	 * @param imageLoader
	 * @param name
	 * @return
	 */
	public Bitmap decode(InputStream inputStream, ImageSize targetSize, ImageLoader imageLoader, String name);
}
