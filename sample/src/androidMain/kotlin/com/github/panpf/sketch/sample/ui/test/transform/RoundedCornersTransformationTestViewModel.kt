package com.github.panpf.sketch.sample.ui.test.transform

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RoundedCornersTransformationTestViewModel() : ViewModel() {

    private val _topLeftRadiusData = MutableStateFlow(10)
    val topLeftRadiusData: StateFlow<Int> = _topLeftRadiusData
    private val _topRightRadiusData = MutableStateFlow(20)
    val topRightRadiusData: StateFlow<Int> = _topRightRadiusData
    private val _bottomLeftRadiusData = MutableStateFlow(40)
    val bottomLeftRadiusData: StateFlow<Int> = _bottomLeftRadiusData
    private val _bottomRightRadiusData = MutableStateFlow(80)
    val bottomRightRadiusData: StateFlow<Int> = _bottomRightRadiusData

    fun changeTopLeftRadius(radius: Int) {
        _topLeftRadiusData.value = radius
    }

    fun changeTopRightRadius(radius: Int) {
        _topRightRadiusData.value = radius
    }

    fun changeBottomLeftRadius(radius: Int) {
        _bottomLeftRadiusData.value = radius
    }

    fun changeBottomRightRadius(radius: Int) {
        _bottomRightRadiusData.value = radius
    }
}