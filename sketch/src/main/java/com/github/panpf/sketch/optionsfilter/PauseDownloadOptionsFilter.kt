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
package com.github.panpf.sketch.optionsfilter

import com.github.panpf.sketch.request.DownloadOptions
import com.github.panpf.sketch.request.LoadOptions
import com.github.panpf.sketch.request.RequestLevel

/**
 * 暂停下载
 */
class PauseDownloadOptionsFilter : OptionsFilter {
    override fun filter(options: DownloadOptions) {
        // 仅限 display 和 load 请求
        if (options is LoadOptions) {
            // TODO 这里改成了按大小覆盖之后强制点击显示就不起作用了
            val level = options.getRequestLevel()
            if (level == null || level.level > RequestLevel.LOCAL.level) {
                options.requestLevel = RequestLevel.LOCAL
            }
        }
    }
}