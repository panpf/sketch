package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.ui.screen.base.BaseScreen
import com.github.panpf.sketch.sample.ui.screen.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.test.transform.BlurTransformationTestPage
import com.github.panpf.sketch.sample.ui.test.transform.CircleTransformationTestPage
import com.github.panpf.sketch.sample.ui.test.transform.MaskTransformationTestPage
import com.github.panpf.sketch.sample.ui.test.transform.MultiTransformationTestPage
import com.github.panpf.sketch.sample.ui.test.transform.RotateTransformationTestPage
import com.github.panpf.sketch.sample.ui.test.transform.RoundCornersTransformationTestPage
import kotlinx.coroutines.launch

class TransformationTestScreen : BaseScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "TransformationTest") {
            val tabs = remember {
                listOf("ROUNDED_CORNERS", "CIRCLE", "ROTATE", "BLUR", "MASK", "MULTI")
            }
            val pagerState = rememberPagerState(0) { tabs.size }
            val coroutineScope = rememberCoroutineScope()
            Column(Modifier.fillMaxWidth()) {
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
                    tabs.forEach {
                        when (it) {
                            "ROUNDED_CORNERS" -> RoundCornersTransformationTestPage()
                            "CIRCLE" -> CircleTransformationTestPage()
                            "ROTATE" -> RotateTransformationTestPage()
                            "BLUR" -> BlurTransformationTestPage()
                            "MASK" -> MaskTransformationTestPage()
                            "MULTI" -> MultiTransformationTestPage()
                        }
                    }
                }
            }
        }
    }
}