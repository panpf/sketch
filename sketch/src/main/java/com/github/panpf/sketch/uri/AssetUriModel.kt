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
import com.github.panpf.sketch.datasource.AssetsDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.DownloadResult

class AssetUriModel : UriModel() {

    companion object {
        const val SCHEME = "asset://"

        @JvmStatic
        fun makeUri(assetResName: String): String {
            require(!TextUtils.isEmpty(assetResName)) { "Param assetResName is null or empty" }
            return if (!assetResName.startsWith(SCHEME)) SCHEME + assetResName else assetResName
        }
    }

    override fun match(uri: String): Boolean {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME)
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "asset://test.png"，就会返回 "test.png"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "asset://test.png"，就会返回 "test.png"
     */
    override fun getUriContent(uri: String): String {
        return if (match(uri)) uri.substring(SCHEME.length) else uri
    }

    @Throws(GetDataSourceException::class)
    override fun getDataSource(
        context: Context,
        uri: String,
        downloadResult: DownloadResult?
    ): DataSource {
        return AssetsDataSource(context, getUriContent(uri))
    }
}