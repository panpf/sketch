/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.decode.NotFoundGifLibraryException;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.request.ImageFrom;

/**
 * 数据源
 */
public interface DataSource {
    /**
     * 获取输入流
     *
     * @return {@link InputStream}
     * @throws IOException 数据源异常
     */
    @NonNull
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
    @Nullable
    File getFile(@Nullable File outDir, @Nullable String outName) throws IOException;

    /**
     * 获取图片来源
     *
     * @return {@link ImageFrom}
     */
    @NonNull
    ImageFrom getImageFrom();

    /**
     * 创建 GifDrawable
     *
     * @param key        请求的唯一标识 key
     * @param uri        图片 uri
     * @param imageAttrs 图片的属性
     * @param bitmapPool {@link android.graphics.Bitmap} 缓存池
     * @return {@link SketchGifDrawable}
     * @throws IOException                 数据源异常
     * @throws NotFoundGifLibraryException 没有集成 sketch-gif
     */
    @NonNull
    SketchGifDrawable makeGifDrawable(@NonNull String key, @NonNull String uri, @NonNull ImageAttrs imageAttrs,
                                      @NonNull BitmapPool bitmapPool) throws IOException, NotFoundGifLibraryException;
}
