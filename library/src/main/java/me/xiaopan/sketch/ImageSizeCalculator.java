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

import android.content.Context;

/**
 * 图片最大尺寸和修正尺寸计算器
 */
public interface ImageSizeCalculator {
    /**
     * 计算MaxSize
     * @param sketchImageViewInterface 你需要根据ImageView的宽高来计算
     * @return MaxSize
     */
    MaxSize calculateImageMaxSize(SketchImageViewInterface sketchImageViewInterface);

    /**
     * 计算Resize
     * @param sketchImageViewInterface 你需要根据ImageView的宽高来计算
     * @return Resize
     */
    Resize calculateImageResize(SketchImageViewInterface sketchImageViewInterface);

    /**
     * 计算FixedSize
     * @param sketchImageViewInterface 你需要根据ImageView的宽高来计算
     * @return FixedSize
     */
    FixedSize calculateImageFixedSize(SketchImageViewInterface sketchImageViewInterface);

    /**
     * 获取默认的maxSize
     * @param context 上下文
     * @return maxSize
     */
    MaxSize getDefaultImageMaxSize(Context context);

    /**
     * 比较两个maxSize的大小，在使用options()方法批量设置属性的时候会使用此方法比较RequestOptions的maxSize和已有的maxSize，如果前者小于后者就会使用前者代替后者
     * @param maxSize1 maxSize1
     * @param maxSize2 maxSize2
     * @return 等于0：两者相等；小于0：maxSize1小于maxSize2；大于0：maxSize1大于maxSize2
     */
    int compareMaxSize(MaxSize maxSize1, MaxSize maxSize2);

    /**
     * 计算InSampleSize
     * @param outWidth 原始宽
     * @param outHeight 原始高
     * @param targetWidth 目标宽
     * @param targetHeight 目标高
     * @return 合适的InSampleSize
     */
    int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight);

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
