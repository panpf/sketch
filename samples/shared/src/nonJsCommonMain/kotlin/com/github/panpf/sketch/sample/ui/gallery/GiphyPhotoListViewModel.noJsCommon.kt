package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.panpf.sketch.sample.data.api.giphy.GiphyApi

actual class GiphyPhotoListViewModel actual constructor(val giphyApi: GiphyApi) : ViewModel() {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            GiphyPhotoListPagingSource(this@GiphyPhotoListViewModel.giphyApi)
        }
    ).flow.cachedIn(viewModelScope)
}