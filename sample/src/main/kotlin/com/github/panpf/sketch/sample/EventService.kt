package com.github.panpf.sketch.sample

import kotlinx.coroutines.flow.MutableSharedFlow

class EventService {
    val viewerPagerRotateEvent = MutableSharedFlow<Int>()
    val viewerPagerInfoEvent = MutableSharedFlow<Int>()
}