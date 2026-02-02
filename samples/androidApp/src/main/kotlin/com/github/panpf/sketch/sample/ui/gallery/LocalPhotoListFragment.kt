package com.github.panpf.sketch.sample.ui.gallery

import androidx.paging.PagingData
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocalPhotoListFragment : BasePhotoListFragment() {

    override val permission: String = "android.Manifest.permission.READ_EXTERNAL_STORAGE"

    private val localPhotoListViewModel by viewModel<LocalPhotoListViewModel>()

    override val animatedPlaceholder: Boolean
        get() = false

    override val photoPagingFlow: Flow<PagingData<Photo>>
        get() = localPhotoListViewModel.pagingFlow

}