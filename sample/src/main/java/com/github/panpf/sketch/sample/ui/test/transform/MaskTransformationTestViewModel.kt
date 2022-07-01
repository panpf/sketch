package com.github.panpf.sketch.sample.ui.test.transform

import android.app.Application
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel

class MaskTransformationTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val maskColorData = MutableLiveData(ColorUtils.setAlphaComponent(Color.RED, 128))

    fun changeMaskColor(color: Int) {
        maskColorData.postValue(color)
    }
}