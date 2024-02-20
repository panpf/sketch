package com.github.panpf.sketch.sample.ui.screen

import app.cash.paging.PagingData
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow

class PhotoTab(
    val title: String,
    val animatedPlaceholder: Boolean,
    val photoPagingFlow: Flow<PagingData<Photo>>
)