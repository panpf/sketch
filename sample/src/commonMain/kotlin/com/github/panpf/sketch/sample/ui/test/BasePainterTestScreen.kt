package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.util.name

abstract class BasePainterTestScreen : BaseScreen() {

    @Composable
    abstract fun getTitle(): String

    @Composable
    open fun BuildInitial() {
    }

    abstract suspend fun buildPainters(
        context: PlatformContext,
        contentScale: ContentScale,
        alignment: Alignment,
        containerSize: IntSize,
        cells: Int,
        gridDividerSizePx: Float,
    ): List<Pair<String, Painter>>

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = getTitle()) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val context = LocalPlatformContext.current
                var contentScale by remember { mutableStateOf(ContentScale.Fit) }
                var alignment by remember { mutableStateOf(Alignment.Center) }
                val containerSize = LocalWindowInfo.current.containerSize
                val cells = if (containerSize.width > containerSize.height) 5 else 3
                val gridDividerSizeDp = 8.dp
                val gridDividerSizePx = with(LocalDensity.current) { gridDividerSizeDp.toPx() }
                var painterList by remember {
                    mutableStateOf<List<Pair<String, Painter>>>(emptyList())
                }
                BuildInitial()
                LaunchedEffect(contentScale, alignment) {
                    painterList = buildPainters(
                        context,
                        contentScale,
                        alignment,
                        containerSize,
                        cells,
                        gridDividerSizePx
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(cells),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(gridDividerSizeDp),
                    horizontalArrangement = Arrangement.spacedBy(gridDividerSizeDp),
                    verticalArrangement = Arrangement.spacedBy(gridDividerSizeDp),
                ) {
                    items(painterList) { pair ->
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))) {
                            val colorScheme = MaterialTheme.colorScheme
                            BasicText(
                                text = pair.first,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .background(colorScheme.tertiaryContainer)
                                    .padding(4.dp),
                                color = { colorScheme.onTertiaryContainer },
                                autoSize = TextAutoSize.StepBased(minFontSize = 8.sp)
                            )
                            val painter = remember(pair.second) { pair.second }
                            Image(
                                painter = painter,
                                contentDescription = pair.first,
                                contentScale = contentScale,
                                alignment = alignment,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.75f)
                                    .background(colorScheme.primaryContainer)
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val contentScales = remember {
                        listOf(
                            ContentScale.Fit,
                            ContentScale.Crop,
                            ContentScale.Inside,
                            ContentScale.None,
                            ContentScale.FillWidth,
                            ContentScale.FillHeight,
                            ContentScale.FillBounds,
                        )
                    }
                    contentScales.forEach { contentScaleItem ->
                        Button(
                            onClick = { contentScale = contentScaleItem },
                            enabled = contentScaleItem != contentScale,
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(text = contentScaleItem.name, fontSize = 12.sp)
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val alignments = remember {
                        listOf(
                            Alignment.TopStart,
                            Alignment.TopCenter,
                            Alignment.TopEnd,
                            Alignment.CenterStart,
                            Alignment.Center,
                            Alignment.CenterEnd,
                            Alignment.BottomStart,
                            Alignment.BottomCenter,
                            Alignment.BottomEnd,
                        )
                    }
                    alignments.forEach { alignmentItem ->
                        Button(
                            onClick = { alignment = alignmentItem },
                            enabled = alignmentItem != alignment,
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(text = alignmentItem.name, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}