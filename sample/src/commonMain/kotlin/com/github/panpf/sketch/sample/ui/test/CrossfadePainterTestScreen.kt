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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.numbers
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.util.SizeColorPainter
import com.github.panpf.sketch.sample.ui.util.WrapperPainter
import com.github.panpf.sketch.sample.ui.util.name
import com.github.panpf.sketch.sample.ui.util.toImageBitmap
import com.github.panpf.sketch.transition.CrossfadeTransition
import org.jetbrains.compose.resources.painterResource

class CrossfadePainterTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "CrossfadePainter") {
            Column(
                modifier = Modifier.fillMaxSize()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val containerSize = LocalWindowInfo.current.containerSize
                val cells = if (containerSize.width > containerSize.height) 5 else 3
                var contentScale by remember { mutableStateOf(ContentScale.Fit) }
                var alignment by remember { mutableStateOf(Alignment.Center) }
                val dividerSizeDp = 8.dp
                val dividerSizePx = with(LocalDensity.current) { dividerSizeDp.toPx() }
                val numbersPainter = painterResource(Res.drawable.numbers)
                val density = LocalDensity.current
                val layoutDirection = LocalLayoutDirection.current
                val painterList by remember {
                    derivedStateOf {
                        val gridWidth = (containerSize.width - (cells + 1) * dividerSizePx) / cells
                        val startPainterWidth = gridWidth * 0.75f
                        val endPainterWidth = gridWidth * 0.5f
                        val endImageBitmap = numbersPainter.toImageBitmap(
                            density = density,
                            layoutDirection = layoutDirection,
                            size = Size(endPainterWidth, endPainterWidth)
                        )
                        val endPainter = BitmapPainter(endImageBitmap)
                        mutableListOf<Pair<String, Painter>>(
                            "Default" to CrossfadePainter(
                                start = SizeColorPainter(
                                    Color.Blue,
                                    Size(startPainterWidth, startPainterWidth)
                                ),
                                end = endPainter,
                                contentScale = contentScale,
                                alignment = alignment,
                            ),
                            "Long Duration" to CrossfadePainter(
                                start = SizeColorPainter(
                                    Color.Blue,
                                    Size(startPainterWidth, startPainterWidth)
                                ),
                                end = endPainter,
                                contentScale = contentScale,
                                alignment = alignment,
                                durationMillis = CrossfadeTransition.DEFAULT_DURATION_MILLIS * 4
                            ),
                            "No fadeStart" to CrossfadePainter(
                                start = SizeColorPainter(
                                    Color.Blue,
                                    Size(startPainterWidth, startPainterWidth)
                                ),
                                end = endPainter,
                                contentScale = contentScale,
                                alignment = alignment,
                                fadeStart = !CrossfadeTransition.DEFAULT_FADE_START
                            ),
                            "PreferExactIntrinsicSize" to CrossfadePainter(
                                start = SizeColorPainter(
                                    Color.Blue,
                                    Size(startPainterWidth, startPainterWidth)
                                ),
                                end = endPainter,
                                contentScale = contentScale,
                                alignment = alignment,
                                preferExactIntrinsicSize = !CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE
                            ),
                        ).map {
                            it.first to WrapperPainter(
                                it.second,
                                Color.Green.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(cells),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(dividerSizeDp),
                    horizontalArrangement = Arrangement.spacedBy(dividerSizeDp),
                    verticalArrangement = Arrangement.spacedBy(dividerSizeDp),
                ) {
                    items(items = painterList) { pair ->
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
                    modifier = Modifier.fillMaxWidth().padding(dividerSizeDp),
                    horizontalArrangement = Arrangement.spacedBy(dividerSizeDp)
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
                            contentPadding = PaddingValues(4.dp),
                        ) {
                            Text(text = contentScaleItem.name, fontSize = 12.sp)
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(dividerSizeDp),
                    horizontalArrangement = Arrangement.spacedBy(dividerSizeDp)
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
                            contentPadding = PaddingValues(4.dp),
                        ) {
                            Text(text = alignmentItem.name, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}