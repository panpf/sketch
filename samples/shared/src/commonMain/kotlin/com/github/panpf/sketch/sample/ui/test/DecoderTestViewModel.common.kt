package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.PlatformContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DecoderTestViewModel(context: PlatformContext) : ViewModel() {

    private val _data = MutableStateFlow<List<DecoderTestItem>>(emptyList())
    val data: StateFlow<List<DecoderTestItem>> = _data

    init {
        viewModelScope.launch {
            _data.value = buildDecoderTestItems(context)
        }
    }
}

expect suspend fun buildDecoderTestItems(context: PlatformContext): List<DecoderTestItem>