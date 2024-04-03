package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.images.MyImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import kotlinx.coroutines.launch

class ExifOrientationTestScreen : BaseScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "ExifOrientationTest") {
            val exifImages = MyImages.clockExifs
            val pagerState = rememberPagerState(0) { exifImages.size }
            val coroutineScope = rememberCoroutineScope()
            Column(
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
            ) {
                ScrollableTabRow(selectedTabIndex = pagerState.currentPage, edgePadding = 20.dp) {
                    exifImages.forEachIndexed { index, image ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                            content = {
                                Text(
                                    text = image.name,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                                )
                            }
                        )
                    }
                }
                HorizontalPager(state = pagerState) {
                    ExifOrientationTest(exifImages[it])
                }
            }
        }
    }

    @Composable
    private fun ExifOrientationTest(image: MyImage) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Ignore Exif Orientation",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
            val imageUri = image.uri
            MyAsyncImage(
                request = ImageRequest(LocalPlatformContext.current, imageUri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    downloadCachePolicy(DISABLED)
                    ignoreExifOrientation(true)
                },
                contentDescription = "Image",
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
                    .fillMaxWidth()
            )

            Text(
                text = "Read Exif Orientation",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
            MyAsyncImage(
                request = ImageRequest(LocalPlatformContext.current, imageUri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    downloadCachePolicy(DISABLED)
                    ignoreExifOrientation(false)
                },
                contentDescription = "Image",
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}