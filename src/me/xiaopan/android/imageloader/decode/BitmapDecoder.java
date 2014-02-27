/*
 * Copyright 2014 Peng fei Pan
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

package me.xiaopan.android.imageloader.decode;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.graphics.Bitmap;

/**
 * 位图解码器
 */
public interface BitmapDecoder{
	/**
	 * 解码
	 * @param onNewBitmapInputStreamListener 创建新的用来读取位图的输入流
	 * @param targetSize 目标尺寸
	 * @param configuration 配置
	 * @param requestName 请求名称
	 * @return
	 */
	public Bitmap decode(InputStreamCreator onNewBitmapInputStreamListener, ImageSize targetSize, Configuration configuration, String requestName);
}
