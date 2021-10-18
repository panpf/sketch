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

/**
 * 解码时质量优先于速度
 */
public class InPreferQualityOverSpeedOptionsFilter implements OptionsFilter {

    @Override
    public void filter(@NonNull DownloadOptions options) {
        if (options instanceof LoadOptions) {
            ((LoadOptions) options).setInPreferQualityOverSpeed(true);
        }
    }
}
