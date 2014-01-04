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
 * 位图解码器
 */
public interface BitmapDecoder{
	/**
	 * 解码
	 * @param inputStream 位图输入流
	 * @param maxSize 最大尺寸
	 * @param imageLoader 图片加载器
	 * @param requestName 请求名称
	 * @return
	 */
	public Bitmap decode(InputStream inputStream, ImageSize maxSize, ImageLoader imageLoader, String requestName);
}
