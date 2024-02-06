package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.navigation.Navigation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun GalleryScreen(navigation: Navigation) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalPlatformContext.current
    val sketch = SingletonSketch.get(context)
    val pages = remember {
        listOf(
            Page(
                title = "Local",
                animatedPlaceholder = false,
                photoPagingFlow = LocalPhotoListScreenModel(context, sketch).pagingFlow
            ),
            Page(
                title = "Pexels",
                animatedPlaceholder = false,
                photoPagingFlow = PexelsPhotoListScreenModel().pagingFlow
            ),
            Page(
                title = "Giphy",
                animatedPlaceholder = true,
                photoPagingFlow = GiphyPhotoListScreenModel().pagingFlow
            ),
        )
    }
    val pagerState = rememberPagerState { pages.size }
    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, page ->
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
                val page = pages[pageIndex]
                PhotoGrid(
                    animatedPlaceholder = page.animatedPlaceholder,
                    photoPagingFlow = page.photoPagingFlow
                )
            }

            MainMenu(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp))
        }
    }
}

private class Page(
    val title: String,
    val animatedPlaceholder: Boolean,
    val photoPagingFlow: Flow<PagingData<Photo>>
)