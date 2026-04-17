package com.github.panpf.sketch.sample.ui.test.transform

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BlurTransformationTestViewModel : ViewModel() {

    private val _radiusData = MutableStateFlow(30)
    val radiusData: StateFlow<Int> = _radiusData
    private val _maskColorData = MutableStateFlow<Int?>(null)
    val maskColorData: StateFlow<Int?> = _maskColorData
    private val _backgroundColorData = MutableStateFlow<Int?>(null)
    val backgroundColorData: StateFlow<Int?> = _backgroundColorData

    fun changeRadius(radius: Int) {
        _radiusData.value = radius
    }

    fun changeMaskColor(color: Int?) {
        _maskColorData.value = color
    }

    fun changeBackgroundColor(color: Int?) {
        _backgroundColorData.value = color
    }
}