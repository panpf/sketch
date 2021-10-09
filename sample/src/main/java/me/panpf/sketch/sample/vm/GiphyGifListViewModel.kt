package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import me.panpf.sketch.sample.ds.GiphyGifListPagingSource

class GiphyGifListViewModel(application: Application) : AndroidViewModel(application) {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 40,
            prefetchDistance = 20,
            enablePlaceholders = false,
            initialLoadSize = 40
        ),
        initialKey = 0,
        pagingSourceFactory = {
            GiphyGifListPagingSource(application)
        }
    ).flow.cachedIn(viewModelScope)
}