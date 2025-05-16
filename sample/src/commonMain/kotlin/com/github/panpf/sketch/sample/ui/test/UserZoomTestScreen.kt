package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntSize
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.util.isNotEmpty
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.compose.ZoomState
import com.github.panpf.zoomimage.compose.rememberZoomState
import com.github.panpf.zoomimage.compose.zoom.ZoomableState
import com.github.panpf.zoomimage.compose.zoom.zoomable
import kotlinx.coroutines.launch

class UserZoomTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "UserZoomTest") {
            val tabs = remember {
                listOf("HOR", "VER")
            }
            val pagerState = rememberPagerState(0) { tabs.size }
            val coroutineScope = rememberCoroutineScope()
            Column(
                Modifier.fillMaxWidth()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
            ) {
                ScrollableTabRow(selectedTabIndex = pagerState.currentPage, edgePadding = 20.dp) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                            content = {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                                )
                            }
                        )
                    }
                }
                HorizontalPager(state = pagerState) {
                    when (tabs[it]) {
                        "HOR" -> UserZoomContent(ResourceImages.longQMSHT.uri)
                        "VER" -> UserZoomContent(ResourceImages.longCOMIC.uri)
                    }
                }
            }
        }
    }

    @Composable
    private fun UserZoomContent(uri: String) {
        Box(Modifier.fillMaxSize()) {
            val imageState = rememberAsyncImageState()
            val zoomState: ZoomState = rememberZoomState()
            imageState.onPainterState = remember {
                {
                    val painterSize = it.painter
                        ?.intrinsicSize
                        ?.takeIf { it.isSpecified }
                        ?.roundToIntSize()
                        ?.takeIf { it.isNotEmpty() }
                    zoomState.zoomable.contentSize = painterSize ?: IntSize.Zero
                }
            }
            AsyncImage(
                request = ComposableImageRequest(uri) {
                    memoryCachePolicy(CachePolicy.DISABLED)
                    resultCachePolicy(CachePolicy.DISABLED)
                    placeholder(ThumbnailMemoryCacheStateImage(uri))
                    crossfade(fadeStart = false)
                },
                state = imageState,
                contentDescription = "",
                modifier = Modifier.matchParentSize()
                    .background(Color.Cyan)
                    .zoomable(
                        zoomable = zoomState.zoomable,
                        userSetupContentSize = true,
                    )
                    .zoomingWithUser(zoomState.zoomable)
            )
        }
    }

    /**
     * A Modifier that applies changes in [ZoomableState].transform to the component. It can be used on any composable component.
     */
    private fun Modifier.zoomingWithUser(
        zoomable: ZoomableState
    ): Modifier = this
        .clipToBounds()
        .graphicsLayer {
            val transform = zoomable.userTransform
            zoomable.logger.v { "ZoomableState. graphicsLayer. transform=$transform" }
            scaleX = transform.scaleX
            scaleY = transform.scaleY
            translationX = transform.offsetX
            translationY = transform.offsetY
            transformOrigin = transform.scaleOrigin
        }
        // Because rotationOrigin and rotationOrigin are different, they must be set separately.
        .graphicsLayer {
            val transform = zoomable.transform
            rotationZ = transform.rotation
            transformOrigin = transform.rotationOrigin
        }
}