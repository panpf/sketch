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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.datasource.DataFrom
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
    animatedPlaceholder: Boolean = false,
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
    val localView = LocalView.current
    LaunchedEffect(key1 = reloadFlow) {
        scope.launch {
            restartImageFlow.collect {
                // todo Look for ways to actively discard the old state redraw, and then listen for restartImageFlow to perform the redraw
                context.showLongToast("You need to scroll through the list manually to see the changes")
                localView.postInvalidate()
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
                    PhotoContent(index, it, animatedPlaceholder) { photo, index ->
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
    animatedPlaceholder: Boolean = false,
    onClick: (photo: Photo, index: Int) -> Unit
) {
    var componentSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val path by remember(componentSize) {
        derivedStateOf {
            Path().apply {
                val viewWidth = componentSize.width.toFloat()
                val paddingRight = 0
                val paddingTop = 0
                val realSize = with(density) { 20.dp.toPx() }
                moveTo(
                    viewWidth - paddingRight - realSize,
                    paddingTop.toFloat()
                )
                lineTo(
                    viewWidth - paddingRight.toFloat(),
                    paddingTop.toFloat()
                )
                lineTo(
                    viewWidth - paddingRight.toFloat(),
                    paddingTop.toFloat() + realSize
                )
                close()
            }
        }
    }
    var dataFrom by remember { mutableStateOf<DataFrom?>(null) }

    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clickable {
            onClick(photo, index)
        }
        .onSizeChanged {
            componentSize = it
        }
        .drawWithContent {
            drawContent()
            val dataFrom1 = dataFrom
            if (dataFrom1 != null) {
                val color = Color(dataFrom2Color(dataFrom1))
                drawPath(path, color)
            }
        }
    val request = DisplayRequest(LocalContext.current, photo.listThumbnailUrl) {
        setApplySettings(LIST)
        if (animatedPlaceholder) {
            placeholder(drawable.ic_placeholder_eclipse_animated)
        } else {
            placeholder(IconStateImage(drawable.ic_image_outline, ResColor(color.placeholder_bg)))
        }
        error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg))) {
            saveCellularTrafficError(
                IconStateImage(drawable.ic_signal_cellular, ResColor(color.placeholder_bg))
            )
        }
        crossfade()
        resizeApplyToDrawable()
        // todo 加了 listener 会导致图片在列表滑动时不停的加载，估计是 equals 的原因
//        listener(
//            onSuccess = { _, result ->
//                dataFrom = result.dataFrom
//            },
//            onError = { _, _ ->
//                dataFrom = null
//            }
//        )
    }
    when (index % 3) {
        0 -> {
            com.github.panpf.sketch.compose.AsyncImage(
                request = request,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        1 -> {
            com.github.panpf.sketch.compose.SubcomposeAsyncImage(
                request = request,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        else -> {
            Image(
                painter = com.github.panpf.sketch.compose.rememberAsyncImagePainter(
                    request = request,
                ),
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo"
            )
        }
    }
}

private const val FROM_FLAG_COLOR_MEMORY = 0x77008800   // dark green
private const val FROM_FLAG_COLOR_MEMORY_CACHE = 0x7700FF00   // green
private const val FROM_FLAG_COLOR_RESULT_CACHE = 0x77FFFF00 // yellow
private const val FROM_FLAG_COLOR_LOCAL = 0x771E90FF   // dodger blue
private const val FROM_FLAG_COLOR_DOWNLOAD_CACHE = 0x77FF8800 // dark yellow
private const val FROM_FLAG_COLOR_NETWORK = 0x77FF0000  // red
fun dataFrom2Color(dataFrom: DataFrom): Int {
    return when (dataFrom) {
        DataFrom.MEMORY_CACHE -> FROM_FLAG_COLOR_MEMORY_CACHE
        DataFrom.MEMORY -> FROM_FLAG_COLOR_MEMORY
        DataFrom.RESULT_CACHE -> FROM_FLAG_COLOR_RESULT_CACHE
        DataFrom.DOWNLOAD_CACHE -> FROM_FLAG_COLOR_DOWNLOAD_CACHE
        DataFrom.LOCAL -> FROM_FLAG_COLOR_LOCAL
        DataFrom.NETWORK -> FROM_FLAG_COLOR_NETWORK
    }
}