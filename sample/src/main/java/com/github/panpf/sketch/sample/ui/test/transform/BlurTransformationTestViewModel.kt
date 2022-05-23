package com.github.panpf.sketch.sample.ui.test.transform

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel

class BlurTransformationTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val radiusData = MutableLiveData(30)

    fun changeRadius(radius: Int) {
        radiusData.postValue(radius)
    }
}