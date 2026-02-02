package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.ability.bindPauseLoadWhenScrolling
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.common.AppendState
import com.github.panpf.sketch.sample.ui.common.PageState
import com.github.panpf.sketch.sample.ui.components.VerticalScrollbarCompat
import com.github.panpf.sketch.sample.ui.model.Photo
import org.koin.compose.koinInject

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun PhotoList(
    photoPaging: SimplePaging<Photo>,
    animatedPlaceholder: Boolean,
    modifier: Modifier = Modifier,
    gridCellsMinSize: Dp = 100.dp,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = photoPaging.refreshing,
        onRefresh = { photoPaging.refresh() }
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        val appSettings: AppSettings = koinInject()
        val staggeredGridMode by appSettings.staggeredGridMode.collectAsState()
        if (staggeredGridMode) {
            val staggeredGridState = rememberLazyStaggeredGridState()
            photoPaging.nextPageWith(staggeredGridState)
            PhotoStaggeredGrid(
                staggeredGridState = staggeredGridState,
                photos = photoPaging.photos,
                animatedPlaceholder = animatedPlaceholder,
                gridCellsMinSize = gridCellsMinSize,
                appendState = photoPaging.appendState,
                onClick = onClick,
            )
        } else {
            val gridState = rememberLazyGridState()
            photoPaging.nextPageWith(gridState)
            PhotoSquareGrid(
                gridState = gridState,
                photos = photoPaging.photos,
                animatedPlaceholder = animatedPlaceholder,
                gridCellsMinSize = gridCellsMinSize,
                appendState = photoPaging.appendState,
                onClick = onClick,
            )
        }

        PullRefreshIndicator(
            refreshing = photoPaging.refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        PageState(photoPaging.pageState)
    }
}

@Composable
private fun PhotoSquareGrid(
    gridState: LazyGridState,
    photos: List<Photo>,
    animatedPlaceholder: Boolean,
    gridCellsMinSize: Dp,
    appendState: AppendState?,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        bindPauseLoadWhenScrolling(gridState)

        LazyVerticalGrid(
            columns = GridCells.Adaptive(gridCellsMinSize),
            state = gridState,
            contentPadding = PaddingValues(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 84.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                count = photos.size,
                key = { "${photos[it].originalUrl}:${it}" },
                contentType = { 1 }
            ) { index ->
                val item = photos[index]
                PhotoGridItem(
                    index = index,
                    photo = item,
                    animatedPlaceholder = animatedPlaceholder,
                    staggeredGridMode = false,
                    onClick = { photo, index1 ->
                        onClick(photos, photo, index1)
                    },
                )
            }

            if (appendState != null) {
                item(
                    key = "AppendState",
                    span = { GridItemSpan(this.maxLineSpan) },
                    contentType = 2
                ) {
                    AppendState(appendState)
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
    staggeredGridState: LazyStaggeredGridState,
    photos: List<Photo>,
    animatedPlaceholder: Boolean,
    gridCellsMinSize: Dp,
    appendState: AppendState?,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        bindPauseLoadWhenScrolling(staggeredGridState)

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = gridCellsMinSize),
            state = staggeredGridState,
            contentPadding = PaddingValues(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 84.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalItemSpacing = 4.dp,
        ) {
            items(
                count = photos.size,
                key = { "${photos[it].originalUrl}:${it}" },
                contentType = { 1 }
            ) { index ->
                val item = photos[index]
                PhotoGridItem(
                    index = index,
                    photo = item,
                    animatedPlaceholder = animatedPlaceholder,
                    staggeredGridMode = true,
                    onClick = { photo, index1 ->
                        onClick(photos, photo, index1)
                    },
                )
            }

            if (appendState != null) {
                item(
                    key = "AppendState",
                    span = StaggeredGridItemSpan.FullLine,
                    contentType = 2
                ) {
                    AppendState(appendState)
                }
            }
        }

        // VerticalScrollbar is not yet supported in StaggeredGrid
//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(
//                scrollState = gridState
//            )
//        )
    }
}