package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_info_baseline
import com.github.panpf.sketch.sample.resources.ic_rotate_right
import com.github.panpf.sketch.sample.resources.ic_save
import com.github.panpf.sketch.sample.resources.ic_share
import com.github.panpf.sketch.sample.resources.ic_zoom_in
import com.github.panpf.sketch.sample.resources.ic_zoom_out
import com.github.panpf.sketch.sample.ui.common.AsyncImagePageState
import com.github.panpf.sketch.sample.ui.components.MyDialog
import com.github.panpf.sketch.sample.ui.components.MyZoomAsyncImage
import com.github.panpf.sketch.sample.ui.components.rememberMyDialogState
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.zoomimage.SketchZoomState
import com.github.panpf.zoomimage.rememberSketchZoomState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun PhotoViewer(
    photo: Photo,
    photoPaletteState: MutableState<PhotoPalette>,
    pageSelected: Boolean,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalPlatformContext.current
        val imageState = rememberAsyncImageState()
        val zoomState = rememberSketchZoomState()
        val infoDialogState = rememberMyDialogState()
        val showOriginImage by context.appSettings.showOriginImage.collectAsState()
        val imageUri = if (showOriginImage)
            photo.originalUrl else photo.mediumUrl ?: photo.originalUrl

        MyZoomAsyncImage(
            uri = imageUri,
            contentDescription = "view image",
            placeholderUri = photo.thumbnailUrl,
            modifier = Modifier.fillMaxSize(),
            imageState = imageState,
            zoomState = zoomState,
            highlightColor = photoPaletteState.value.containerColor,
            pageSelected = pageSelected,
            onLongPress = { infoDialogState.show() }
        )

        PhotoViewerBottomBarWrapper(
            imageUri = imageUri,
            buttonBackgroundColor = photoPaletteState.value.containerColor,
            buttonContentColor = photoPaletteState.value.contentColor,
            zoomState = zoomState,
            onInfoClick = { infoDialogState.show() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .padding(vertical = 30.dp)
        )

        AsyncImagePageState(
            imageState = imageState,
            modifier = Modifier.align(Alignment.Center),
        )

        MyDialog(infoDialogState) {
            PhotoInfo(imageState.result)
        }
    }
}

@Composable
expect fun PhotoViewerBottomBarWrapper(
    imageUri: String,
    modifier: Modifier,
    zoomState: SketchZoomState,
    buttonBackgroundColor: Color?,
    buttonContentColor: Color?,
    onInfoClick: (() -> Unit)?,
)

@Composable
fun PhotoViewerBottomBar(
    modifier: Modifier,
    zoomState: SketchZoomState,
    buttonBackgroundColor: Color?,
    buttonContentColor: Color?,
    onInfoClick: (() -> Unit)?,
    onShareClick: (() -> Unit)?,
    onSaveClick: (() -> Unit)?,
) {
    Row(modifier) {
        val coroutineScope = rememberCoroutineScope()
        val buttonColors = IconButtonDefaults.iconButtonColors(
            containerColor = buttonBackgroundColor ?: Color.Unspecified,
            contentColor = buttonContentColor ?: LocalContentColor.current
        )

        IconButton(
            onClick = { onShareClick?.invoke() },
            colors = buttonColors
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_share),
                contentDescription = "share",
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        IconButton(
            onClick = { onSaveClick?.invoke() },
            colors = buttonColors
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_save),
                contentDescription = "save",
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        val zoomIn by remember {
            derivedStateOf {
                zoomState.zoomable.getNextStepScale() > zoomState.zoomable.transform.scaleX
            }
        }
        IconButton(
            onClick = {
                coroutineScope.launch {
                    val zoomable = zoomState.zoomable
                    val nextStepScale = zoomable.getNextStepScale()
                    zoomable.scale(nextStepScale, animated = true)
                }
            },
            colors = buttonColors
        ) {
            Icon(
                painter = if (zoomIn) {
                    painterResource(Res.drawable.ic_zoom_in)
                } else {
                    painterResource(Res.drawable.ic_zoom_out)
                },
                contentDescription = "zoom",
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        IconButton(
            onClick = {
                coroutineScope.launch {
                    val zoomable = zoomState.zoomable
                    zoomable.rotate(zoomable.transform.rotation.roundToInt() + 90)
                }
            },
            colors = buttonColors
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_rotate_right),
                contentDescription = "right rotate",
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        IconButton(
            onClick = { onInfoClick?.invoke() },
            colors = buttonColors
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_info_baseline),
                contentDescription = "info",
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
            )
        }
    }
}