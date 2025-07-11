package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image2_baseline
import com.github.panpf.sketch.sample.resources.ic_image2_outline
import com.github.panpf.sketch.sample.resources.ic_settings
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.components.MyDialog
import com.github.panpf.sketch.sample.ui.components.TurnPageIndicator
import com.github.panpf.sketch.sample.ui.components.rememberMyDialogState
import com.github.panpf.sketch.sample.ui.setting.AppSettingsList
import com.github.panpf.sketch.sample.ui.setting.Page.ZOOM
import com.github.panpf.sketch.sample.util.Platform
import com.github.panpf.sketch.sample.util.current
import com.github.panpf.sketch.sample.util.isMobile
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.util.toSketchSize
import com.github.panpf.sketch.util.windowContainerSize
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

expect fun getTopMargin(context: PlatformContext): Int

class PhotoPagerScreen(private val params: PhotoPagerParams) : BaseScreen() {

    @Composable
    override fun DrawContent() {
        val appEvents: AppEvents = koinInject()
        val coroutineScope = rememberCoroutineScope()
        val focusRequest = remember { androidx.compose.ui.focus.FocusRequester() }
        Box(
            Modifier
                .fillMaxSize()
                .focusable()
                .focusRequester(focusRequest)
                .onKeyEvent {
                    coroutineScope.launch {
                        appEvents.keyEvent.emit(it)
                    }
                    true
                }
        ) {
            val initialPage = params.initialPosition - params.startPosition
            val pagerState = rememberPagerState(initialPage = initialPage) {
                params.photos.size
            }

            val uri = params.photos[pagerState.currentPage].listThumbnailUrl
            val colorScheme = MaterialTheme.colorScheme
            val photoPaletteState = remember { mutableStateOf(PhotoPalette(colorScheme)) }
            PhotoPagerBackground(uri, photoPaletteState)

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                val pageSelected by remember {
                    derivedStateOf {
                        pagerState.currentPage == index
                    }
                }
                PhotoViewer(
                    photo = params.photos[index],
                    photoPaletteState = photoPaletteState,
                    pageSelected = pageSelected,
                )
            }

            PhotoPagerHeaders(params, pagerState, photoPaletteState)

            TurnPageIndicator(pagerState, photoPaletteState)

            if (!Platform.current.isMobile()) {
                GestureDialog()
            }
        }
        LaunchedEffect(Unit) {
            focusRequest.requestFocus()
        }
    }
}

@Composable
fun PhotoPagerBackground(
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
    // Cache the image size to prevent reloading the image when the window size changes
    val windowsSize = windowContainerSize()
    val imageSize = remember { (windowsSize / 4).toSketchSize() }
    val request = ComposableImageRequest(imageUri) {
        resize(size = imageSize, precision = SMALLER_SIZE)
        addTransformations(BlurTransformation(radius = 20, maskColor = 0x63000000))
        memoryCachePolicy(DISABLED)
        resultCachePolicy(DISABLED)
        disallowAnimatedImage()
        crossfade(alwaysUse = true, durationMillis = 400)
        resizeOnDraw()
        components {
            addDecodeInterceptor(PaletteDecodeInterceptor())
        }
    }
    AsyncImage(
        request = request,
        state = imageState,
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun PhotoPagerHeaders(
    params: PhotoPagerParams,
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
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Spacer(Modifier.weight(1f))

            val appEvents: AppEvents = koinInject()
            val appSettings: AppSettings = koinInject()
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
                            appEvents.toastFlow.emit("Now show original image")
                        } else {
                            appEvents.toastFlow.emit("Now show thumbnails image")
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


            val settingsDialogState = rememberMyDialogState()
            IconButton(
                onClick = { settingsDialogState.show() },
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
            MyDialog(settingsDialogState) {
                AppSettingsList(ZOOM)
            }

            Spacer(modifier = Modifier.size(10.dp))

            Box(
                Modifier
                    .height(40.dp)
                    .background(
                        color = photoPalette.containerColor,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                val numberText by remember {
                    derivedStateOf {
                        val number = params.startPosition + pagerState.currentPage + 1
                        "${number}/${params.totalCount}"
                    }
                }
                Text(
                    text = numberText,
                    textAlign = TextAlign.Center,
                    color = photoPalette.contentColor,
                    style = TextStyle(lineHeight = 12.sp),
                )
            }
        }
    }
}

@Composable
fun GestureDialog() {
    val appSettings: AppSettings = koinInject()
    val pagerGuideShowed by appSettings.pagerGuideShowed.collectAsState()
    var showPagerGuide by remember { mutableStateOf(true) }
    if (!pagerGuideShowed && showPagerGuide) {
        AlertDialog(
            onDismissRequest = { showPagerGuide = false },
            title = { Text("Operation gestures") },
            text = {
                Text(
                    text = """The current page supports the following gestures or operations：
                            |1. Turn page:
                            |    1.2. Key.LeftBracket + (meta/ctrl)/alt, Key.RightBracket + (meta/ctrl)/alt
                            |    1.3. Key.DirectionLeft + (meta/ctrl)/alt, Key.DirectionRight + (meta/ctrl)/alt
                            |2. Scaling image：
                            |    2.1. Double-click the image with one finger
                            |    2.2. Double-click the image with one finger and slide up and down without letting go.
                            |    2.3. Pinch with two fingers
                            |    2.4. Mouse scroll scaling
                            |    2.5. Key.ZoomIn, Key.ZoomOut, 
                            |        Key.Equals + (meta/ctrl)/alt, Key.Minus + (meta/ctrl)/alt, 
                            |        Key.DirectionUp + (meta/ctrl)/alt, Key.DirectionDown + (meta/ctrl)/alt
                            |3. Moving image：
                            |    3.1. Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight
                            """.trimMargin(),
                    fontSize = 12.sp
                )
            },
            dismissButton = {
                Button(onClick = { showPagerGuide = false }) {
                    Text("I Known")
                }
            },
            confirmButton = {
                Button(onClick = { appSettings.pagerGuideShowed.value = true }) {
                    Text("Not prompting")
                }
            }
        )
    }
}