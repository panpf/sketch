package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel

class MaskProcessorTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val maskOpacityData = MutableLiveData(15)

    fun changeMaskOpacity(maskOpacity: Int) {
        maskOpacityData.postValue(maskOpacity)
    }
}