package com.github.panpf.sketch.sample.ui.test.transform

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RotateTransformationTestViewModel : ViewModel() {

    private val _rotateData = MutableStateFlow(45)
    val rotateData: StateFlow<Int> = _rotateData

    fun changeRotate(rotate: Int) {
        _rotateData.value = rotate
    }
}