package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.panpf.sketch.sample.ui.screen.base.BaseRememberObserver
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberLocalPhotoListViewModel(): LocalPhotoListViewModel {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        LocalPhotoListViewModel(coroutineScope)
    }
}

class LocalPhotoListViewModel(private val coroutineScope: CoroutineScope) : BaseRememberObserver() {

    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            LocalPhotoListPagingSource()
        }
    ).flow.cachedIn(coroutineScope)

    override fun onFirstRemembered() {

    }

    override fun onLastRemembered() {

    }
}