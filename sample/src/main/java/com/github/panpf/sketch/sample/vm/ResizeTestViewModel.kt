package com.github.panpf.sketch.sample.vm

import android.app.Application
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel

class ResizeTestViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val resizeTestData = MutableLiveData(ResizeTest(50, 50, ImageView.ScaleType.FIT_CENTER))

    fun changeWidth(width: Int) {
        val old = resizeTestData.value!!
        resizeTestData.postValue(ResizeTest(width, old.height, old.scaleType))
    }

    fun changeHeight(height: Int) {
        val old = resizeTestData.value!!
        resizeTestData.postValue(ResizeTest(old.width, height, old.scaleType))
    }

    fun changeScaleType(scaleType: ImageView.ScaleType) {
        val old = resizeTestData.value!!
        resizeTestData.postValue(ResizeTest(old.width, old.height, scaleType))
    }

    class ResizeTest(
        val width: Int,
        val height: Int,
        val scaleType: ImageView.ScaleType,
    )
}