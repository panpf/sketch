package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_github
import com.github.panpf.sketch.sample.ui.components.AutoLinkText
import com.github.panpf.sketch.sample.util.Platform
import com.github.panpf.sketch.sample.util.current
import com.github.panpf.sketch.sample.util.isMobile
import org.jetbrains.compose.resources.painterResource

expect fun platformTestScreens(): List<TestItem>

@Composable
fun TestPage() {
    val testItems = remember {
        buildList {
            add(TestGroup("Components"))
            add(TestItem("Decoder", DecoderTestScreen()))
            add(TestItem("Fetcher", FetcherTestScreen()))

            add(TestGroup("Functions"))
            add(TestItem("AnimatedImage", AnimatedImageTestScreen()))
            add(TestItem("ExifOrientation", ExifOrientationTestScreen()))
            add(TestItem("Transformation", TransformationTestScreen()))

            add(TestGroup("UI"))
            add(TestItem("CrossfadePainter", CrossfadePainterTestScreen()))
            add(TestItem("ResizePainter", ResizePainterTestScreen()))
            add(TestItem("Painter Mix", MixPainterTestScreen()))
            add(TestItem("IconPainter", IconPainterTestScreen()))
            add(TestItem("AnimatablePlaceholder", AnimatablePlaceholderTestScreen()))
            add(TestItem("Preview", PreviewTestScreen()))
            add(TestItem("ProgressIndicator", ProgressIndicatorTestScreen()))

            val platformTestScreens = platformTestScreens()
            if (platformTestScreens.isNotEmpty()) {
                add(TestGroup("Platform"))
                addAll(platformTestScreens)
            }

            add(TestGroup("Other"))
            add(TestItem("DisplayInsanity", DisplayInsanityTestScreen()))
            add(TestItem("UserZoom", UserZoomTestScreen()))
            add(TestItem("Temp", TempTestScreen()))

            add(ProjectInfo)
        }
    }
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(if (Platform.current.isMobile()) 2 else 4),
        state = gridState,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            count = testItems.size,
            key = {
                when (val data = testItems[it]) {
                    is TestItem -> data.title
                    is TestGroup -> data.title
                    is ProjectInfo -> data.toString()
                    else -> throw IllegalArgumentException("Unknown data type: $data")
                }
            },
            span = {
                when (val data = testItems[it]) {
                    is TestItem -> GridItemSpan(1)
                    is TestGroup -> GridItemSpan(maxLineSpan)
                    is ProjectInfo -> GridItemSpan(maxLineSpan)
                    else -> throw IllegalArgumentException("Unknown data type: $data")
                }
            },
            contentType = {
                when (val data = testItems[it]) {
                    is TestItem -> 1
                    is TestGroup -> 2
                    is ProjectInfo -> 3
                    else -> throw IllegalArgumentException("Unknown data type: $data")
                }
            },
        ) { index ->
            when (val data = testItems[index]) {
                is TestItem -> TestGridItem(data)
                is TestGroup -> TestGroupItem(data)
                is ProjectInfo -> ProjectInfoItem()
                else -> throw IllegalArgumentException("Unknown data type: $data")
            }
        }
    }
}

data class TestItem(val title: String, val screen: Screen)

data class TestGroup(val title: String)

@Composable
fun TestGridItem(item: TestItem) {
    val navigator = LocalNavigator.current!!
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .widthIn(100.dp, 1000.dp)
            .heightIn(60.dp, 1000.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colorScheme.primaryContainer)
            .clickable { navigator.push(item.screen) }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.title,
            color = colorScheme.onPrimaryContainer,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun TestGroupItem(group: TestGroup) {
    Text(
        text = group.title,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 16.dp)
    )
}

data object ProjectInfo {
    override fun toString(): String {
        return "ProjectInfo"
    }
}

@Composable
fun ProjectInfoItem() {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colorScheme.primaryContainer)
            .padding(16.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_github),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(10.dp))
        AutoLinkText(text = "https://github.com/panpf/sketch")
    }
}