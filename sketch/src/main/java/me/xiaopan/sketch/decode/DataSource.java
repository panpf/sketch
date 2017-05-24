/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.decode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.request.ImageFrom;

/**
 * 数据源
 */
public interface DataSource {
    /**
     * 获取输入流
     *
     * @return 输入流
     * @throws IOException 数据源异常
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获取数据长度
     *
     * @return 数据长度
     * @throws IOException 数据源异常
     */
    long getLength() throws IOException;

    /**
     * 获取可用的文件
     *
     * @param outDir  如果当前数据源无法直接返回一个可用的文件，就将内容输出到指定文件夹中
     * @param outName 输出文件的名字
     * @return null：无可用文件
     */
    File getFile(File outDir, String outName) throws IOException;

    /**
     * 获取图片来源
     *
     * @return 图片来源
     */
    ImageFrom getImageFrom();

    /**
     * 创建GifDrawable
     *
     * @param key        请求的唯一标识 key
     * @param uri        图片uri
     * @param imageAttrs 图片的属性
     * @param bitmapPool bitmap缓存池
     * @return GifDrawable
     */
    SketchGifDrawable makeGifDrawable(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool);
}
