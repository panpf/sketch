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

package me.xiaopan.sketch.process;

import android.graphics.Bitmap;

import me.xiaopan.sketch.Resize;
import me.xiaopan.sketch.Sketch;

/**
 * 图片处理器，你可以是实现此接口，将你的图片处理成你想要的效果
 */
public interface ImageProcessor {
	/**
	 * 处理
	 * @param sketch Sketch
	 * @param bitmap 要被处理的图片
	 * @param resize 新的尺寸
	 * @param forceUseResize 是否强制使用resize
     * @param lowQualityImage 需要一个低质量的新图片
	 * @return 新的图片
	 */
	Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage);

    /**
     * 获取标识符
     * @return 标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(StringBuilder builder);
}
