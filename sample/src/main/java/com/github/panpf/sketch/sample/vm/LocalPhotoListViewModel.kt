package com.github.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.panpf.sketch.sample.ds.LocalPhotoListPagingSource

class LocalPhotoListViewModel(application: Application) : AndroidViewModel(application) {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 40,
            prefetchDistance = 20,
            enablePlaceholders = false,
            initialLoadSize = 80
        ),
        initialKey = 0,
        pagingSourceFactory = {
            LocalPhotoListPagingSource(application)
        }
    ).flow.cachedIn(viewModelScope)
}