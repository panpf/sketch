package com.github.panpf.sketch.sample.ui.test.insanity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class InsanityTestViewModel(application: Application) : AndroidViewModel(application) {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 80,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            InsanityTestPagingSource(application)
        }
    ).flow.cachedIn(viewModelScope)
}