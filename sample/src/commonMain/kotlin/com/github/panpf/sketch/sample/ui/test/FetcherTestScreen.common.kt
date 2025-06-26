package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.ability.dataFromLogo
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.common.AsyncImagePageState
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

class FetcherTestItem(val title: String, val imageUri: String)

expect suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean = true
): List<FetcherTestItem>

class FetcherTestScreen : BaseScreen() {

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    override fun DrawContent() {
        ToolbarScaffold(title = "FetcherTest") {
            val fetcherTestViewModel: FetcherTestViewModel = koinViewModel()
            val items by fetcherTestViewModel.data.collectAsState()
            if (items.isNotEmpty()) {
                val pagerState = rememberPagerState(0) { items.size }
                val coroutineScope = rememberCoroutineScope()
                Column(
                    Modifier.fillMaxWidth()
                        .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        edgePadding = 20.dp
                    ) {
                        items.forEachIndexed { index, item ->
                            Tab(
                                selected = index == pagerState.currentPage,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.scrollToPage(index)
                                    }
                                },
                                content = {
                                    Text(
                                        text = item.title,
                                        modifier = Modifier.padding(
                                            vertical = 8.dp,
                                            horizontal = 10.dp
                                        )
                                    )
                                }
                            )
                        }
                    }
                    HorizontalPager(state = pagerState) {
                        Box(Modifier.fillMaxSize()) {
                            val imageUri = items[it].imageUri
                            val imageState = rememberAsyncImageState {
                                ImageOptions {
                                    memoryCachePolicy(DISABLED)
                                    resultCachePolicy(DISABLED)
                                    downloadCachePolicy(DISABLED)
                                }
                            }
                            val progressPainter = rememberThemeSectorProgressPainter()
                            MyAsyncImage(
                                uri = imageUri,
                                contentDescription = "Image",
                                state = imageState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .dataFromLogo(imageState)
                                    .progressIndicator(imageState, progressPainter)
                            )

                            AsyncImagePageState(imageState = imageState)
                        }
                    }
                }
            }
        }
    }
}