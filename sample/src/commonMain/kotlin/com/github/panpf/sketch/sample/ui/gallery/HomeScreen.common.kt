@file:OptIn(ExperimentalResourceApi::class)

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
import cafe.adriel.voyager.core.screen.Screen
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.test.TestPage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import sketch.sample.generated.resources.Res
import sketch.sample.generated.resources.ic_debug
import sketch.sample.generated.resources.ic_giphy
import sketch.sample.generated.resources.ic_pexels
import sketch.sample.generated.resources.ic_phone

expect val gridCellsMinSize: Dp

@Composable
expect fun HomeHeader()

enum class HomeTab(
    val title: String,
    val icon: DrawableResource,
    val content: @Composable Screen.() -> Unit
) {
    LOCAL("Local", Res.drawable.ic_phone, { LocalPhotoPage() }),
    PEXELS("Pexels", Res.drawable.ic_pexels, { PexelsPhotoPage() }),
    GIPHY("Giphy", Res.drawable.ic_giphy, { GiphyPhotoPage() }),
    TEST("Test", Res.drawable.ic_debug, { TestPage() }),
}

object HomeScreen : BaseScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun DrawContent() {
        Column {
            HomeHeader()

            val coroutineScope = rememberCoroutineScope()
            val context = LocalPlatformContext.current
            val appSettings = context.appSettings
            val homeTabs = remember {
                HomeTab.values()
            }

            Column {
                val pagerState = rememberPagerState(
                    initialPage = appSettings.currentPageIndex.value.coerceIn(0, homeTabs.size - 1),
                    pageCount = { homeTabs.size }
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
                        homeTabs[pageIndex].content.invoke(this@HomeScreen)
                    }

                    MainMenu(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp))
                }

                NavigationBar(Modifier.fillMaxWidth()) {
                    homeTabs.forEachIndexed { index, homeTab ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(homeTab.icon),
                                    contentDescription = homeTab.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text(homeTab.title) },
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } }
                        )
                    }
                }
            }
        }
    }
}