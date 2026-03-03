package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FetcherTestViewModel(context: PlatformContext) : ViewModel() {

    private val _data = MutableStateFlow<List<PhotoTestItem>>(emptyList())
    val data: StateFlow<List<PhotoTestItem>> = _data

    init {
        viewModelScope.launch {
            _data.value = buildFetcherTestItems(context, fromCompose = false)
        }
    }
}

expect suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean = true
): List<PhotoTestItem>