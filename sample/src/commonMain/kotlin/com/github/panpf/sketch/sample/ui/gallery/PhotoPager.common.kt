package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.resources.Res.drawable
import com.github.panpf.sketch.sample.resources.ic_image2_baseline
import com.github.panpf.sketch.sample.resources.ic_image2_outline
import com.github.panpf.sketch.sample.resources.ic_settings
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.setting.AppSettingsDialog
import com.github.panpf.sketch.sample.ui.setting.Page.ZOOM
import com.github.panpf.sketch.sample.ui.util.isEmpty
import com.github.panpf.sketch.transform.BlurTransformation
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PhotoPager(
    photos: List<Photo>,
    initialPosition: Int,
    startPosition: Int,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = initialPosition - startPosition) {
            photos.size
        }

        val uri = photos[pagerState.currentPage].let {
            it.thumbnailUrl ?: it.mediumUrl ?: it.originalUrl
        }
        val colorScheme = MaterialTheme.colorScheme
        val buttonBgColorState = remember { mutableStateOf(colorScheme.primary) }
        PagerBackground(uri, buttonBgColorState)

        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 0,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            PhotoViewer(
                photo = photos[index],
                buttonBgColorState = buttonBgColorState,
            )
        }

        PagerTools(
            pageNumber = startPosition + pagerState.currentPage + 1,
            pageCount = startPosition + photos.size,
            buttonBgColorState = buttonBgColorState,
            pagerState = pagerState,
        )
    }
}

@Composable
fun PagerBackground(
    imageUri: String,
    buttonBgColorState: MutableState<Color>,
) {
    val imageState = rememberAsyncImageState()
    LaunchedEffect(Unit) {
        snapshotFlow { imageState.result }.collect {
            if (it is ImageResult.Success) {
                val preferredSwatch = it.simplePalette?.run {
                    listOfNotNull(
                        darkMutedSwatch,
                        mutedSwatch,
                        lightMutedSwatch,
                        darkVibrantSwatch,
                        vibrantSwatch,
                        lightVibrantSwatch,
                    ).firstOrNull()
                }
                if (preferredSwatch != null) {
                    buttonBgColorState.value = Color(preferredSwatch.rgb).copy(0.6f)
                }
            }
        }
    }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = Modifier.fillMaxSize().onSizeChanged {
            imageSize = IntSize(it.width / 4, it.height / 4)
        }
    ) {
        val context = LocalPlatformContext.current
        val request by remember(imageUri) {
            derivedStateOf {
                if (imageSize.isEmpty()) {
                    null
                } else {
                    ImageRequest(context, imageUri) {
                        resize(
                            width = imageSize.width,
                            height = imageSize.height,
                            precision = SMALLER_SIZE
                        )
                        addTransformations(
                            BlurTransformation(radius = 20, maskColor = 0x63000000)
                        )
                        memoryCachePolicy(DISABLED)
                        resultCachePolicy(DISABLED)
                        disallowAnimatedImage()
                        crossfade(alwaysUse = true, durationMillis = 400)
                        resizeOnDraw()
                        components {
                            addDecodeInterceptor(PaletteDecodeInterceptor())
                        }
                    }
                }
            }
        }
        val request1 = request
        if (request1 != null) {
            AsyncImage(
                request = request1,
                state = imageState,
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

expect fun getTopMargin(context: PlatformContext): Int

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerTools(
    pageNumber: Int,
    pageCount: Int,
    buttonBgColorState: MutableState<Color>,
    pagerState: PagerState,
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    val context = LocalPlatformContext.current
    val density = LocalDensity.current
    val toolbarTopMarginDp = remember {
        val toolbarTopMargin = getTopMargin(context)
        with(density) { toolbarTopMargin.toDp() }
    }
    val buttonBgColor = buttonBgColorState.value
    val buttonTextColor = Color.White
    val navigator = LocalNavigator.current!!

    Box(modifier = Modifier.fillMaxSize().padding(top = toolbarTopMarginDp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            IconButton(onClick = { navigator.pop() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColorState.value)
                        .padding(8.dp),
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp), // margin,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val appSettings = LocalPlatformContext.current.appSettings
            val showOriginImage by appSettings.showOriginImage.collectAsState()
            val image2IconPainter = if (showOriginImage)
                painterResource(drawable.ic_image2_baseline) else painterResource(drawable.ic_image2_outline)
            val coroutineScope = rememberCoroutineScope()
            IconButton(onClick = {
                val newValue = !appSettings.showOriginImage.value
                appSettings.showOriginImage.value = newValue
                coroutineScope.launch {
                    if (newValue) {
                        MyEvents.toastFlow.emit("Now show original image")
                    } else {
                        MyEvents.toastFlow.emit("Now show thumbnails image")
                    }
                }
            }) {
                Icon(
                    painter = image2IconPainter,
                    contentDescription = "show origin image",
                    tint = buttonTextColor,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColor)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Box(
                Modifier
                    .width(40.dp)
                    .background(
                        color = buttonBgColor,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${pageNumber.coerceAtMost(999)}\n·\n${pageCount.coerceAtMost(999)}",
                    textAlign = TextAlign.Center,
                    color = buttonTextColor,
                    style = TextStyle(lineHeight = 12.sp),
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(onClick = { showSettingsDialog = true }) {
                Icon(
                    painter = painterResource(drawable.ic_settings),
                    contentDescription = "settings",
                    tint = buttonTextColor,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = buttonBgColor)
                        .padding(8.dp)
                )
            }
        }

        PlatformPagerTools(buttonBgColorState, pagerState)

        if (showSettingsDialog) {
            AppSettingsDialog(page = ZOOM) {
                showSettingsDialog = false
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
expect fun BoxScope.PlatformPagerTools(
    buttonBgColorState: MutableState<Color>,
    pagerState: PagerState
)