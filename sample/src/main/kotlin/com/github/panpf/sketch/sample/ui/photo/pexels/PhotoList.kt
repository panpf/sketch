/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.ui.photo.pexels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDrawableDecodeInterceptor
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.LayoutMode
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.common.compose.AppendState
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotoList(
    photoPagingFlow: Flow<PagingData<Photo>>,
    animatedPlaceholder: Boolean = false,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit
) {
    val appSettingsService = LocalContext.current.appSettingsService
    val lazyPagingItems = photoPagingFlow.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        appSettingsService.ignoreExifOrientation.ignoreFirst().collect {
            lazyPagingItems.refresh()
        }
    }
    SwipeRefresh(
        state = SwipeRefreshState(lazyPagingItems.loadState.refresh is LoadState.Loading),
        onRefresh = { lazyPagingItems.refresh() }
    ) {
        val photoListLayoutMode by appSettingsService.photoListLayoutMode.collectAsState()
        if (LayoutMode.valueOf(photoListLayoutMode) == LayoutMode.GRID) {
            PhotoGrid(lazyPagingItems, animatedPlaceholder, onClick)
        } else {
            PhotoStaggeredGrid(lazyPagingItems, animatedPlaceholder, onClick)
        }
    }
}

@Composable
private fun PhotoGrid(
    lazyPagingItems: LazyPagingItems<Photo>,
    animatedPlaceholder: Boolean,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit
) {
    val lazyGridState = rememberLazyGridState()
    LaunchedEffect(lazyGridState.isScrollInProgress) {
        PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling =
            lazyGridState.isScrollInProgress
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        state = lazyGridState,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.grid_divider)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = { lazyPagingItems.peek(it)?.diffKey ?: "" },
            contentType = { 1 }
        ) { index ->
            val item = lazyPagingItems[index]
            item?.let {
                PhotoGridItem(
                    index = index,
                    photo = it,
                    animatedPlaceholder = animatedPlaceholder,
                    staggeredGridMode = false
                ) { photo, index ->
                    onClick(lazyPagingItems.itemSnapshotList.items, photo, index)
                }
            }
        }

        if (lazyPagingItems.itemCount > 0) {
            item(
                key = "AppendState",
                span = { GridItemSpan(this.maxLineSpan) },
                contentType = 2
            ) {
                AppendState(lazyPagingItems.loadState.append) {
                    lazyPagingItems.retry()
                }
            }
        }
    }
}

@Composable
private fun PhotoStaggeredGrid(
    lazyPagingItems: LazyPagingItems<Photo>,
    animatedPlaceholder: Boolean,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit
) {
    val lazyGridState = rememberLazyStaggeredGridState()
    LaunchedEffect(lazyGridState.isScrollInProgress) {
        PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling =
            lazyGridState.isScrollInProgress
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 100.dp),
        state = lazyGridState,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.grid_divider)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
        verticalItemSpacing = dimensionResource(id = R.dimen.grid_divider),
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = { lazyPagingItems.peek(it)?.diffKey ?: "" },
            contentType = { 1 }
        ) { index ->
            val item = lazyPagingItems[index]
            item?.let {
                PhotoGridItem(
                    index = index,
                    photo = it,
                    animatedPlaceholder = animatedPlaceholder,
                    staggeredGridMode = true
                ) { photo, index ->
                    onClick(lazyPagingItems.itemSnapshotList.items, photo, index)
                }
            }
        }

        if (lazyPagingItems.itemCount > 0) {
            item(
                key = "AppendState",
                span = StaggeredGridItemSpan.FullLine,
                contentType = 2
            ) {
                AppendState(lazyPagingItems.loadState.append) {
                    lazyPagingItems.retry()
                }
            }
        }
    }
}