package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel

class WrappedProcessorTestViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val wrappedTestData = MutableLiveData(WrappedTest(30, 45, 45))

    fun changeRoundedRadius(roundedRadius: Int) {
        val old = wrappedTestData.value!!
        wrappedTestData.postValue(WrappedTest(roundedRadius, old.maskOpacity, old.rotate))
    }

    fun changeMaskOpacity(maskOpacity: Int) {
        val old = wrappedTestData.value!!
        wrappedTestData.postValue(WrappedTest(old.roundedRadius, maskOpacity, old.rotate))
    }

    fun changeRotate(rotate: Int) {
        val old = wrappedTestData.value!!
        wrappedTestData.postValue(WrappedTest(old.roundedRadius, old.maskOpacity, rotate))
    }

    class WrappedTest(
        val roundedRadius: Int,
        val maskOpacity: Int,
        val rotate: Int,
    )
}