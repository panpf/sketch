package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells.Adaptive
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
import com.github.panpf.sketch.sample.ui.gallery.PhotoGridItem
import com.github.panpf.sketch.sample.ui.util.plus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DisplayInsanityTestScreen() {
    BaseScreen {
        ToolbarScaffold(title = "DisplayInsanityTest") {
            val gridState = rememberLazyGridState()
            val viewModel: DisplayInsanityTestViewModel = koinViewModel()
            val photos by viewModel.data.collectAsState()
            val windowInsetContentPadding = listContentPaddingWithNavigationBarsWindowInset()
            val contentPadding = remember(windowInsetContentPadding) {
                windowInsetContentPadding + PaddingValues(4.dp)
            }
            LazyVerticalGrid(
                columns = Adaptive(100.dp),
                state = gridState,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(
                    count = photos.size,
                    key = { "${photos[it].originalUrl}:${it}" },
                    contentType = { 1 }
                ) { index ->
                    val item = photos[index]
                    PhotoGridItem(
                        index = index,
                        photo = item,
                        animatedPlaceholder = false,
                        staggeredGridMode = false,
                        onClick = { _, _ -> },
                    )
                }
            }
        }
    }
}
