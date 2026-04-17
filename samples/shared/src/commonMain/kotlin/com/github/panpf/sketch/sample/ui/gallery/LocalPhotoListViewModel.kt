package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.ui.common.gridPagingConfig

class LocalPhotoListViewModel(val sketch: Sketch) : ViewModel() {

    val pagingFlow = Pager(
        config = gridPagingConfig,
        initialKey = 0,
        pagingSourceFactory = {
            LocalPhotoListPagingSource(sketch)
        }
    ).flow.cachedIn(viewModelScope)
}