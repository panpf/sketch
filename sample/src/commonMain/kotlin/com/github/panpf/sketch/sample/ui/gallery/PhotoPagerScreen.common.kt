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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
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
import com.github.panpf.sketch.sample.EventBus
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_arrow_left
import com.github.panpf.sketch.sample.resources.ic_arrow_right
import com.github.panpf.sketch.sample.resources.ic_image2_baseline
import com.github.panpf.sketch.sample.resources.ic_image2_outline
import com.github.panpf.sketch.sample.resources.ic_settings
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.setting.AppSettingsDialog
import com.github.panpf.sketch.sample.ui.setting.Page.ZOOM
import com.github.panpf.sketch.sample.ui.util.isEmpty
import com.github.panpf.sketch.sample.util.isMobile
import com.github.panpf.sketch.sample.util.runtimePlatformInstance
import com.github.panpf.sketch.transform.BlurTransformation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

expect fun getTopMargin(context: PlatformContext): Int

class PhotoPagerScreen(private val params: PhotoPagerParams) : BaseScreen() {

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    override fun DrawContent() {
        Box(Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                val initialPage = params.initialPosition - params.startPosition
                val pagerState = rememberPagerState(initialPage = initialPage) {
                    params.photos.size
                }

                val uri = params.photos[pagerState.currentPage].listThumbnailUrl
                val colorScheme = MaterialTheme.colorScheme
                val photoPaletteState = remember { mutableStateOf(PhotoPalette(colorScheme)) }
                PagerBackground(uri, photoPaletteState)

                HorizontalPager(
                    state = pagerState,
                    beyondBoundsPageCount = 0,
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    PhotoViewer(
                        photo = params.photos[index],
                        photoPaletteState = photoPaletteState,
                    )
                }

                Headers(pagerState, photoPaletteState)
                TurnPageIndicator(pagerState, photoPaletteState)
            }
        }
    }

    @Composable
    fun PagerBackground(
        imageUri: String,
        photoPaletteState: MutableState<PhotoPalette>,
    ) {
        val colorScheme = MaterialTheme.colorScheme
        val imageState = rememberAsyncImageState()
        LaunchedEffect(Unit) {
            snapshotFlow { imageState.result }.collect {
                if (it is ImageResult.Success) {
                    photoPaletteState.value =
                        PhotoPalette(it.simplePalette, colorScheme = colorScheme)
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

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun Headers(
        pagerState: PagerState,
        photoPaletteState: MutableState<PhotoPalette>,
    ) {
        val context = LocalPlatformContext.current
        val density = LocalDensity.current
        val toolbarTopMarginDp = remember {
            val toolbarTopMargin = getTopMargin(context)
            with(density) { toolbarTopMargin.toDp() }
        }
        val photoPalette by photoPaletteState
        Box(modifier = Modifier.fillMaxSize().padding(top = toolbarTopMarginDp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                val navigator = LocalNavigator.current!!
                IconButton(
                    onClick = { navigator.pop() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = photoPalette.containerColor,
                        contentColor = photoPalette.contentColor
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(40.dp)
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
                    painterResource(Res.drawable.ic_image2_baseline) else painterResource(Res.drawable.ic_image2_outline)
                val coroutineScope = rememberCoroutineScope()
                IconButton(
                    onClick = {
                        val newValue = !appSettings.showOriginImage.value
                        appSettings.showOriginImage.value = newValue
                        coroutineScope.launch {
                            if (newValue) {
                                EventBus.toastFlow.emit("Now show original image")
                            } else {
                                EventBus.toastFlow.emit("Now show thumbnails image")
                            }
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = photoPalette.containerColor,
                        contentColor = photoPalette.contentColor
                    ),
                ) {
                    Icon(
                        painter = image2IconPainter,
                        contentDescription = "show origin image",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Box(
                    Modifier
                        .width(40.dp)
                        .background(
                            color = photoPalette.containerColor,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val number by remember {
                        derivedStateOf {
                            (pagerState.currentPage + 1).coerceAtMost(999)
                        }
                    }
                    val numberCount by remember {
                        derivedStateOf {
                            (params.startPosition + params.photos.size).coerceAtMost(999)
                        }
                    }
                    val numberText by remember {
                        derivedStateOf {
                            "${number}\n·\n$numberCount"
                        }
                    }
                    Text(
                        text = numberText,
                        textAlign = TextAlign.Center,
                        color = photoPalette.contentColor,
                        style = TextStyle(lineHeight = 12.sp),
                        modifier = Modifier
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                var showSettingsDialog by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { showSettingsDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = photoPalette.containerColor,
                        contentColor = photoPalette.contentColor
                    ),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_settings),
                        contentDescription = "settings",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                    )
                }
                if (showSettingsDialog) {
                    AppSettingsDialog(page = ZOOM) {
                        showSettingsDialog = false
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    fun BoxScope.TurnPageIndicator(
        pagerState: PagerState,
        photoPaletteState: MutableState<PhotoPalette>,
    ) {
        if (runtimePlatformInstance.isMobile()) return
        val turnPage = remember { MutableSharedFlow<Boolean>() }
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            EventBus.keyEvent.collect { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && !keyEvent.isMetaPressed) {
                    when (keyEvent.key) {
                        Key.PageUp, Key.DirectionLeft -> turnPage.emit(true)
                        Key.PageDown, Key.DirectionRight -> turnPage.emit(false)
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            turnPage.collect { previousPage ->
                if (previousPage) {
                    val nextPageIndex = (pagerState.currentPage + 1) % pagerState.pageCount
                    pagerState.animateScrollToPage(nextPageIndex)
                } else {
                    val nextPageIndex =
                        (pagerState.currentPage - 1).let { if (it < 0) pagerState.pageCount + it else it }
                    pagerState.animateScrollToPage(nextPageIndex)
                }
            }
        }
        val turnPageIconModifier = Modifier
            .padding(20.dp)
            .size(50.dp)
            .clip(CircleShape)
        val photoPalette by photoPaletteState
        IconButton(
            onClick = { coroutineScope.launch { turnPage.emit(false) } },
            modifier = turnPageIconModifier.align(Alignment.CenterStart),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = photoPalette.containerColor,
                contentColor = photoPalette.contentColor
            ),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = "Previous",
            )
        }
        IconButton(
            onClick = { coroutineScope.launch { turnPage.emit(true) } },
            modifier = turnPageIconModifier.align(Alignment.CenterEnd),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = photoPalette.containerColor,
                contentColor = photoPalette.contentColor
            ),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = "Next",
            )
        }
    }
}