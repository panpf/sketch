package com.github.panpf.sketch.sample.ui.test.transform

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel

class RotateTransformationTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val rotateData = MutableLiveData(30)

    fun changeRotate(rotate: Int) {
        rotateData.postValue(rotate)
    }
}