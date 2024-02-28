package com.github.panpf.sketch.sample.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.rememberIconDebugPainter
import com.github.panpf.sketch.sample.ui.rememberIconGiphyPainter
import com.github.panpf.sketch.sample.ui.rememberIconPexelsPainter
import com.github.panpf.sketch.sample.ui.rememberIconPhonePainter
import com.github.panpf.sketch.sample.ui.screen.GiphyPhotoListScreenModel
import com.github.panpf.sketch.sample.ui.screen.LocalPhotoListScreenModel
import com.github.panpf.sketch.sample.ui.screen.MainMenu
import com.github.panpf.sketch.sample.ui.screen.MyTab
import com.github.panpf.sketch.sample.ui.screen.PexelsPhotoListScreenModel
import com.github.panpf.sketch.sample.ui.screen.PhotoGrid
import com.github.panpf.sketch.sample.ui.screen.PhotoTab
import com.github.panpf.sketch.sample.ui.screen.TestTab
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PhotoListPage(startPhotoPager: (items: List<Photo>, position: Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalPlatformContext.current
    val sketch = SingletonSketch.get(context)
    val photoTabs: List<MyTab> = remember {
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
            TestTab
        )
    }
    val pagerState = rememberPagerState { photoTabs.size }
    Column {
//        TabRow(selectedTabIndex = pagerState.currentPage) {
//            photoTabs.forEachIndexed { index, page ->
//                Tab(
//                    selected = index == pagerState.currentPage,
//                    onClick = {
//                        coroutineScope.launch {
//                            pagerState.animateScrollToPage(index)
//                        }
//                    }
//                ) {
//                    Text(text = page.title, Modifier.padding(vertical = 10.dp))
//                }
//            }
//        }
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { pageIndex ->
                val page = photoTabs[pageIndex]
                if (page is PhotoTab) {
                    PhotoGrid(
                        photoPagingFlow = page.photoPagingFlow,
                        animatedPlaceholder = page.animatedPlaceholder,
                        gridCellsMinSize = 150.dp,
                        onClick = { photos, _, index ->
                            startPhotoPager(photos, index)
                        },
                    )
                } else if (page is TestTab) {
                    TestPage()
                }
            }

            MainMenu(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp))
        }
        NavigationBar(Modifier.fillMaxWidth()) {
            NavigationBarItem(
                icon = {
                    Icon(
                        rememberIconPhonePainter(),
                        contentDescription = "Phone",
                        Modifier.size(24.dp)
                    )
                },
                label = { Text("Local") },
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.scrollToPage(0) } }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        rememberIconPexelsPainter(),
                        contentDescription = "Pexels",
                        Modifier.size(24.dp)
                    )
                },
                label = { Text("Pexels") },
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.scrollToPage(1) } }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        rememberIconGiphyPainter(),
                        contentDescription = "Giphy",
                        Modifier.size(24.dp)
                    )
                },
                label = { Text("Giphy") },
                selected = pagerState.currentPage == 2,
                onClick = { coroutineScope.launch { pagerState.scrollToPage(2) } }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        rememberIconDebugPainter(),
                        contentDescription = "Test",
                        Modifier.size(24.dp)
                    )
                },
                label = { Text("Test") },
                selected = pagerState.currentPage == 3,
                onClick = { coroutineScope.launch { pagerState.scrollToPage(3) } }
            )
        }
    }
}