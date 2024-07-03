package com.github.panpf.sketch.sample.ui.gallery

import android.app.Application
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerRefreshBinding
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.flow.Flow

class LocalPhotoListViewFragment : BasePhotoListViewFragment() {

    private val localPhotoListViewModel by viewModels<LocalPhotoListViewModel>()

    override val animatedPlaceholder: Boolean
        get() = false

    override val photoPagingFlow: Flow<PagingData<Photo>>
        get() = localPhotoListViewModel.pagingFlow

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            super.onViewCreated(binding!!, null)
        }

    override fun onViewCreated(
        binding: FragmentRecyclerRefreshBinding,
        savedInstanceState: Bundle?
    ) {
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    class LocalPhotoListViewModel(application: Application) : AndroidViewModel(application) {
        val pagingFlow = Pager(
            config = PagingConfig(
                pageSize = 60,
                enablePlaceholders = false,
            ),
            initialKey = 0,
            pagingSourceFactory = {
                LocalPhotoListPagingSource(application, application.sketch)
            }
        ).flow.cachedIn(viewModelScope)
    }
}