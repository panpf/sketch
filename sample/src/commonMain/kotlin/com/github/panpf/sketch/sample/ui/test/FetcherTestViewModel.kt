package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.PlatformContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FetcherTestViewModel(context: PlatformContext) : ViewModel() {

    private val _data = MutableStateFlow<List<FetcherTestItem>>(emptyList())
    val data: StateFlow<List<FetcherTestItem>> = _data

    init {
        viewModelScope.launch {
            _data.value = buildFetcherTestItems(context, fromCompose = false)
        }
    }
}