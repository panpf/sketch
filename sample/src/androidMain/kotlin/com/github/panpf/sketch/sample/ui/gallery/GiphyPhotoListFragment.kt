package com.github.panpf.sketch.sample.ui.gallery

import android.app.Application
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow

class GiphyPhotoListFragment : BasePhotoListFragment() {

    private val giphyPhotoListViewModel by viewModels<GiphyPhotoListViewModel>()

    override val animatedPlaceholder: Boolean
        get() = true

    override val photoPagingFlow: Flow<PagingData<Photo>>
        get() = giphyPhotoListViewModel.pagingFlow

    class GiphyPhotoListViewModel(application: Application) : AndroidViewModel(application) {
        val pagingFlow = Pager(
            config = PagingConfig(
                pageSize = 40,
                enablePlaceholders = false,
            ),
            initialKey = 0,
            pagingSourceFactory = {
                GiphyPhotoListPagingSource()
            }
        ).flow.cachedIn(viewModelScope)
    }
}