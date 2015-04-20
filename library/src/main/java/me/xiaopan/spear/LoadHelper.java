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

package me.xiaopan.spear;

import android.widget.ImageView;

import me.xiaopan.spear.process.ImageProcessor;

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
     * 禁止解码Gif图片
     * @return LoadHelper
     */
    LoadHelper disableGifImage();

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxSize 最大尺寸
     * @return LoadHelper
     */
    LoadHelper maxSize(ImageSize maxSize);

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return LoadHelper
     */
    LoadHelper maxSize(int width, int height);

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param resize 新的尺寸
     * @return LoadHelper
     */
    LoadHelper resize(ImageSize resize);

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param width 宽
     * @param height 高
     * @return LoadHelper
     */
    LoadHelper resize(int width, int height);

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
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
     * 设置ScaleType，ImageProcessor会根据resize和ScaleType创建一张新的图片
     * @param scaleType ScaleType
     * @return LoadHelper
     */
    LoadHelper scaleType(ImageView.ScaleType scaleType);

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
     * 设置加载参数，你只需要提前将LoadOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return LoadHelper
     */
    LoadHelper options(Enum<?> optionsName);

    /**
     * 执行请求
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    Request fire();
}
