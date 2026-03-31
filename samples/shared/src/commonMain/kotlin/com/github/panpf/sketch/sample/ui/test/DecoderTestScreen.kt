package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DecoderTestScreen() {
    BaseScreen {
        ToolbarScaffold(title = "DecoderTest") {
            val gridState = rememberLazyGridState()
            val viewModel: DecoderTestViewModel = koinViewModel()
            val photoTestItems by viewModel.data.collectAsState()
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState,
                contentPadding = PaddingValues(
                    start = 4.dp,
                    top = 4.dp,
                    end = 4.dp,
                    bottom = 84.dp
                ),
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