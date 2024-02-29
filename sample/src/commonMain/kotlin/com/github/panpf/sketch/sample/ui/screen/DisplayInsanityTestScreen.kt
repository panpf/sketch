package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.ui.screen.base.BaseScreen
import com.github.panpf.sketch.sample.ui.screen.base.ToolbarScaffold

class DisplayInsanityTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "DisplayInsanityTest") {
            val context = LocalPlatformContext.current
            val sketch = SingletonSketch.get(context)
            val viewModel = rememberScreenModel {
                InsanityTestViewModel(context, sketch)
            }
            PhotoGrid(
                photoPagingFlow = viewModel.pagingFlow,
                animatedPlaceholder = false,
                gridCellsMinSize = 100.dp,
                onClick = { _, _, _ -> },
            )
        }
    }
}

class InsanityTestViewModel(context: PlatformContext, sketch: Sketch) : ScreenModel {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 80,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            InsanityTestPagingSource(context, sketch)
        }
    ).flow.cachedIn(screenModelScope)
}