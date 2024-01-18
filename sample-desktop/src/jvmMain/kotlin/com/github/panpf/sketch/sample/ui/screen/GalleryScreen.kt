package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.resources.ResourcesImages
import com.github.panpf.sketch.sample.ui.navigation.Navigation
import com.github.panpf.sketch.util.Logger


@Composable
@Preview
fun GalleryScreen(navigation: Navigation) {
    val imageResourceList = remember {
        listOf(
            ResourcesImages.jpeg,
            ResourcesImages.png,
            ResourcesImages.webp,
            ResourcesImages.bmp,
            ResourcesImages.animGif,
            ResourcesImages.animWebp
        )
    }
    val divider = Arrangement.spacedBy(4.dp)
    val platformContext = LocalPlatformContext.current
    val sketch = remember {
        Sketch.Builder(platformContext).apply {
            logger(Logger(Logger.Level.DEBUG))
        }.build()
    }

    Box(Modifier.fillMaxSize()) {
        val state: LazyGridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            horizontalArrangement = divider,
            verticalArrangement = divider,
            modifier = Modifier.fillMaxSize(),
            state = state,
        ) {
            itemsIndexed(imageResourceList) { index, imageResource ->
//                Image(
//                    painter = painterResource(imageResource.fileName),
//                    contentDescription = "image",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(1f)
//                        .clickable {
////                            navigation.push(Page.Slideshow(imageResourceList, index))
//                        }
//                )
                AsyncImage(
                    imageUri = imageResource.uri,
                    sketch = sketch,
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable {
//                            navigation.push(Page.Slideshow(imageResourceList, index))
                        }
                )
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}