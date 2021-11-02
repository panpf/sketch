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
package com.github.panpf.sketch.uri

import android.content.Context
import android.text.TextUtils
import com.github.panpf.sketch.uri.GetDataSourceException
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.uri.UriModel

/**
 * 负责解析某一特定类型的 uri
 */
abstract class UriModel {
    /**
     * 当前 [UriModel] 是否能够解析指定 uri
     *
     * @param uri 图片 uri
     */
    abstract fun match(uri: String): Boolean

    /**
     * 获取指定 uri 的数据，用于后续解码读取图片
     *
     * @param context        [Context]
     * @param uri            图片 uri
     * @param downloadResult 下载结果，只对 [.isFromNet] 为 true 的 [UriModel] 有用
     * @return DataSource
     */
    @Throws(GetDataSourceException::class)
    abstract fun getDataSource(
        context: Context, uri: String,
        downloadResult: DownloadResult?
    ): DataSource

    /**
     * 获取 uri 中的内容部分，默认是它自己。例如对于 http 类型的 uri，内容部分就是它自己，而对于 asset://sample.jpg 类型的 uri，内容部分就是 sample.jpg
     *
     * @param uri 图片 uri
     * @return uri 中的内容部分，默认是它自己
     */
    open fun getUriContent(uri: String): String {
        return uri
    }

    /**
     * 获取指定 uri 的磁盘缓存 key，默认返回 uri 自己
     *
     * @param uri 图片 uri
     * @return 指定 uri 的磁盘缓存 key
     */
    open fun getDiskCacheKey(uri: String): String {
        return uri
    }

    /**
     * 当前类型 uri 的数据是否来自网络
     */
    open val isFromNet: Boolean
        get() = false

    /**
     * 在生成 key 时，是否需要将 uri 使用 md5 转成短 uri，适用于非常长的 uri，例如 base64 格式的 uri
     */
    open val isConvertShortUriForKey: Boolean
        get() = false

    companion object {
        /**
         * 匹配可以解析指定 uri 的 [UriModel]
         *
         * @param sketch [Sketch]
         * @param uri    图片 uri
         * @return [UriModel]. 能够解析这种 uri 的 [UriModel]
         */
        // todo 放到 Configuration 中
        @JvmStatic
        fun match(sketch: Sketch, uri: String): UriModel? {
            return if (!TextUtils.isEmpty(uri)) sketch.configuration.uriModelManager.match(uri) else null
        }

        /**
         * 匹配可以解析指定 uri 的 [UriModel]
         *
         * @param context [Context]
         * @param uri     图片 uri
         * @return [UriModel]. 能够解析这种 uri 的 [UriModel]
         */
        // todo 放到 Configuration 中
        @JvmStatic
        fun match(context: Context, uri: String): UriModel? {
            return match(Sketch.with(context), uri)
        }
    }
}