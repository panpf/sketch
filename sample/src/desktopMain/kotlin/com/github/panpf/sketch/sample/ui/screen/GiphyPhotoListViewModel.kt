package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import app.cash.paging.PagingData
import com.github.panpf.sketch.sample.ui.screen.base.BaseRememberObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Composable
fun rememberGiphyPhotoListViewModel(): GiphyPhotoListViewModel {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        GiphyPhotoListViewModel(coroutineScope)
    }
}

class GiphyPhotoListViewModel(private val coroutineScope: CoroutineScope) : BaseRememberObserver() {

    val pagingFlow: Flow<PagingData<Photo>> = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            GiphyPhotoListPagingSource()
        }
    ).flow.cachedIn(coroutineScope)

    override fun onFirstRemembered() {

    }

    override fun onLastRemembered() {

    }
}