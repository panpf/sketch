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
package me.xiaoapn.easy.imageloader.process;

import java.io.File;

import me.xiaoapn.easy.imageloader.ImageLoader;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 位图解码器
 */
public interface BitmapLoader{
	/**
	 * 从本地文件加载
	 * @param localFile 本地文件
	 * @param showImageView 图片视图
	 * @param imageLoader
	 * @return
	 */
	public Bitmap onDecodeFile(File localFile, ImageView showImageView, ImageLoader imageLoader);
	
	/**
	 * 从字节数组中加载
	 * @param byteArray
	 * @param showImageView 图片视图
	 * @param imageLoader
	 * @return
	 */
	public Bitmap onDecodeByteArray(byte[] byteArray, ImageView showImageView, ImageLoader imageLoader);
}
