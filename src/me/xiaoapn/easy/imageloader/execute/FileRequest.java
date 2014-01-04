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

import java.io.File;

import me.xiaoapn.easy.imageloader.Options;
import me.xiaoapn.easy.imageloader.util.ImageSize;

/**
 * 加载请求
 */
public class FileRequest extends Request{
	private File imageFile;	//图片文件
	
	public FileRequest(String id, String name, File imageFile, Options options, ImageSize targetSize) {
		super(id, name, options, targetSize);
		this.imageFile = imageFile;
	}

	/**
	 * 获取图片文件
	 * @return 图片文件
	 */
	public File getImageFile() {
		return imageFile;
	}

	/**
	 * 设置图片文件
	 * @param imageFile 图片文件
	 */
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
}
