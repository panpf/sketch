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

package com.github.panpf.sketch.optionsfilter;

import androidx.annotation.NonNull;

import com.github.panpf.sketch.request.DownloadOptions;
import com.github.panpf.sketch.request.LoadOptions;
import com.github.panpf.sketch.request.RequestLevel;

/**
 * 暂停下载
 */
public class PauseDownloadOptionsFilter implements OptionsFilter {

    @Override
    public void filter(@NonNull DownloadOptions options) {
        // 仅限 display 和 load 请求
        if (options instanceof LoadOptions) {
            // TODO 这里改成了 按大小覆盖之后强制点击显示就不起作用了
            RequestLevel level = options.getRequestLevel();
            if (level == null || level.getLevel() > RequestLevel.LOCAL.getLevel()) {
                options.setRequestLevel(RequestLevel.LOCAL);
            }
        }
    }
}
