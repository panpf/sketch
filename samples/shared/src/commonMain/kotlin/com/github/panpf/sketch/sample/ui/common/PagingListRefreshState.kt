package com.github.panpf.sketch.sample.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun PagingListRefreshState(
    pagingItems: LazyPagingItems<*>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val pageState by remember {
        derivedStateOf {
            val refreshState = pagingItems.loadState.refresh
            if (refreshState is LoadState.Error) {
                PageState.Error(refreshState.error.message) {
                    pagingItems.refresh()
                }
            } else {
                null
            }
        }
    }
    PageState(pageState = pageState, modifier = modifier)
}