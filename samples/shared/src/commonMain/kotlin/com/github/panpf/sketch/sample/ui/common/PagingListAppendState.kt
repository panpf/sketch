package com.github.panpf.sketch.sample.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun PagingListAppendState(pagingItems: LazyPagingItems<*>) {
    val state by remember {
        derivedStateOf {
            when (val loadState = pagingItems.loadState.append) {
                is LoadState.Loading -> AppendState.Loading
                is LoadState.Error -> AppendState.Error { pagingItems.retry() }
                is LoadState.NotLoading -> if (loadState.endOfPaginationReached) AppendState.End else null
            }
        }
    }
    AppendState(state)
}