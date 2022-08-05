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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.common.compose.itemsIndexed
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.tools4a.dimen.ktx.px2dp
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotoListContent(photoPagingFlow: Flow<PagingData<Photo>>, disabledCache: Boolean = false) {
    val items = photoPagingFlow.collectAsLazyPagingItems()
    LazyVerticalGrid(Fixed(3)) {
        itemsIndexed(items) { _, photo ->
            photo?.let { PhotoContent(it, disabledCache) }
        }
    }
}

@Composable
fun PhotoContent(photo: Photo, disabledCache: Boolean = false) {
    val itemSizeDp = LocalContext.current.getScreenWidth().px2dp / 3
    AsyncImage(
        imageUri = photo.firstThumbnailUrl,
        modifier = Modifier.size(itemSizeDp.dp, itemSizeDp.dp),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    ) {
        placeholder(IconStateImage(drawable.ic_image_outline, ResColor(color.placeholder_bg)))
        error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg)))
        crossfade()
        if (disabledCache) {
            downloadCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }
    }
}