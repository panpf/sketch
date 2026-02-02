package com.github.panpf.sketch.sample.ui.gallery

import androidx.paging.PagingData
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.viewmodel.ext.android.viewModel

class GiphyPhotoListFragment : BasePhotoListFragment() {

    private val giphyPhotoListViewModel by viewModel<GiphyPhotoListViewModel>()

    override val animatedPlaceholder: Boolean
        get() = true

    override val photoPagingFlow: Flow<PagingData<Photo>>
        get() = giphyPhotoListViewModel.pagingFlow

}