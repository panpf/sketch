package com.github.panpf.sketch.compose.sample.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems


@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> LazyGridScope.itemsIndexed(
    items: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    items(
        count = items.itemCount,
    ) { index ->
        itemContent(index, items[index])
    }
}