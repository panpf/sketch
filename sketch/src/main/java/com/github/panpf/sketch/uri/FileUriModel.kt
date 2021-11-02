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
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.request.DownloadResult
import java.io.File

open class FileUriModel : UriModel() {

    companion object {
        const val SCHEME = "/"
    }

    override fun match(uri: String): Boolean {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME)
    }

    override fun getDataSource(
        context: Context,
        uri: String,
        downloadResult: DownloadResult?
    ): DataSource {
        return FileDataSource(File(uri))
    }
}