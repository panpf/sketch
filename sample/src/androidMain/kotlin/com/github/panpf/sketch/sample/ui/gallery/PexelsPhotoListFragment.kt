package com.github.panpf.sketch.sample.ui.gallery

import androidx.paging.PagingData
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.viewmodel.ext.android.viewModel

class PexelsPhotoListFragment : BasePhotoListFragment() {

    private val pexelsImageListViewModel by viewModel<PexelsPhotoListViewModel>()

    override val animatedPlaceholder: Boolean
        get() = false

    override val photoPagingFlow: Flow<PagingData<Photo>>
        get() = pexelsImageListViewModel.pagingFlow

}