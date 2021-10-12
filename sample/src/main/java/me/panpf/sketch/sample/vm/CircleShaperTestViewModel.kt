package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel

class CircleShaperTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val strokeWidthData = MutableLiveData(5)

    fun changeStrokeWidth(strokeWidth: Int) {
        strokeWidthData.postValue(strokeWidth)
    }
}