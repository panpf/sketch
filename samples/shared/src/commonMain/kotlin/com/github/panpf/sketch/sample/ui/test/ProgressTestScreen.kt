package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.ability.mimeTypeLogo
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.supportComposeResHttpUri
import com.github.panpf.sketch.images.toComposeResHttpUri
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.gridCellsMinSize
import com.github.panpf.sketch.sample.ui.util.rememberMimeTypeLogoMap
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter

class ProgressTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        val uris = remember {
            ComposeResImageFiles.values.map { it.toComposeResHttpUri() }
        }
        ToolbarScaffold(title = "ProgressTest") {
            val gridState = rememberLazyGridState()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(gridCellsMinSize),
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
                items(uris) { uri ->
                    val context = LocalPlatformContext.current
                    val imageState = rememberAsyncImageState()
                    val progressPainter = rememberThemeSectorProgressPainter()
                    val mimeTypeLogoMap = rememberMimeTypeLogoMap()
                    val modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .progressIndicator(imageState, progressPainter)
                        .mimeTypeLogo(imageState, mimeTypeLogoMap, margin = 4.dp)
                    val request = remember(uri) {
                        ImageRequest(context, uri) {
                            memoryCachePolicy(CachePolicy.DISABLED)
                            resultCachePolicy(CachePolicy.DISABLED)
                            downloadCachePolicy(CachePolicy.DISABLED)
                            components {
                                supportComposeResHttpUri(context)
                            }
                        }
                    }
                    AsyncImage(
                        request = request,
                        state = imageState,
                        modifier = modifier,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        contentDescription = "photo",
                    )
                }
            }
        }
    }
}