package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel

class BlurProcessorTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val blurRadiusData = MutableLiveData(15)

    fun changeBlurRadius(blurRadius: Int) {
        blurRadiusData.postValue(blurRadius)
    }
}