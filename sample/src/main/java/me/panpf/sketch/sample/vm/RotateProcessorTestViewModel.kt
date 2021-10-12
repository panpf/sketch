package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel

class RotateProcessorTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val rotateData = MutableLiveData(45)

    fun changeRotate(rotate: Int) {
        rotateData.postValue(rotate)
    }
}