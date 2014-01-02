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

package me.xiaoapn.easy.imageloader.execute;

import me.xiaoapn.easy.imageloader.Options;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 加载请求
 */
public abstract class Request {
	private String id;	//ID
	private String name;	//名称，用于在输出log时区分不同的请求
	private Options options;	//加载选项
	private ImageView imageView;	//显示图片的视图
	private Bitmap resultBitmap;	//加载结果Bitmap
	
	public Request(String id, String name, ImageView imageView, Options options) {
		this.id = id;
		this.name = name;
		this.options = options;
		this.imageView = imageView;
	}

	/**
	 * 获取ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 设置ID
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取名称，用于在输出log时区分不同的请求
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称，用于在输出log时区分不同的请求
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 获取图片视图
	 * @return 图片视图
	 */
	public ImageView getImageView() {
		return imageView;
	}
	
	/**
	 * 设置图片视图
	 * @param imageView 图片视图
	 */
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	/**
	 * 获取加载选项
	 * @return
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * 设置加载选项
	 * @param options
	 */
	public void setOptions(Options options) {
		this.options = options;
	}

	/**
	 * 获取最终的Bitmap
	 * @return
	 */
	public Bitmap getResultBitmap() {
		return resultBitmap;
	}

	/**
	 * 设置最终的Bitmap
	 * @param resultBitmap
	 */
	public void setResultBitmap(Bitmap resultBitmap) {
		this.resultBitmap = resultBitmap;
	}
}
