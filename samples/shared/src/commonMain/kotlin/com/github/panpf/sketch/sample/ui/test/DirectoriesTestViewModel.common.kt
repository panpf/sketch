package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.zoomimage.util.ioCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DirectoriesTestViewModel(private val context: PlatformContext) : ViewModel() {

    private val _data = MutableStateFlow<List<DirectoryItem>>(emptyList())
    val data: StateFlow<List<DirectoryItem>> = _data

    init {
        viewModelScope.launch(ioCoroutineDispatcher()) {
            val macosDirectoryItems = buildPlatformDirectoryItemList(context)
            _data.emit(macosDirectoryItems)
        }
    }
}

expect fun buildPlatformDirectoryItemList(context: PlatformContext): List<DirectoryItem>