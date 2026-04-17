package com.github.panpf.sketch.sample.ui.test.transform

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MultiTransformationTestViewModel : ViewModel() {

    private val _blurRadiusData = MutableStateFlow(30)
    val blurRadiusData: StateFlow<Int> = _blurRadiusData

    private val _maskColorData =
        MutableStateFlow<Int?>(ColorUtils.setAlphaComponent(Color.RED, 128))
    val maskColorData: StateFlow<Int?> = _maskColorData

    private val _roundedCornersRadiusData = MutableStateFlow(30)
    val roundedCornersRadiusData: StateFlow<Int> = _roundedCornersRadiusData

    private val _rotateData = MutableStateFlow(45)
    val rotateData: StateFlow<Int> = _rotateData

    fun changeRotate(rotate: Int) {
        _rotateData.value = rotate
    }

    fun changeRoundedCornersRadius(radius: Int) {
        _roundedCornersRadiusData.value = radius
    }

    fun changeBlurRadius(radius: Int) {
        _blurRadiusData.value = radius
    }

    fun changeMaskColor(color: Int?) {
        _maskColorData.value = color
    }
}