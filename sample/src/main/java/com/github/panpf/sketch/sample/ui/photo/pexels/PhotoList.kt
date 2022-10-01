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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDrawableDecodeInterceptor
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.image.ImageType.LIST
import com.github.panpf.sketch.sample.image.setApplySettings
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.common.compose.AppendState
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import com.github.panpf.tools4a.toast.ktx.showLongToast
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun PhotoListContent(
    photoPagingFlow: Flow<PagingData<Photo>>,
    restartImageFlow: Flow<Any>,
    reloadFlow: Flow<Any>,
    onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit
) {
    val lazyPagingItems = photoPagingFlow.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = reloadFlow) {
        scope.launch {
            reloadFlow.collect {
                lazyPagingItems.refresh()
            }
        }
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = reloadFlow) {
        scope.launch {
            restartImageFlow.collect {
                // todo Look for ways to actively discard the old state redraw, and then listen for restartImageFlow to perform the redraw
                context.showLongToast("You need to scroll through the list manually to see the changes")
            }
        }
    }
    SwipeRefresh(
        state = SwipeRefreshState(lazyPagingItems.loadState.refresh is LoadState.Loading),
        onRefresh = { lazyPagingItems.refresh() }
    ) {
        val lazyGridState = rememberLazyGridState()
        if (lazyGridState.isScrollInProgress) {
            DisposableEffect(Unit) {
                PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = true
                onDispose {
                    PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = false
                }
            }
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
                    PhotoContent(index, it) { photo, index ->
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
}

@Composable
fun PhotoContent(
    index: Int,
    photo: Photo,
    onClick: (photo: Photo, index: Int) -> Unit
) {
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clickable {
            onClick(photo, index)
        }
    val configBlock: (DisplayRequest.Builder.() -> Unit) = {
        setApplySettings(LIST)
        placeholder(IconStateImage(drawable.ic_image_outline, ResColor(color.placeholder_bg)))
        error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg))) {
            saveCellularTrafficError(
                IconStateImage(drawable.ic_signal_cellular, ResColor(color.placeholder_bg))
            )
        }
        crossfade()
        resizeApplyToDrawable()
    }
    when (index % 3) {
        0 -> {
            com.github.panpf.sketch.compose.AsyncImage(
                imageUri = photo.listThumbnailUrl,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "",
                configBlock = configBlock
            )
        }
        1 -> {
            com.github.panpf.sketch.compose.SubcomposeAsyncImage(
                imageUri = photo.listThumbnailUrl,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "",
                configBlock = configBlock
            )
        }
        else -> {
            Image(
                painter = com.github.panpf.sketch.compose.rememberAsyncImagePainter(
                    imageUri = photo.listThumbnailUrl,
                    configBlock = configBlock
                ),
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
        }
    }
}