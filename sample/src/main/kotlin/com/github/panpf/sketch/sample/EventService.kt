package com.github.panpf.sketch.sample

import com.github.panpf.liveevent.LiveEvent

class EventService {
    val viewerPagerShareEvent = LiveEvent<Int>()
    val viewerPagerSaveEvent = LiveEvent<Int>()
    val viewerPagerRotateEvent = LiveEvent<Int>()
    val viewerPagerInfoEvent = LiveEvent<Int>()

    val hugeViewerPageRotateEvent = LiveEvent<Int>()
}