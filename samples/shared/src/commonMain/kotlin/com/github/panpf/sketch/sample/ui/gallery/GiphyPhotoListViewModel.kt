package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.data.api.giphy.GiphyApi
import com.github.panpf.sketch.sample.ui.common.gridPagingConfig

class GiphyPhotoListViewModel(val context: PlatformContext, val giphyApi: GiphyApi) : ViewModel() {
    val pagingFlow = Pager(
        config = gridPagingConfig,
        initialKey = 0,
        pagingSourceFactory = {
            GiphyPhotoListPagingSource(context, this@GiphyPhotoListViewModel.giphyApi)
        }
    ).flow.cachedIn(viewModelScope)
}