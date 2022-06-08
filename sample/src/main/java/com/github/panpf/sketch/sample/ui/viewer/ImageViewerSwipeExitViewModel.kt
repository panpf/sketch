package com.github.panpf.sketch.sample.ui.viewer

import android.app.Application
import com.github.panpf.liveevent.LiveEvent
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel

class ImageViewerSwipeExitViewModel(application: Application) :
    LifecycleAndroidViewModel(application) {

    val progressChangedEvent = LiveEvent<Float>()
    val backEvent = LiveEvent<Boolean>()
}