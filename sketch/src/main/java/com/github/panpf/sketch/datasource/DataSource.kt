/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.datasource

import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.decode.NotFoundGifLibraryException
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.drawable.SketchGifDrawable
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * 数据源
 */
interface DataSource {
    /**
     * 获取输入流
     *
     * @return [InputStream]
     * @throws IOException 数据源异常
     */
    @get:Throws(IOException::class)
    val inputStream: InputStream

    /**
     * 获取数据长度
     *
     * @return 数据长度
     * @throws IOException 数据源异常
     */
    @get:Throws(IOException::class)
    val length: Long

    /**
     * 获取可用的文件
     *
     * @param outDir  如果当前数据源无法直接返回一个可用的文件，就将内容输出到指定文件夹中
     * @param outName 输出文件的名字
     * @return null：无可用文件
     */
    @Throws(IOException::class)
    fun getFile(outDir: File?, outName: String?): File?

    /**
     * 获取图片来源
     *
     * @return [ImageFrom]
     */
    val imageFrom: ImageFrom

    /**
     * 创建 GifDrawable
     *
     * @param key        请求的唯一标识 key
     * @param uri        图片 uri
     * @param imageAttrs 图片的属性
     * @param bitmapPool [android.graphics.Bitmap] 缓存池
     * @return [SketchGifDrawable]
     * @throws IOException                 数据源异常
     * @throws NotFoundGifLibraryException 没有集成 sketch-gif
     */
    @Throws(IOException::class, NotFoundGifLibraryException::class)
    fun makeGifDrawable(
        key: String, uri: String, imageAttrs: ImageAttrs,
        bitmapPool: BitmapPool
    ): SketchGifDrawable
}