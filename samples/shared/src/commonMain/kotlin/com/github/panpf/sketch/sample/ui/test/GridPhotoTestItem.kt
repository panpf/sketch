package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.ability.dataFromLogo
import com.github.panpf.sketch.ability.progressIndicator
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.Res.drawable
import com.github.panpf.sketch.sample.ic_image_broken_outline
import com.github.panpf.sketch.sample.ic_image_outline
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import com.github.panpf.sketch.sample.ui.util.rememberThemeSectorProgressPainter
import com.github.panpf.sketch.state.rememberIconPainterStateImage

@Composable
fun GridPhotoTestItem(item: PhotoTestItem) {
    Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
        val context = LocalPlatformContext.current
        val placeholderStateImage = rememberIconPainterStateImage(
            icon = drawable.ic_image_outline,
            background = colorScheme.primaryContainer,
            iconTint = colorScheme.onPrimaryContainer
        )
        val errorStateImage = rememberIconPainterStateImage(
            icon = drawable.ic_image_broken_outline,
            background = colorScheme.primaryContainer,
            iconTint = colorScheme.onPrimaryContainer
        )
        val request = remember(item) {
            ImageRequest(context, item.photoUri) {
                placeholder(placeholderStateImage)
                error(errorStateImage)
                crossfade()
                resizeOnDraw()
                sizeMultiplier(2f)  // To get a clearer thumbnail
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
                if (item.imageDecoder != null) {
                    addComponents {
                        add(item.imageDecoder)
                    }
                }
                if (item.imageFetcher != null) {
                    addComponents {
                        add(item.imageFetcher)
                    }
                }
            }
        }
        val imageState = rememberAsyncImageState()
        MyAsyncImage(
            request = request,
            state = imageState,
            contentScale = ContentScale.Crop,
            contentDescription = "photo",
            modifier = Modifier
                .fillMaxSize()
                .dataFromLogo(imageState)
                .progressIndicator(
                    state = imageState,
                    progressPainter = rememberThemeSectorProgressPainter()
                ),
        )

        val title = remember(item.apiSupport, item.title) {
            if (item.apiSupport) {
                item.title
            } else if (item.title?.isNotEmpty() == true) {
                "${item.title} (API Level Not Supported)"
            } else {
                "API Level Not Supported"
            }
        }
        if (title != null) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}