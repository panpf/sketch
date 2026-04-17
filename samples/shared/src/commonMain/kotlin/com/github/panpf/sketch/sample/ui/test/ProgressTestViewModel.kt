package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.ComposeResHttpStack
import com.github.panpf.sketch.images.ComposeResHttpUriFetcher
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.toComposeResHttpUri
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProgressTestViewModel(val context: PlatformContext) : ViewModel() {

    private val _data = MutableStateFlow<List<PhotoTestItem>>(emptyList())
    val data: StateFlow<List<PhotoTestItem>> = _data

    init {
        val httpStack = ComposeResHttpStack(context)
        viewModelScope.launch {
            _data.value = ComposeResImageFiles.values.map {
                PhotoTestItem(
                    photoUri = it.toComposeResHttpUri(),
                    imageFetcher = ComposeResHttpUriFetcher.Factory(httpStack)
                )
            }
        }
    }
}