package com.github.panpf.sketch.sample.ui.test

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.toBitmap
import com.github.panpf.sketch.util.toNSData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.UIKit.UIImage

class UIImageTestViewModel : ViewModel() {
    private val _originBitmapFlow = MutableStateFlow<ImageBitmap?>(null)
    val originBitmapFlow = _originBitmapFlow.asStateFlow()

    private val _subsamplingBitmapFlow = MutableStateFlow<ImageBitmap?>(null)
    val subsamplingBitmapFlow = _subsamplingBitmapFlow.asStateFlow()

    private val _sampleSizeFlow = MutableStateFlow(1)
    val sampleSizeFlow = _sampleSizeFlow.asStateFlow()

    private val _regionFlow = MutableStateFlow<Rect?>(null)
    val regionFlow = _regionFlow.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val data = ComposeResImageFiles.jpeg
                .toDataSource(PlatformContext.INSTANCE)
                .toByteArray()
            val bitmap = data.decodeToImageBitmap()
            _originBitmapFlow.value = bitmap
        }
    }

    private var lastJob: Job? = null

    fun updateRect(newRect: Rect) {
        _regionFlow.value = newRect
        launchJob()
    }

    fun updateSampleSize(newSampleSize: Int) {
        _sampleSizeFlow.value = newSampleSize
        launchJob()
    }

    private fun launchJob() {
        val rect = _regionFlow.value ?: return
        lastJob?.cancel()
        lastJob = viewModelScope.launch(Dispatchers.IO) {
            val data = ComposeResImageFiles.jpeg
                .toDataSource(PlatformContext.INSTANCE)
                .toByteArray()
            val uiImage = UIImage.imageWithData(data.toNSData())!!
            val bitmap = uiImage.toBitmap(sampleSize = _sampleSizeFlow.value, cropRect = rect)
            _subsamplingBitmapFlow.value = bitmap.asComposeImageBitmap()
        }
    }
}