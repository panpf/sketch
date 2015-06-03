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

import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.process.ImageProcessor;

/**
 * LoadHelper
 */
public interface LoadHelper {
    /**
     * 设置名称，用于在log总区分请求
     * @param name 名称
     * @return LoadHelper
     */
    LoadHelper name(String name);

    /**
     * 关闭硬盘缓存
     * @return LoadHelper
     */
    LoadHelper disableDiskCache();

    /**
     * 不解码Gif图片
     * @return LoadHelper
     */
    LoadHelper disableDecodeGifImage();

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxSize 最大尺寸
     * @return LoadHelper
     */
    LoadHelper maxSize(MaxSize maxSize);

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return LoadHelper
     */
    LoadHelper maxSize(int width, int height);

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     * @param width 宽
     * @param height 高
     * @return LoadHelper
     */
    LoadHelper resize(int width, int height);

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     * @param width 宽
     * @param height 高
     * @param scaleType
     * @return LoadHelper
     */
    LoadHelper resize(int width, int height, ScaleType scaleType);

    /**
     * 强制使经过resize返回的图片同resize的尺寸一致
     * @return DisplayHelper
     */
    LoadHelper forceUseResize();

    /**
     * 返回低质量的图片
     * @return LoadHelper
     */
    LoadHelper lowQualityImage();

    /**
     * 设置图片处理器，图片处理器会根据resize创建一张新的图片
     * @param processor 图片处理器
     * @return LoadHelper
     */
    LoadHelper processor(ImageProcessor processor);

    /**
     * 设置加载监听器
     * @param loadListener 加载监听器
     * @return LoadHelper
     */
    LoadHelper listener(LoadListener loadListener);

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return LoadHelper
     */
    LoadHelper progressListener(ProgressListener progressListener);

    /**
     * 设置加载参数
     * @param options 加载参数
     * @return LoadHelper
     */
    LoadHelper options(LoadOptions options);

    /**
     * 设置加载参数，你只需要提前将LoadOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return LoadHelper
     */
    LoadHelper options(Enum<?> optionsName);

    /**
     * 设置请求Level
     * @param requestLevel 请求Level
     * @return DisplayHelper
     */
    LoadHelper requestLevel(RequestLevel requestLevel);

    /**
     * 提交请求
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    Request commit();
}
