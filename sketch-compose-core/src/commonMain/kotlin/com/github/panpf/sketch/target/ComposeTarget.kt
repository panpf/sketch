/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
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
package com.github.panpf.sketch.target

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ComposeTargetRequestDelegate
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.resize.ComposeResizeOnDrawHelper
import com.github.panpf.sketch.resize.ResizeOnDrawHelper
import com.github.panpf.sketch.transform.CrossfadeTransition
import com.github.panpf.sketch.transition.ComposeCrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import kotlinx.coroutines.Job

interface ComposeTarget : Target {

    /**
     * The component's current [Painter].
     */
    var painter: Painter?

    override val currentImage: Image?
        get() = painter?.asSketchImage()

    override fun getResizeOnDrawHelper(): ResizeOnDrawHelper? {
        // TODO rename to PainterImageSizeWrapper
        return ComposeResizeOnDrawHelper
    }

    override fun getTargetCrossfadeTransitionFactory(
        factory: CrossfadeTransition.Factory
    ): Transition.Factory? {
        return ComposeCrossfadeTransition.Factory(
            durationMillis = factory.durationMillis,
            fadeStart = factory.fadeStart,
            preferExactIntrinsicSize = factory.preferExactIntrinsicSize,
            alwaysUse = factory.alwaysUse,
        )
    }

    override fun newRequestDelegate(
        sketch: Sketch,
        initialRequest: ImageRequest,
        job: Job
    ): RequestDelegate = ComposeTargetRequestDelegate(sketch, initialRequest, this, job)
}