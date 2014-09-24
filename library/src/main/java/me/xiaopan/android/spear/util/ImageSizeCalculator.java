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


package me.xiaopan.android.spear.util;

import android.widget.ImageView;

/**
 * 图片最大尺寸和修正尺寸计算器
 */
public interface ImageSizeCalculator {
    /**
     * 计算maxsize
     * @param imageView 你需要根据ImageView的宽高来计算
     * @return maxsize
     */
    public ImageSize calculateImageMaxsize(ImageView imageView);

    /**
     * 计算resize
     * @param imageView 你需要根据ImageView的宽高来计算
     * @return resize
     */
    public ImageSize calculateImageResize(ImageView imageView);

    /**
     * 比较两个maxsize的大小，在使用options()方法批量设置属性的时候会使用此方法比较RequestOptions的maxsize和已有的maxsize，如果前者小于后者就会使用前者代替后者
     * @param maxsize1 maxsize1
     * @param maxsize2 maxsize2
     * @return 等于0：两者相等；小于0：maxsize1小于maxsize2；大于0：maxsize1大于maxsize2
     */
    public int compareMaxsize(ImageSize maxsize1, ImageSize maxsize2);

    /**
     * 计算InSampleSize
     * @param outWidth 原始宽
     * @param outHeight 原始高
     * @param targetWidth 目标宽
     * @param targetHeight 目标高
     * @return 合适的InSampleSize
     */
    public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight);
}
