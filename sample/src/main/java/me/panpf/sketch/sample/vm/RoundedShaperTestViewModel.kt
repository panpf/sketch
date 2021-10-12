package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel

class RoundedShaperTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val testData = MutableLiveData(ResizeTest(20, 5))

    fun changeRoundedRadius(roundedRadius: Int) {
        val old = testData.value!!
        testData.postValue(ResizeTest(roundedRadius, old.strokeWidth))
    }

    fun changeStrokeWidth(strokeWidth: Int) {
        val old = testData.value!!
        testData.postValue(ResizeTest(old.roundedRadius, strokeWidth))
    }

    class ResizeTest(
        val roundedRadius: Int,
        val strokeWidth: Int,
    )
}