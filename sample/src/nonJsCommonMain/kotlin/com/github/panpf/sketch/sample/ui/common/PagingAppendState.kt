package com.github.panpf.sketch.sample.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.cash.paging.LoadState
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading

@Composable
fun PagingAppendState(loadState: LoadState, onClick: () -> Unit) {
    val state by remember {
        derivedStateOf {
            when (loadState) {
                is LoadStateLoading -> AppendState.Loading
                is LoadStateError -> AppendState.Error(onClick)
                is LoadStateNotLoading -> if (loadState.endOfPaginationReached) AppendState.End else null
                else -> null
            }
        }
    }
    AppendState(state)
}