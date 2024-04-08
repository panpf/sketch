package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells.Adaptive
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.gallery.PhotoGridItem
import com.github.panpf.sketch.sample.ui.model.Photo

class DisplayInsanityTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "DisplayInsanityTest") {
            val photos = remember {
                buildList {
                    var index = 0
                    repeat(100) {
                        MyImages.numbers.forEach { image ->
                            val photo = Photo(
                                originalUrl = image.uri,
                                mediumUrl = null,
                                thumbnailUrl = null,
                                width = image.size.width,
                                height = image.size.height,
                                exifOrientation = image.exifOrientation,
                                index = index++,
                            )
                            add(photo)
                        }
                    }
                }
            }
            val gridState = rememberLazyGridState()
            LazyVerticalGrid(
                columns = Adaptive(100.dp),
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
                        onClick = { _, _ ->
                        },
                    )
                }
            }
        }
    }
}