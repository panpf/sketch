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
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.util.name

expect suspend fun buildPainterContentScaleTestPainters(
    context: PlatformContext,
    contentScale: ContentScale
): List<Pair<String, Painter>>

class PainterContentScaleTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "Painter ContentScale") {
            Column(
                modifier = Modifier.fillMaxSize()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val context = LocalPlatformContext.current
                var contentScale by remember { mutableStateOf(ContentScale.Fit) }
                var painterList by remember {
                    mutableStateOf<List<Pair<String, Painter>>?>(null)
                }
                LaunchedEffect(contentScale) {
                    painterList = buildPainterContentScaleTestPainters(context, contentScale)
                }

                val painterList1 = painterList
                if (painterList1 != null) {
                    val containerSize = LocalWindowInfo.current.containerSize
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (containerSize.width > containerSize.height) 5 else 3),
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(painterList1) { pair ->
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
                                val painter = remember { pair.second }
                                Image(
                                    painter = painter,
                                    contentDescription = pair.first,
                                    contentScale = contentScale,
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                }
            }
        }
    }
}