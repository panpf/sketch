///*
// * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.github.panpf.sketch.sample.ui.gallery
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.GridItemSpan
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.rememberLazyGridState
//import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
//import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
//import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
//import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.pullrefresh.PullRefreshIndicator
//import androidx.compose.material.pullrefresh.pullRefresh
//import androidx.compose.material.pullrefresh.rememberPullRefreshState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.unit.dp
//import androidx.paging.LoadState.Loading
//import androidx.paging.PagingData
//import androidx.paging.compose.LazyPagingItems
//import androidx.paging.compose.collectAsLazyPagingItems
//import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor
//import com.github.panpf.sketch.sample.R
//import com.github.panpf.sketch.sample.appSettingsService
//import com.github.panpf.sketch.sample.model.LayoutMode
//import com.github.panpf.sketch.sample.ui.model.Photo
//import com.github.panpf.sketch.sample.ui.list.AppendState
//import com.github.panpf.sketch.sample.ui.screen.PhotoGridItem
//import com.github.panpf.sketch.sample.util.ignoreFirst
//import kotlinx.coroutines.flow.Flow
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun PhotoGrid(
//    photoPagingFlow: Flow<PagingData<Photo>>,
//    animatedPlaceholder: Boolean = false,
//    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit
//) {
//    val appSettingsService = LocalContext.current.appSettingsService
//    val pagingItems = photoPagingFlow.collectAsLazyPagingItems()
//    LaunchedEffect(Unit) {
//        appSettingsService.ignoreExifOrientation.ignoreFirst().collect {
//            pagingItems.refresh()
//        }
//    }
//
//    val pullRefreshState = rememberPullRefreshState(
//        refreshing = pagingItems.loadState.refresh is Loading,
//        onRefresh = { pagingItems.refresh() }
//    )
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pullRefresh(pullRefreshState)
//    ) {
//        val photoListLayoutMode by appSettingsService.photoListLayoutMode.collectAsState()
//        if (LayoutMode.valueOf(photoListLayoutMode) == LayoutMode.GRID) {
//            PhotoNormalGrid(pagingItems, animatedPlaceholder, onClick)
//        } else {
//            PhotoStaggeredGrid(pagingItems, animatedPlaceholder, onClick)
//        }
//
//        PullRefreshIndicator(
//            refreshing = pagingItems.loadState.refresh is Loading,
//            state = pullRefreshState,
//            modifier = Modifier.align(Alignment.TopCenter)
//        )
//    }
//}
//
//@Composable
//private fun PhotoNormalGrid(
//    pagingItems: LazyPagingItems<Photo>,
//    animatedPlaceholder: Boolean,
//    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
//) {
//    val gridState = rememberLazyGridState()
//    LaunchedEffect(gridState.isScrollInProgress) {
//        PauseLoadWhenScrollingDecodeInterceptor.scrolling =
//            gridState.isScrollInProgress
//    }
//
//    LazyVerticalGrid(
//        columns = GridCells.Adaptive(minSize = 100.dp),
//        state = gridState,
//        contentPadding = PaddingValues(dimensionResource(id = R.dimen.grid_divider)),
//        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
//        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
//    ) {
//        items(
//            count = pagingItems.itemCount,
//            key = { pagingItems.peek(it)?.originalUrl ?: "" },
//            contentType = { 1 }
//        ) { index ->
//            val item = pagingItems[index]
//            item?.let {
//                PhotoGridItem(
//                    index = index,
//                    photo = it,
//                    animatedPlaceholder = animatedPlaceholder,
//                    staggeredGridMode = false
//                ) { photo, index ->
//                    onClick(pagingItems.itemSnapshotList.items, photo, index)
//                }
//            }
//        }
//
//        if (pagingItems.itemCount > 0) {
//            item(
//                key = "AppendState",
//                span = { GridItemSpan(this.maxLineSpan) },
//                contentType = 2
//            ) {
//                AppendState(pagingItems.loadState.append) {
//                    pagingItems.retry()
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun PhotoStaggeredGrid(
//    pagingItems: LazyPagingItems<Photo>,
//    animatedPlaceholder: Boolean,
//    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
//) {
//    val gridState = rememberLazyStaggeredGridState()
//    LaunchedEffect(gridState.isScrollInProgress) {
//        PauseLoadWhenScrollingDecodeInterceptor.scrolling =
//            gridState.isScrollInProgress
//    }
//
//    LazyVerticalStaggeredGrid(
//        columns = StaggeredGridCells.Adaptive(minSize = 100.dp),
//        state = gridState,
//        contentPadding = PaddingValues(dimensionResource(id = R.dimen.grid_divider)),
//        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
//        verticalItemSpacing = dimensionResource(id = R.dimen.grid_divider),
//    ) {
//        items(
//            count = pagingItems.itemCount,
//            key = { pagingItems.peek(it)?.originalUrl ?: "" },
//            contentType = { 1 }
//        ) { index ->
//            val item = pagingItems[index]
//            item?.let {
//                PhotoGridItem(
//                    index = index,
//                    photo = it,
//                    animatedPlaceholder = animatedPlaceholder,
//                    staggeredGridMode = true
//                ) { photo, index ->
//                    onClick(pagingItems.itemSnapshotList.items, photo, index)
//                }
//            }
//        }
//
//        if (pagingItems.itemCount > 0) {
//            item(
//                key = "AppendState",
//                span = StaggeredGridItemSpan.FullLine,
//                contentType = 2
//            ) {
//                AppendState(pagingItems.loadState.append) {
//                    pagingItems.retry()
//                }
//            }
//        }
//    }
//}