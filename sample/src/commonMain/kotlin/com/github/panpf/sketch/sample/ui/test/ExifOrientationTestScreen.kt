@file:OptIn(ExperimentalResourceApi::class)

package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.ability.dataFromLogo
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import sketch_root.sample.generated.resources.Res.drawable
import sketch_root.sample.generated.resources.ic_image_outline

class ExifOrientationTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "ExifOrientationTest") {
            val exifImages = MyImages.clockExifs
            val gridState = rememberLazyGridState()
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
                    count = exifImages.size,
                    key = { exifImages[it].uri },
                    contentType = { 1 }
                ) { index ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        val image = exifImages[index]
                        val placeholderStateImage = rememberIconPainterStateImage(
                            icon = drawable.ic_image_outline,
                            background = colorScheme.primaryContainer,
                            iconTint = colorScheme.onPrimaryContainer
                        )
                        val imageState = rememberAsyncImageState()
                        MyAsyncImage(
                            request = ImageRequest(LocalPlatformContext.current, image.uri) {
                                placeholder(placeholderStateImage)
                                crossfade()
                                resizeOnDraw()
                                sizeMultiplier(2f)  // To get a clearer thumbnail
                                memoryCachePolicy(DISABLED)
                                resultCachePolicy(DISABLED)
                            },
                            modifier = Modifier.fillMaxSize().dataFromLogo(imageState),
                            state = imageState,
                            contentScale = ContentScale.Crop,
                            contentDescription = "photo",
                        )

                        Box(
                            Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = image.name,
                                modifier = Modifier.align(Alignment.BottomStart),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}