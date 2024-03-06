package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.ability.dataFromLogo
import com.github.panpf.sketch.compose.ability.progressIndicator
import com.github.panpf.sketch.compose.painter.rememberSectorProgressPainter
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FetcherTestScreen : BaseScreen() {

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    override fun DrawContent() {
        ToolbarScaffold(title = "FetcherTest") {
            val context = LocalPlatformContext.current
            val screenModel = rememberScreenModel { FetcherTestScreenModel(context) }
            val items by screenModel.testItems.collectAsState()
            if (items.isNotEmpty()) {
                val pagerState = rememberPagerState(0) { items.size }
                val coroutineScope = rememberCoroutineScope()
                Column(Modifier.fillMaxWidth()) {
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
                        val imageState = rememberAsyncImageState()
                        val progressPainter = rememberSectorProgressPainter()
                        AsyncImage(
                            request = ImageRequest(context, items[it].imageUri) {
                                memoryCachePolicy(DISABLED)
                                resultCachePolicy(DISABLED)
                                downloadCachePolicy(DISABLED)
                            },
                            contentDescription = "Image",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .fillMaxSize()
                                .dataFromLogo(imageState)
                                .progressIndicator(imageState, progressPainter) // TODO invalid
                        )
                    }
                }
            }
        }
    }

    class FetcherTestScreenModel(val context: PlatformContext) : ScreenModel {
        private val _testItems = MutableStateFlow<List<FetcherTestItem>>(emptyList())
        val testItems: StateFlow<List<FetcherTestItem>> = _testItems

        init {
            screenModelScope.launch {
                val buildFetcherTestItems = buildFetcherTestItems(context)
                _testItems.value = buildFetcherTestItems
            }
        }
    }
}

class FetcherTestItem(val title: String, val imageUri: String)

expect suspend fun buildFetcherTestItems(context: PlatformContext): List<FetcherTestItem>