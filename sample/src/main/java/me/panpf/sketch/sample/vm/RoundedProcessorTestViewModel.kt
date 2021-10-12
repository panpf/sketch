package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel

class RoundedProcessorTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val roundedRadiusData = MutableLiveData(30)

    fun changeRounded(rounded: Int) {
        roundedRadiusData.postValue(rounded)
    }
}