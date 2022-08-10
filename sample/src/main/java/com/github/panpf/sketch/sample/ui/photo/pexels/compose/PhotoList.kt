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
package com.github.panpf.sketch.sample.ui.photo.pexels.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.common.compose.AppendState
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotoListContent(photoPagingFlow: Flow<PagingData<Photo>>, disabledCache: Boolean = false) {
    val items = photoPagingFlow.collectAsLazyPagingItems()
    SwipeRefresh(
        state = SwipeRefreshState(items.loadState.refresh is LoadState.Loading),
        onRefresh = { items.refresh() }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.grid_divider)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_divider)),
        ) {
            items(count = items.itemCount) { index ->
                val item = items[index]
                item?.let { PhotoContent(index, it, disabledCache) }
            }

            if (items.itemCount > 0) {
                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    AppendState(items.loadState.append) {
                        items.retry()
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoContent(index: Int, photo: Photo, disabledCache: Boolean = false) {
    when (index % 3) {
        0 -> {
            com.github.panpf.sketch.compose.AsyncImage(
                imageUri = photo.firstThumbnailUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            ) {
                placeholder(
                    IconStateImage(
                        drawable.ic_image_outline,
                        ResColor(color.placeholder_bg)
                    )
                )
                error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg)))
                crossfade()
                if (disabledCache) {
                    downloadCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    memoryCachePolicy(DISABLED)
                }
            }
        }
        1 -> {
            com.github.panpf.sketch.compose.SubcomposeAsyncImage(
                imageUri = photo.firstThumbnailUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            ) {
                placeholder(
                    IconStateImage(
                        drawable.ic_image_outline,
                        ResColor(color.placeholder_bg)
                    )
                )
                error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg)))
                crossfade()
                if (disabledCache) {
                    downloadCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    memoryCachePolicy(DISABLED)
                }
            }
        }
        else -> {
            Image(
                painter = com.github.panpf.sketch.compose.rememberAsyncImagePainter(imageUri = photo.firstThumbnailUrl) {
                    placeholder(
                        IconStateImage(
                            drawable.ic_image_outline,
                            ResColor(color.placeholder_bg)
                        )
                    )
                    error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg)))
                    crossfade()
                    if (disabledCache) {
                        downloadCachePolicy(DISABLED)
                        resultCachePolicy(DISABLED)
                        memoryCachePolicy(DISABLED)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
        }
    }
}