package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.rememberIconImage2BaselinePainter
import com.github.panpf.sketch.sample.ui.rememberIconImage2OutlinePainter
import com.github.panpf.sketch.sample.ui.rememberIconSettingsPainter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlin.math.roundToInt

@Composable
fun rememberPhotoPagerEvents(): PhotoPagerEvents {
    return remember { PhotoPagerEvents() }
}

class PhotoPagerEvents {
    val nextPageFlow = MutableSharedFlow<Unit>()
    val previousPageFlow = MutableSharedFlow<Unit>()
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PhotoPager(
    imageList: List<ImageDetail>,
    initialPosition: Int,
    startPosition: Int,
    totalCount: Int,
    photoPagerEvents: PhotoPagerEvents = rememberPhotoPagerEvents(),
    onSettingsClick: () -> Unit,
    onShowOriginClick: () -> Unit,
    onShareClick: (ImageDetail) -> Unit,
    onSaveClick: (ImageDetail) -> Unit,
    onImageClick: () -> Unit,
    onImageLongClick: (ImageResult) -> Unit,
    onInfoClick: (ImageResult) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = initialPosition - startPosition) {
            imageList.size
        }
        LaunchedEffect(Unit) {
            photoPagerEvents.previousPageFlow.collect {
                val nextPageIndex =
                    (pagerState.currentPage - 1).let { if (it < 0) pagerState.pageCount + it else it }
                pagerState.animateScrollToPage(nextPageIndex)
            }
        }
        LaunchedEffect(Unit) {
            photoPagerEvents.nextPageFlow.collect {
                val nextPageIndex = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPageIndex)
            }
        }

        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.toPx() }.roundToInt()
        val maxHeightPx = with(density) { maxHeight.toPx() }.roundToInt()

        val uriString = imageList[pagerState.currentPage].let {
            it.thumbnailUrl ?: it.mediumUrl ?: it.originUrl
        }
        val colorScheme = MaterialTheme.colorScheme
        val buttonBgColorState = remember { mutableStateOf(colorScheme.primary) }
        PagerBackground(uriString, buttonBgColorState, IntSize(maxWidthPx, maxHeightPx))

        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 0,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            PhotoViewer(
                imageDetail = imageList[index],
                buttonBgColorState = buttonBgColorState,
                onClick = onImageClick,
                onLongClick = onImageLongClick,
                onInfoClick = onInfoClick,
                onShareClick = {
                    onShareClick.invoke(imageList[pagerState.currentPage])
                },
                onSaveClick = {
                    onSaveClick.invoke(imageList[pagerState.currentPage])
                },
            )
        }

        val showOriginImage by LocalPlatformContext.current.appSettings.showOriginImage.collectAsState()
        PagerTools(
            pageNumber = startPosition + pagerState.currentPage + 1,
            pageCount = totalCount,
            showOriginImage = showOriginImage,
            buttonBgColorState = buttonBgColorState,
            onSettingsClick = onSettingsClick,
            onShowOriginClick = onShowOriginClick,
        )
    }
}

@Composable
expect fun PagerBackground(
    imageUri: String,
    buttonBgColorState: MutableState<Color>,
    screenSize: IntSize,
)

expect fun getTopMargin(context: PlatformContext): Int

@Composable
private fun PagerTools(
    pageNumber: Int,
    pageCount: Int,
    showOriginImage: Boolean,
    buttonBgColorState: MutableState<Color>,
    onSettingsClick: () -> Unit,
    onShowOriginClick: () -> Unit,
) {
    val context = LocalPlatformContext.current
    val density = LocalDensity.current
    val toolbarTopMarginDp = remember {
        val toolbarTopMargin = getTopMargin(context)
        with(density) { toolbarTopMargin.toDp() }
    }
    val buttonBgColor = buttonBgColorState.value
    val buttonTextColor = Color.White

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = toolbarTopMarginDp)
                .padding(20.dp), // margin,
        ) {
            val buttonModifier = Modifier
                .size(40.dp)
                .background(
                    color = buttonBgColor,
                    shape = RoundedCornerShape(50)
                )
                .padding(8.dp)
            IconButton(
                modifier = buttonModifier,
                onClick = { onShowOriginClick.invoke() },
            ) {
                val image2IconPainter = if (showOriginImage)
                    rememberIconImage2BaselinePainter() else rememberIconImage2OutlinePainter()
                Icon(
                    painter = image2IconPainter,
                    contentDescription = "show origin image",
                    tint = buttonTextColor
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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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

            IconButton(
                modifier = buttonModifier,
                onClick = { onSettingsClick.invoke() },
            ) {
                Icon(
                    painter = rememberIconSettingsPainter(),
                    contentDescription = "settings",
                    tint = buttonTextColor
                )
            }
        }
    }
}