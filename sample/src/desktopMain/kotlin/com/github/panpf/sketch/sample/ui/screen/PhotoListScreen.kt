package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.launch

class PhotoListScreen : Screen {

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalPlatformContext.current
        val sketch = SingletonSketch.get(context)
        val navigator = LocalNavigator.current!!
        val photoTabs = remember {
            listOf(
                PhotoTab(
                    title = "Local",
                    animatedPlaceholder = false,
                    photoPagingFlow = LocalPhotoListScreenModel(context, sketch).pagingFlow
                ),
                PhotoTab(
                    title = "Pexels",
                    animatedPlaceholder = false,
                    photoPagingFlow = PexelsPhotoListScreenModel().pagingFlow
                ),
                PhotoTab(
                    title = "Giphy",
                    animatedPlaceholder = true,
                    photoPagingFlow = GiphyPhotoListScreenModel().pagingFlow
                ),
            )
        }
        val pagerState = rememberPagerState { photoTabs.size }
        Column {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                photoTabs.forEachIndexed { index, page ->
                    Tab(
                        selected = index == pagerState.currentPage,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    ) {
                        Text(text = page.title, Modifier.padding(vertical = 10.dp))
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { pageIndex ->
                    val page = photoTabs[pageIndex]
                    PhotoGrid(
                        photoPagingFlow = page.photoPagingFlow,
                        animatedPlaceholder = page.animatedPlaceholder,
                        gridCellsMinSize = 150.dp,
                        onClick = { photos, _, index ->
                            startPhotoPagerScreen(navigator, photos, index)
                        },
                        onLongClick = { _, _, _, imageResult ->
                            // TODO Open photo info dialog
                        }
                    )
                }

                MainMenu(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp))
            }
        }
    }

    private fun startPhotoPagerScreen(navigator: Navigator, items: List<Photo>, position: Int) {
        val totalCount = items.size
        val startPosition = (position - 50).coerceAtLeast(0)
        val endPosition = (position + 50).coerceAtMost(totalCount - 1)
        val imageList = items.asSequence()
            .filterIndexed { index, _ -> index in startPosition..endPosition }
            .map {
                ImageDetail(
                    originUrl = it.originalUrl,
                    mediumUrl = it.detailPreviewUrl,
                    thumbnailUrl = it.listThumbnailUrl,
                )
            }.toList()
        navigator.push(
            PhotoPagerScreen(
                imageList = imageList,
                totalCount = totalCount,
                startPosition = startPosition,
                initialPosition = position
            )
        )
    }
}