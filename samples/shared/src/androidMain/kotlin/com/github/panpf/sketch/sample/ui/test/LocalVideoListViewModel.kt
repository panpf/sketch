package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.common.listPagingConfig

class LocalVideoListViewModel(context: PlatformContext) : ViewModel() {
    val pagingFlow = Pager(
        config = listPagingConfig,
        initialKey = 0,
        pagingSourceFactory = {
            LocalVideoListPagingSource(context)
        }
    ).flow.cachedIn(viewModelScope)
}