package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.panpf.sketch.sample.ui.screen.base.BaseScreen
import com.github.panpf.sketch.sample.ui.screen.base.ToolbarScaffold

class DecoderTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "DecoderTest") {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("In development...")
            }
//            val images = remember {
//                listOf(
//                    AssetImages.jpeg,
//                    AssetImages.png,
//                    AssetImages.bmp,
//                    AssetImages.heic,
//                    AssetImages.svg,
//                    AssetImages.webp,
//                    AssetImages.animGif,
//                )
//            }
//            val pagerState = rememberPagerState(0) { images.size }
//            val coroutineScope = rememberCoroutineScope()
//            Column(Modifier.fillMaxWidth()) {
//                ScrollableTabRow(selectedTabIndex = pagerState.currentPage, edgePadding = 20.dp) {
//                    images.forEachIndexed { index, image ->
//                        Tab(
//                            selected = index == pagerState.currentPage,
//                            onClick = {
//                                coroutineScope.launch {
//                                    pagerState.scrollToPage(index)
//                                }
//                            },
//                            content = {
//                                Text(
//                                    text = image.name,
//                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
//                                )
//                            }
//                        )
//                    }
//                }
//                HorizontalPager(state = pagerState) {
//                    AsyncImage(
//                        request = ImageRequest(LocalPlatformContext.current, images[it].uri),
//                        contentDescription = "Image",
//                        modifier = Modifier.padding(16.dp).weight(1f).fillMaxWidth()
//                    )
//                }
//            }
        }
    }
}