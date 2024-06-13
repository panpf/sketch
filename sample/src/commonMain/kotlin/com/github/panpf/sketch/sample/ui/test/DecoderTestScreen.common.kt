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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.ability.dataFromLogo
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.common.list.LoadState
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DecoderTestScreen : BaseScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "DecoderTest") {
            val context = LocalPlatformContext.current
            val screenModel = rememberScreenModel { DecoderTestScreenModel(context) }
            val items by screenModel.testItems.collectAsState()
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
                                        text = item.name,
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
                        val imageState = rememberAsyncImageState {
                            ImageOptions {
                                memoryCachePolicy(DISABLED)
                                resultCachePolicy(DISABLED)
                                downloadCachePolicy(DISABLED)
                            }
                        }

                        println(imageState)
                        val progressPainter = rememberThemeSectorProgressPainter()
                        val testItem = items[it]
                        if ((testItem.currentApi ?: 0) >= (testItem.minAPI ?: 0)) {
                            Box(Modifier.fillMaxSize()) {
                                val imageUri = testItem.imageUri
                                MyAsyncImage(
                                    request = ImageRequest(context, imageUri) {
                                        if (testItem.imageDecoder != null) {
                                            components {
                                                addDecoder(testItem.imageDecoder)
                                            }
                                        }
                                    },
                                    contentDescription = "Image",
                                    state = imageState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .dataFromLogo(imageState)
                                        .progressIndicator(imageState, progressPainter)
                                )

                                LoadState(
                                    modifier = Modifier.align(Alignment.Center),
                                    imageState = imageState
                                )
                            }
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("This format requires API ${testItem.minAPI} or higher")
                            }
                        }
                    }
                }
            }
        }
    }

    class DecoderTestScreenModel(val context: PlatformContext) : ScreenModel {
        private val _testItems = MutableStateFlow<List<DecoderTestItem>>(emptyList())
        val testItems: StateFlow<List<DecoderTestItem>> = _testItems

        init {
            screenModelScope.launch {
                _testItems.value = buildDecoderTestItems(context)
            }
        }
    }
}

class DecoderTestItem(
    val name: String,
    val imageUri: String,
    val minAPI: Int? = null,
    val currentApi: Int? = null,
    val imageDecoder: Decoder.Factory? = null
)

expect suspend fun buildDecoderTestItems(context: PlatformContext): List<DecoderTestItem>