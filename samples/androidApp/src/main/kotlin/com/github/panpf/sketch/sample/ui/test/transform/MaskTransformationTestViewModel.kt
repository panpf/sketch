package com.github.panpf.sketch.sample.ui.test.transform

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MaskTransformationTestViewModel : ViewModel() {

    private val _maskColorData = MutableStateFlow(MaskColor.RED)
    val maskColorData: StateFlow<MaskColor> = _maskColorData

    fun changeMaskColor(maskColor: MaskColor) {
        _maskColorData.value = maskColor
    }

    enum class MaskColor(val colorInt: Int) {
        RED(ColorUtils.setAlphaComponent(Color.RED, 128)),
        GREEN(ColorUtils.setAlphaComponent(Color.GREEN, 128)),
        BLUE(ColorUtils.setAlphaComponent(Color.BLUE, 128))
    }
}