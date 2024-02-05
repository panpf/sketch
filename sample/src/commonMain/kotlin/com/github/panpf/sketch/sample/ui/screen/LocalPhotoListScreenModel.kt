package com.github.panpf.sketch.sample.ui.screen

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.data.paging.LocalPhotoListPagingSource

class LocalPhotoListScreenModel(context: PlatformContext, sketch: Sketch) : ScreenModel {

    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            LocalPhotoListPagingSource(context, sketch)
        }
    ).flow.cachedIn(screenModelScope)
}