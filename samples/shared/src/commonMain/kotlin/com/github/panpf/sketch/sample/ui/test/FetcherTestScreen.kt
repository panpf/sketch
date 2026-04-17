package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.common.listContentPaddingWithNavigationBarsWindowInset
import com.github.panpf.sketch.sample.ui.util.plus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FetcherTestScreen() {
    BaseScreen {
        ToolbarScaffold(title = "FetcherTest") {
            val gridState = rememberLazyGridState()
            val viewModel: FetcherTestViewModel = koinViewModel()
            val photoTestItems by viewModel.data.collectAsState()
            val windowInsetContentPadding = listContentPaddingWithNavigationBarsWindowInset()
            val contentPadding = remember(windowInsetContentPadding) {
                windowInsetContentPadding + PaddingValues(4.dp)
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(
                    count = photoTestItems.size,
                    contentType = { 1 }
                ) { index ->
                    GridPhotoTestItem(photoTestItems[index])
                }
            }
        }
    }
}