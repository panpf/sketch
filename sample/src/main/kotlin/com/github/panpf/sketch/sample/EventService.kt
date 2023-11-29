package com.github.panpf.sketch.sample

import kotlinx.coroutines.flow.MutableSharedFlow

class EventService {
    val viewerPagerShareEvent = MutableSharedFlow<Int>()
    val viewerPagerSaveEvent = MutableSharedFlow<Int>()
    val viewerPagerRotateEvent = MutableSharedFlow<Int>()
    val viewerPagerInfoEvent = MutableSharedFlow<Int>()
}