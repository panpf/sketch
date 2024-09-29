/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.ability

import com.github.panpf.sketch.source.DataFrom

private const val DATA_FROM_COLOR_MEMORY = 0x77008800   // dark green

private const val DATA_FROM_COLOR_MEMORY_CACHE = 0x7700FF00   // green

private const val DATA_FROM_COLOR_RESULT_CACHE = 0x77FFFF00 // yellow

private const val DATA_FROM_COLOR_LOCAL = 0x771E90FF   // dodger blue

private const val DATA_FROM_COLOR_DOWNLOAD_CACHE = 0x77FF8800 // dark yellow

private const val DATA_FROM_COLOR_NETWORK = 0x77FF0000  // red

/**
 * Get the color of the data source
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.ability.DataFromLogoTest.testDataFromColor
 */
fun dataFromColor(dataFrom: DataFrom): Int = when (dataFrom) {
    DataFrom.MEMORY_CACHE -> DATA_FROM_COLOR_MEMORY_CACHE
    DataFrom.MEMORY -> DATA_FROM_COLOR_MEMORY
    DataFrom.RESULT_CACHE -> DATA_FROM_COLOR_RESULT_CACHE
    DataFrom.DOWNLOAD_CACHE -> DATA_FROM_COLOR_DOWNLOAD_CACHE
    DataFrom.LOCAL -> DATA_FROM_COLOR_LOCAL
    DataFrom.NETWORK -> DATA_FROM_COLOR_NETWORK
}

/**
 * Get the default size of the data source logo
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.ability.DataFromLogoTest.testDataFromDefaultSize
 */
const val DATA_FROM_DEFAULT_SIZE = 20f