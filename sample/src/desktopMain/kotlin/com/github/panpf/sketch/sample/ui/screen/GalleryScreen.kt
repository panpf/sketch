package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.ability.dataFromLogo
import com.github.panpf.sketch.compose.ability.mimeTypeLogo
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.compose.request.crossfade
import com.github.panpf.sketch.compose.stateimage.iconPainterStateImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageStartCropScaleDecider
import com.github.panpf.sketch.sample.ui.navigation.Navigation
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.sample.util.rememberMimeTypeLogoMap
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun GalleryScreen(navigation: Navigation) {
    val coroutineScope = rememberCoroutineScope()
    val localListViewModel = rememberLocalPhotoListViewModel()
    val pexelsListViewModel = rememberPexelsPhotoListViewModel()
    val giphyListViewModel = rememberGiphyPhotoListViewModel()
    val photoListStates = remember {
        listOf(
            localListViewModel.photoList,
            pexelsListViewModel.photoList,
            giphyListViewModel.photoList,
        )
    }
    val tabTiles = remember {
        listOf("Local", "Pexels", "Giphy")
    }
    val pagerState = rememberPagerState() {
        photoListStates.size
    }
    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTiles.forEachIndexed { index, title ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                ) {
                    Text(text = title, Modifier.padding(vertical = 10.dp))
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            PhotoGridPage(photoListStates[page])
        }
    }
}

@Composable
fun PhotoGridPage(photoListState: StateFlow<List<Photo>>) {
    val photoList by photoListState.collectAsState()
    val divider = Arrangement.spacedBy(4.dp)
    val colorScheme = MaterialTheme.colorScheme

    Box(Modifier.fillMaxSize()) {
        val gridState: LazyGridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            horizontalArrangement = divider,
            verticalArrangement = divider,
            modifier = Modifier.fillMaxSize(),
            state = gridState,
        ) {
            itemsIndexed(photoList) { _, photo ->
                val imageState = rememberAsyncImageState()
                val imagePainter = painterResource("ic_image_outline.xml")
                AsyncImage(
                    request = ImageRequest(LocalPlatformContext.current, photo.thumbnailUrl) {
                        precision(LongImageClipPrecisionDecider())
                        scale(LongImageStartCropScaleDecider())
//                        memoryCachePolicy(DISABLED)
//                        placeholder(colorPainterStateImage(colorScheme.primaryContainer))
                        placeholder(
                            iconPainterStateImage(
                                icon = imagePainter,
                                background = colorScheme.primaryContainer,
                                iconTint = colorScheme.onPrimaryContainer
                            )
                        )
                        crossfade()
                    },
                    state = imageState,
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .dataFromLogo(imageState)
                        .mimeTypeLogo(imageState, rememberMimeTypeLogoMap(), margin = 4.dp)
                        .progressIndicator(imageState, rememberThemeSectorProgressPainter())
                        .clickable {
//                            navigation.push(Page.Slideshow(imageResourceList, index))
                        }
                )
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = gridState
            )
        )
    }
}