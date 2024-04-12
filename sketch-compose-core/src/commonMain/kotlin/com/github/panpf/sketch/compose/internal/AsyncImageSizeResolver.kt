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
package com.github.panpf.sketch.compose.internal

import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull

class AsyncImageSizeResolver(size: IntSize?) : SizeResolver {

    override val key: String = "ComposeComponentSize"

    // MutableStateFlow must be used here
    // Previously, due to the use of snapshotFlow { size }, the response to changes in size was slow.
    // When using the combination of Image plus AsyncImagePainter, the placeholder is not ready when the component is displayed.
    // The user sees the process of the component going from blank to displaying the placeholder.
    val sizeState: MutableStateFlow<IntSize?> = MutableStateFlow(size)

    override suspend fun size(): Size {
        return sizeState
            .filterNotNull()
            .mapNotNull { it.toSketchSize() }
            .first()
    }
}