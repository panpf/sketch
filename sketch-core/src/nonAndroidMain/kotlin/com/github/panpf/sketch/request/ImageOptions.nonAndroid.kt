package com.github.panpf.sketch.request

import com.github.panpf.sketch.resize.ResizeOnDrawHelper
import com.github.panpf.sketch.transition.Crossfade
import com.github.panpf.sketch.transition.Transition

actual fun createCrossfadeTransitionFactory(crossfade: Crossfade): Transition.Factory? = null

actual fun createResizeOnDrawHelper(): ResizeOnDrawHelper? = null