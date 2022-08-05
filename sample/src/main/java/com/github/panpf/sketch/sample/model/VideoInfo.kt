/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.sample.model

import android.content.Context
import android.text.format.Formatter
import com.github.panpf.assemblyadapter.recycler.DiffKey
import com.github.panpf.tools4j.date.Datex
import com.github.panpf.tools4j.date.ktx.format
import com.github.panpf.tools4j.date.ktx.formatDuration
import com.github.panpf.tools4j.date.ktx.toDate

class VideoInfo(
    val title: String?,
    val path: String?,
    val mimeType: String?,
    val duration: Long,
    val date: Long,
    val size: Long,
) : DiffKey {

    override val diffKey = "VideoInfo-$path"

    private var tempFormattedSize: String? = null

    val tempFormattedDuration: String by lazy {
        duration.formatDuration("%h?:%M:%S")
    }

    val tempFormattedDate: String by lazy {
        date.toDate().format(Datex.yMdHm)
    }

    fun getTempFormattedSize(context: Context): String {
        if (tempFormattedSize == null) {
            tempFormattedSize = Formatter.formatFileSize(context, size)
        }
        return tempFormattedSize!!
    }

    override fun toString(): String {
        return "VideoInfo(title=$title, path=$path, mimeType=$mimeType, duration=$duration, date=$date, size=$size, tempFormattedSize=$tempFormattedSize)"
    }
}
