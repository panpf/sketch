/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.transition

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.toScale

interface TransitionComposeTarget : TransitionTarget {

    /**
     * The component's current [Painter].
     */
    val painter: Painter?

    /**
     * The [ContentScale] of the component that this target is applied to.
     */
    val contentScale: ContentScale

    /**
     * The [Alignment] of the component that this target is applied to.
     */
    val alignment: Alignment

    @Deprecated("Please use contentScale instead and will be deleted in the future")
    override val fitScale: Boolean
        get() = contentScale.fitScale

    override fun getScaleDecider(): ScaleDecider? = ScaleDecider(toScale(contentScale, alignment))
}