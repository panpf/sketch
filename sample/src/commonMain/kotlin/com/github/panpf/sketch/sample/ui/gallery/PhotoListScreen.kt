package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.data.paging.GiphyPhotoListPagingSource
import com.github.panpf.sketch.sample.data.paging.LocalPhotoListPagingSource
import com.github.panpf.sketch.sample.data.paging.PexelsPhotoListPagingSource
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.rememberIconDebugPainter
import com.github.panpf.sketch.sample.ui.rememberIconGiphyPainter
import com.github.panpf.sketch.sample.ui.rememberIconPexelsPainter
import com.github.panpf.sketch.sample.ui.rememberIconPhonePainter
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.test.TestPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

expect val gridCellsMinSize: Dp

@Composable
expect fun PhotoListHeader()

object PhotoListScreen : BaseScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun DrawContent() {
        Column {
            PhotoListHeader()

            val navigator = LocalNavigator.current!!
            val coroutineScope = rememberCoroutineScope()
            val context = LocalPlatformContext.current
            val appSettings = context.appSettings
            val sketch = SingletonSketch.get(context)
            val localPhotoListScreenModel = rememberScreenModel {
                LocalPhotoListScreenModel(context, sketch)
            }
            val pexelsPhotoListScreenModel = rememberScreenModel {
                PexelsPhotoListScreenModel()
            }
            val giphyPhotoListScreenModel = rememberScreenModel {
                GiphyPhotoListScreenModel()
            }

            val photoTabs: List<MyTab> = remember(
                localPhotoListScreenModel,
                pexelsPhotoListScreenModel,
                giphyPhotoListScreenModel
            ) {
                listOf(
                    PhotoTab(
                        title = "Local",
                        animatedPlaceholder = false,
                        photoPagingFlow = localPhotoListScreenModel.pagingFlow
                    ),
                    PhotoTab(
                        title = "Pexels",
                        animatedPlaceholder = false,
                        photoPagingFlow = pexelsPhotoListScreenModel.pagingFlow
                    ),
                    PhotoTab(
                        title = "Giphy",
                        animatedPlaceholder = true,
                        photoPagingFlow = giphyPhotoListScreenModel.pagingFlow
                    ),
                    TestTab
                )
            }

            Column {
                val pagerState = rememberPagerState(
                    initialPage = appSettings.currentPageIndex.value.coerceIn(0, photoTabs.size - 1),
                    pageCount = { photoTabs.size }
                )
                LaunchedEffect(Unit) {
                    snapshotFlow { pagerState.currentPage }.collect { index ->
                        appSettings.currentPageIndex.value = index
                    }
                }
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
                                gridCellsMinSize = gridCellsMinSize,
                                onClick = { photos, _, index ->
                                    val params = buildPhotoPagerParams(photos, index)
                                    navigator.push(PhotoPagerScreen(params))
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
    }
}

class LocalPhotoListScreenModel(context: PlatformContext, sketch: Sketch) : ScreenModel {

    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            LocalPhotoListPagingSource(context, sketch)
        }
    ).flow.cachedIn(screenModelScope)
}

class PexelsPhotoListScreenModel : ScreenModel {

    val pagingFlow: Flow<PagingData<Photo>> = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            PexelsPhotoListPagingSource()
        }
    ).flow.cachedIn(screenModelScope)
}

class GiphyPhotoListScreenModel : ScreenModel {

    val pagingFlow: Flow<PagingData<Photo>> = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            GiphyPhotoListPagingSource()
        }
    ).flow.cachedIn(screenModelScope)
}