package com.github.panpf.sketch.sample.ui.test.transform

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel

class CircleCropTransformationTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val scaleData = MutableLiveData(Scale.CENTER_CROP)

    fun changeRotate(scale: Scale) {
        scaleData.postValue(scale)
    }
}