package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.github.panpf.sketch.sample.ui.common.AppendState
import com.github.panpf.sketch.sample.ui.common.PageState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SimplePaging<T>(
    scope: CoroutineScope,
    initialPageStart: Int,
    pageSize: Int = 80,
    loadData: suspend (pageStart: Int, pageSize: Int) -> List<T>,
    calculateNextPageStart: (currentPageStart: Int, loadedPhotoSize: Int) -> Int,
) {

    var photos: List<T> by mutableStateOf(emptyList())
        private set
    var pageStart: Int by mutableStateOf(initialPageStart)
        private set
    var nextPageStart: Int? by mutableStateOf(null)
        private set
    var refreshing: Boolean by mutableStateOf(false)
        private set
    var appendState: AppendState? by mutableStateOf(null)
        private set
    var pageState: PageState? by mutableStateOf(null)
        private set

    init {
        scope.launch {
            snapshotFlow { pageStart }.collectLatest {
                val finalPageStart = it.takeIf { it >= 0 } ?: initialPageStart  // refresh
                refreshing = finalPageStart == initialPageStart
                appendState =
                    if (finalPageStart > initialPageStart) AppendState.Loading else null
                val listResult = runCatching { loadData(finalPageStart, pageSize) }
                if (listResult.isSuccess) {
                    val loadedPhotos = listResult.getOrThrow()
                    photos = if (it == initialPageStart) {
                        loadedPhotos
                    } else {
                        photos + loadedPhotos
                    }
                    nextPageStart = calculateNextPageStart(it, loadedPhotos.size)
                    appendState =
                        if (finalPageStart > initialPageStart && loadedPhotos.isEmpty())
                            AppendState.End else null
                    pageState = if (refreshing && photos.isEmpty()) PageState.Empty() else null
                } else {
                    appendState = if (!refreshing) AppendState.Error() else null
                    pageState =
                        if (refreshing) PageState.Error(listResult.exceptionOrNull()?.message) else null
                }
                refreshing = false
            }
        }
    }

    fun refresh() {
        pageStart = -1
    }

    fun nextPage() {
        val nextPageStart = nextPageStart
        if (nextPageStart != null) {
            pageStart = nextPageStart
        }
    }

    @Composable
    fun nextPageWith(gridState: LazyStaggeredGridState) {
        LaunchedEffect(Unit) {
            snapshotFlow { gridState.layoutInfo }.collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo
                    .takeIf { it.isNotEmpty() }?.last()?.index ?: -1
                if (!refreshing
                    && appendState !is AppendState.Loading
                    && nextPageStart != null
                    && lastVisibleItemIndex >= 0
                    && lastVisibleItemIndex == layoutInfo.totalItemsCount - 1
                ) {
                    nextPage()
                }
            }
        }
    }

    @Composable
    fun nextPageWith(gridState: LazyGridState) {
        LaunchedEffect(Unit) {
            snapshotFlow { gridState.layoutInfo }.collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo
                    .takeIf { it.isNotEmpty() }?.last()?.index ?: -1
                if (!refreshing
                    && appendState !is AppendState.Loading
                    && nextPageStart != null
                    && lastVisibleItemIndex >= 0
                    && lastVisibleItemIndex == layoutInfo.totalItemsCount - 1
                ) {
                    nextPage()
                }
            }
        }
    }
}