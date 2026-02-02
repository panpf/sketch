package com.github.panpf.sketch.sample.ui.test

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class LocalVideoListViewModel(application: Application) : AndroidViewModel(application) {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 80,
            prefetchDistance = 10,
            enablePlaceholders = false,
            initialLoadSize = 40
        ),
        initialKey = 0,
        pagingSourceFactory = {
            LocalVideoListPagingSource(application)
        }
    ).flow.cachedIn(viewModelScope)
}