package com.github.panpf.sketch.sample.ui.test.transform

import androidx.lifecycle.ViewModel
import com.github.panpf.sketch.resize.Scale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CircleCropTransformationTestViewModel() : ViewModel() {

    private val _scaleData = MutableStateFlow(Scale.CENTER_CROP)
    val scaleData: StateFlow<Scale> = _scaleData

    fun changeScale(scale: Scale) {
        _scaleData.value = scale
    }
}