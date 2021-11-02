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
import android.graphics.Bitmap
import android.text.TextUtils
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.SketchUtils

class ApkIconUriModel : AbsBitmapDiskCacheUriModel() {

    companion object {
        const val SCHEME = "apk.icon://"
        private const val NAME = "ApkIconUriModel"

        @JvmStatic
        fun makeUri(filePath: String): String {
            return SCHEME + filePath
        }
    }

    override fun match(uri: String): Boolean {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME)
    }

    /**
     * 获取 uri 所真正包含的内容部分，例如 "apk.icon:///sdcard/test.apk"，就会返回 "/sdcard/test.apk"
     *
     * @param uri 图片 uri
     * @return uri 所真正包含的内容部分，例如 "apk.icon:///sdcard/test.apk"，就会返回 "/sdcard/test.apk"
     */
    override fun getUriContent(uri: String): String {
        return if (match(uri)) uri.substring(SCHEME.length) else uri
    }

    override fun getDiskCacheKey(uri: String): String {
        return SketchUtils.createFileUriDiskCacheKey(uri, getUriContent(uri))
    }

    @Throws(GetDataSourceException::class)
    override fun getContent(context: Context, uri: String): Bitmap {
        val bitmapPool = Sketch.with(context).configuration.bitmapPool
        val iconBitmap =
            SketchUtils.readApkIcon(context, getUriContent(uri), false, NAME, bitmapPool)
        if (iconBitmap == null || iconBitmap.isRecycled) {
            val cause = String.format("Apk icon bitmap invalid. %s", uri)
            SLog.em(NAME, cause)
            throw GetDataSourceException(cause)
        }
        return iconBitmap
    }
}