package com.github.panpf.sketch.sample.ui.gallery

import android.app.Application
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow
import org.koin.mp.KoinPlatform

class LocalPhotoListFragment : BasePhotoListFragment() {

    override val permission: String = "android.Manifest.permission.READ_EXTERNAL_STORAGE"

    private val localPhotoListViewModel by viewModels<LocalPhotoListViewModel>()

    override val animatedPlaceholder: Boolean
        get() = false

    override val photoPagingFlow: Flow<PagingData<Photo>>
        get() = localPhotoListViewModel.pagingFlow

    class LocalPhotoListViewModel(application: Application) : AndroidViewModel(application) {

        private val sketch: Sketch = KoinPlatform.getKoin().get<Sketch>()

        val pagingFlow = Pager(
            config = PagingConfig(
                pageSize = 60,
                enablePlaceholders = false,
            ),
            initialKey = 0,
            pagingSourceFactory = {
                LocalPhotoListPagingSource(application, sketch)
            }
        ).flow.cachedIn(viewModelScope)
    }
}