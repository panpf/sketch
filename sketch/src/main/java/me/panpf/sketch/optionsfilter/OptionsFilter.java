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

package me.panpf.sketch.optionsfilter;

import me.panpf.sketch.request.DownloadOptions;

/**
 * 选项过滤器，用于统一修改 Options
 */
public interface OptionsFilter {
    /**
     * 过滤 Options
     *
     * @param options {@link DownloadOptions}
     */
    void filter(DownloadOptions options);
}
