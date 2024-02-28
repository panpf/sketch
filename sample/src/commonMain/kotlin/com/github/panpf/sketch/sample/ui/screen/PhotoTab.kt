package com.github.panpf.sketch.sample.ui.screen

import app.cash.paging.PagingData
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow

interface MyTab {
    val title: String
}

class PhotoTab(
    override val title: String,
    val animatedPlaceholder: Boolean,
    val photoPagingFlow: Flow<PagingData<Photo>>
) : MyTab

object TestTab : MyTab {
    override val title: String = "Test"
}