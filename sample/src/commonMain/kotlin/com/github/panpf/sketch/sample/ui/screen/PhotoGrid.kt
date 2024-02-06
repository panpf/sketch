package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState.Loading
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.SubcomposeAsyncImage
import com.github.panpf.sketch.compose.ability.dataFromLogo
import com.github.panpf.sketch.compose.ability.mimeTypeLogo
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.rememberAsyncImagePainter
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.compose.stateimage.rememberIconPainterStateImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.list.AppendState
import com.github.panpf.sketch.sample.ui.model.LayoutMode
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.rememberIconErrorBaselinePainter
import com.github.panpf.sketch.sample.ui.rememberIconImageOutlinePainter
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sample.util.letIf
import com.github.panpf.sketch.sample.util.rememberMimeTypeLogoMap
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import kotlinx.coroutines.flow.Flow

enum class PhotoSource {
    Local, Pexels, Giphy
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun PhotoGrid(animatedPlaceholder: Boolean, photoPagingFlow: Flow<PagingData<Photo>>) {
    val pagingItems = photoPagingFlow.collectAsLazyPagingItems()
    val context = LocalPlatformContext.current
    val appSettingsService = context.appSettings
    LaunchedEffect(Unit) {
        appSettingsService.ignoreExifOrientation.ignoreFirst().collect {
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
            PhotoNormalGrid(
                pagingItems = pagingItems,
                animatedPlaceholder = animatedPlaceholder,
//                    onClick = onClick
            )
        } else {
            PhotoStaggeredGrid(
                pagingItems = pagingItems,
                animatedPlaceholder = animatedPlaceholder,
//                    onClick
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
private fun pagingFlow(photoSource: PhotoSource): Flow<PagingData<Photo>> {
    val context = LocalPlatformContext.current
    val sketch = SingletonSketch.get(context)
    return when (photoSource) {
        PhotoSource.Local -> remember {
            LocalPhotoListScreenModel(
                context,
                sketch
            )
        }.pagingFlow

        PhotoSource.Pexels -> remember { PexelsPhotoListScreenModel() }.pagingFlow
        PhotoSource.Giphy -> remember { GiphyPhotoListScreenModel() }.pagingFlow
    }
}

@Composable
private fun PhotoNormalGrid(
    pagingItems: LazyPagingItems<Photo>,
    animatedPlaceholder: Boolean,
//        onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        val gridState = rememberLazyGridState()
        LaunchedEffect(gridState.isScrollInProgress) {
            PauseLoadWhenScrollingDecodeInterceptor.scrolling =
                gridState.isScrollInProgress
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
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
                        staggeredGridMode = false
                    ) { photo, index ->
//                        onClick(pagingItems.itemSnapshotList.items, photo, index)
                    }
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

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(10.dp),
            adapter = rememberScrollbarAdapter(
                scrollState = gridState
            )
        )
    }
}

@Composable
private fun PhotoStaggeredGrid(
    pagingItems: LazyPagingItems<Photo>,
    animatedPlaceholder: Boolean,
//        onClick: (items: List<Photo>, photo: Photo, index: Int) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        val gridState = rememberLazyStaggeredGridState()
        LaunchedEffect(gridState.isScrollInProgress) {
            PauseLoadWhenScrollingDecodeInterceptor.scrolling =
                gridState.isScrollInProgress
        }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
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
                        staggeredGridMode = true
                    ) { photo, index ->
//                        onClick(pagingItems.itemSnapshotList.items, photo, index)
                    }
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

//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(
//                scrollState = gridState
//            )
//        )
    }
}

@Composable
fun PhotoGridItem(
    index: Int,
    photo: Photo,
    animatedPlaceholder: Boolean = false,
    staggeredGridMode: Boolean = false,
    onClick: (photo: Photo, index: Int) -> Unit
) {
    val context = LocalPlatformContext.current
    val imageState = rememberAsyncImageState()
    val mimeTypeLogoMap = rememberMimeTypeLogoMap()
    val progressPainter = rememberThemeSectorProgressPainter(hiddenWhenIndeterminate = true)
    val appSettingsService = context.appSettings
    val showDataFromLogo by appSettingsService.showDataFromLogo.collectAsState()
    val showMimeTypeLogo by appSettingsService.showMimeTypeLogoInLIst.collectAsState()
    val showProgressIndicator by appSettingsService.showProgressIndicatorInList.collectAsState()
    val modifier = Modifier
        .fillMaxWidth()
        .let {
            val photoWidth = photo.width ?: 0
            val photoHeight = photo.height ?: 0
            if (staggeredGridMode && photoWidth > 0 && photoHeight > 0) {
                it.aspectRatio(photoWidth.toFloat() / photoHeight)
            } else {
                it.aspectRatio(1f)
            }
        }
        .pointerInput(photo, index) {
            detectTapGestures(
                onTap = { onClick(photo, index) },
                onLongPress = {
//                        val displayResult = imageState.result
//                        if (displayResult != null) {
//                            view
//                                .findNavController()
//                                .navigate(PhotoInfoDialogFragment.createNavDirections(displayResult))
//                        }
                }
            )
        }
        .letIf(showDataFromLogo) {
            it.dataFromLogo(imageState)
        }
        .letIf(showMimeTypeLogo) {
            it.mimeTypeLogo(imageState, mimeTypeLogoMap, margin = 4.dp)
        }
        .letIf(showProgressIndicator) {
            it.progressIndicator(imageState, progressPainter)
        }

    val listSettings by appSettingsService.listsCombinedFlow.collectAsState(Unit)
    val colorScheme = MaterialTheme.colorScheme
    val placeholderStateImage = rememberIconPainterStateImage(
        icon = rememberIconImageOutlinePainter(),
        background = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer
    )
    val errorStateImage = rememberIconPainterStateImage(
        icon = rememberIconErrorBaselinePainter(),
        background = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer
    )
    val saveCellularTrafficStateImage = rememberIconPainterStateImage(
        icon = rememberIconErrorBaselinePainter(),
        background = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer
    )
    val request = remember(photo.listThumbnailUrl, listSettings) {
        ImageRequest(context, photo.listThumbnailUrl) {
//                if (animatedPlaceholder) {   // TODO
//                    placeholder(
//                        AnimatableIconStateImage(drawable.ic_placeholder_eclipse_animated) {
//                            resColorBackground(color.placeholder_bg)
//                        }
//                    )
//                } else {
            placeholder(placeholderStateImage)
//                }
            error(errorStateImage) {
                saveCellularTrafficError(saveCellularTrafficStateImage)
            }
            crossfade()
            resizeOnDraw()
            merge(appSettingsService.buildListImageOptions())
        }
    }
    when (index % 3) {
        0 -> {
            AsyncImage(
                request = request,
                state = imageState,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        1 -> {
            SubcomposeAsyncImage(
                request = request,
                state = imageState,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo",
            )
        }

        else -> {
            Image(
                painter = rememberAsyncImagePainter(
                    request = request,
                    state = imageState,
                    contentScale = ContentScale.Crop
                ),
                modifier = modifier,
                contentScale = ContentScale.Crop,
                contentDescription = "photo"
            )
        }
    }
}