package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.github.panpf.sketch.sample.data.api.pexels.PexelsApi
import com.github.panpf.sketch.sample.ui.common.gridPagingConfig

class PexelsPhotoListViewModel(val pexelsApi: PexelsApi) : ViewModel() {
    val pagingFlow = Pager(
        config = gridPagingConfig,
        initialKey = 0,
        pagingSourceFactory = {
            PexelsPhotoListPagingSource(pexelsApi)
        }
    ).flow.cachedIn(viewModelScope)
}