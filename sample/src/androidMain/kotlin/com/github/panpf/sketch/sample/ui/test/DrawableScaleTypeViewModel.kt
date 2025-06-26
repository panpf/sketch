package com.github.panpf.sketch.sample.ui.test

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DrawableScaleTypeViewModel : ViewModel() {

    private val _scaleTypeState = MutableStateFlow(ImageView.ScaleType.FIT_CENTER)
    val scaleTypeState: StateFlow<ImageView.ScaleType> = _scaleTypeState

    fun setScaleType(scaleType: ImageView.ScaleType) {
        _scaleTypeState.value = scaleType
    }
}