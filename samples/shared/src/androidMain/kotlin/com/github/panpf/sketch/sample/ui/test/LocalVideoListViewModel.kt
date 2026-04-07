package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.panpf.sketch.PlatformContext

class LocalVideoListViewModel(context: PlatformContext) : ViewModel() {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 40,
            prefetchDistance = 10,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            LocalVideoListPagingSource(context)
        }
    ).flow.cachedIn(viewModelScope)
}