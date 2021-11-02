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
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DrawableDataSource
import com.github.panpf.sketch.request.DownloadResult

class DrawableUriModel : UriModel() {

    companion object {
        const val SCHEME = "drawable://"
        private const val NAME = "DrawableUriModel"

        @JvmStatic
        fun makeUri(@DrawableRes drawableResId: Int): String {
            return SCHEME + drawableResId
        }
    }

    override fun match(uri: String): Boolean {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME)
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "drawable.icon://424214"，就会返回 "424214"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "drawable.icon://424214"，就会返回 "424214"
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
        val resId: Int = try {
            Integer.valueOf(getUriContent(uri))
        } catch (e: NumberFormatException) {
            val cause = String.format("Conversion resId failed. %s", uri)
            SLog.emt(NAME, e, cause)
            throw GetDataSourceException(cause, e)
        }
        return DrawableDataSource(context, resId)
    }

    fun getResId(uri: String): Int {
        return getUriContent(uri).toInt()
    }
}