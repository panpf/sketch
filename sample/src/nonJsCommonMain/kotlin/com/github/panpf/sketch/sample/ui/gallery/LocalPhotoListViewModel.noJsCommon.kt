package com.github.panpf.sketch.sample.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.panpf.sketch.Sketch

actual class LocalPhotoListViewModel actual constructor(val sketch: Sketch) : ViewModel() {

    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 60,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            LocalPhotoListPagingSource(sketch)
        }
    ).flow.cachedIn(viewModelScope)
}