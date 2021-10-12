package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import me.panpf.sketch.sample.ds.UnsplashImageListPagingSource

class UnsplashImageListViewModel(application: Application) : AndroidViewModel(application) {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        initialKey = 0,
        pagingSourceFactory = {
            UnsplashImageListPagingSource(application)
        }
    ).flow.cachedIn(viewModelScope)
}