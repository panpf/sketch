package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState.Loading
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.components.VerticalScrollbarCompat
import com.github.panpf.sketch.sample.ui.list.AppendState
import com.github.panpf.sketch.sample.ui.model.LayoutMode
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.util.ignoreFirst
import kotlinx.coroutines.flow.Flow

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun PhotoGrid(
    photoPagingFlow: Flow<PagingData<Photo>>,
    animatedPlaceholder: Boolean,
    gridCellsMinSize: Dp = 100.dp,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
    onLongClick: (items: List<Photo>, photo: Photo, index: Int, displayResult: ImageResult) -> Unit,
) {
    val pagingItems = photoPagingFlow.collectAsLazyPagingItems()
    val context = LocalPlatformContext.current
    val appSettingsService = context.appSettings
    LaunchedEffect(Unit) {
        appSettingsService.ignoreExifOrientation.ignoreFirst().collect {
            // PhotoPagingSource needs to calculate the width and height of the image based on exif information, so it needs to be refreshed
            pagingItems.refresh()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = pagingItems.loadState.refresh is Loading,
        onRefresh = { pagingItems.refresh() }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        val photoListLayoutMode by appSettingsService.photoListLayoutMode.collectAsState()
        if (LayoutMode.valueOf(photoListLayoutMode) == LayoutMode.GRID) {
            PhotoSquareGrid(
                pagingItems = pagingItems,
                animatedPlaceholder = animatedPlaceholder,
                gridCellsMinSize = gridCellsMinSize,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        } else {
            PhotoStaggeredGrid(
                pagingItems = pagingItems,
                animatedPlaceholder = animatedPlaceholder,
                gridCellsMinSize = gridCellsMinSize,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        }

        PullRefreshIndicator(
            refreshing = pagingItems.loadState.refresh is Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun PhotoSquareGrid(
    pagingItems: LazyPagingItems<Photo>,
    animatedPlaceholder: Boolean,
    gridCellsMinSize: Dp,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
    onLongClick: (items: List<Photo>, photo: Photo, index: Int, displayResult: ImageResult) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        val gridState = rememberLazyGridState()
        LaunchedEffect(gridState.isScrollInProgress) {
            PauseLoadWhenScrollingDecodeInterceptor.scrolling =
                gridState.isScrollInProgress
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(gridCellsMinSize),
            state = gridState,
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                count = pagingItems.itemCount,
                key = { pagingItems.peek(it)?.originalUrl ?: "" },
                contentType = { 1 }
            ) { index ->
                val item = pagingItems[index]
                item?.let {
                    PhotoGridItem(
                        index = index,
                        photo = it,
                        animatedPlaceholder = animatedPlaceholder,
                        staggeredGridMode = false,
                        onClick = { photo, index ->
                            onClick(pagingItems.itemSnapshotList.items, photo, index)
                        },
                        onLongClick = { photo, index, imageResult ->
                            onLongClick(
                                pagingItems.itemSnapshotList.items,
                                photo,
                                index,
                                imageResult
                            )
                        }
                    )
                }
            }

            if (pagingItems.itemCount > 0) {
                item(
                    key = "AppendState",
                    span = { GridItemSpan(this.maxLineSpan) },
                    contentType = 2
                ) {
                    AppendState(pagingItems.loadState.append) {
                        pagingItems.retry()
                    }
                }
            }
        }

        VerticalScrollbarCompat(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(10.dp),
            gridState = gridState
        )
    }
}

@Composable
private fun PhotoStaggeredGrid(
    pagingItems: LazyPagingItems<Photo>,
    animatedPlaceholder: Boolean,
    gridCellsMinSize: Dp,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
    onLongClick: (items: List<Photo>, photo: Photo, index: Int, displayResult: ImageResult) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        val gridState = rememberLazyStaggeredGridState()
        LaunchedEffect(gridState.isScrollInProgress) {
            PauseLoadWhenScrollingDecodeInterceptor.scrolling =
                gridState.isScrollInProgress
        }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = gridCellsMinSize),
            state = gridState,
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalItemSpacing = 4.dp,
        ) {
            items(
                count = pagingItems.itemCount,
                key = { pagingItems.peek(it)?.originalUrl ?: "" },
                contentType = { 1 }
            ) { index ->
                val item = pagingItems[index]
                item?.let {
                    PhotoGridItem(
                        index = index,
                        photo = it,
                        animatedPlaceholder = animatedPlaceholder,
                        staggeredGridMode = true,
                        onClick = { photo, index ->
                            onClick(pagingItems.itemSnapshotList.items, photo, index)
                        },
                        onLongClick = { photo, index, imageResult ->
                            onLongClick(
                                pagingItems.itemSnapshotList.items,
                                photo,
                                index,
                                imageResult
                            )
                        }
                    )
                }
            }

            if (pagingItems.itemCount > 0) {
                item(
                    key = "AppendState",
                    span = StaggeredGridItemSpan.FullLine,
                    contentType = 2
                ) {
                    AppendState(pagingItems.loadState.append) {
                        pagingItems.retry()
                    }
                }
            }
        }

        // TODO VerticalScrollbar is not yet supported in StaggeredGrid
//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(
//                scrollState = gridState
//            )
//        )
    }
}