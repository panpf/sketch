package com.github.panpf.sketch.sample.ui.viewer

import android.app.Application
import com.github.panpf.liveevent.LiveEvent
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel

class ImageViewerPagerViewModel(application: Application) : LifecycleAndroidViewModel(application) {

    val shareEvent = LiveEvent<Int>()
    val saveEvent = LiveEvent<Int>()
    val rotateEvent = LiveEvent<Int>()
    val infoEvent = LiveEvent<Int>()
}