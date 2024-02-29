package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

expect val gridCells: Int

@Composable
fun TestPage() {
    val testItems = remember {
        listOf(
            TestItem("AnimatablePlaceholder", AnimatablePlaceholderTestScreen()),
            TestItem("Decoder", DecoderTestScreen()),
            TestItem("DisplayInsanity", DisplayInsanityTestScreen()),
            TestItem("ExifOrientation", ExifOrientationTestScreen()),
            TestItem("Fetcher", FetcherTestScreen()),
            TestItem("ProgressIndicator", ProgressIndicatorTestScreen()),
            TestItem("Transformation", TransformationTestScreen()),
            TestItem("Temp", TempTestScreen()),
        )
    }
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridCells),
        state = gridState,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            count = testItems.size,
            key = { testItems[it].title },
            contentType = { 1 }
        ) { index ->
            TestGridItem(testItems[index])
        }
    }
}

data class TestItem(val title: String, val screen: Screen)

@Composable
fun TestGridItem(item: TestItem) {
    val navigator = LocalNavigator.current!!
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .widthIn(100.dp, 1000.dp)
            .heightIn(100.dp, 1000.dp)
            .background(colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp))
            .clickable {
                navigator.push(item.screen)
            }
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