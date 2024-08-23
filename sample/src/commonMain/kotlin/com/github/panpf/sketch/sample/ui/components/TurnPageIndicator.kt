package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.EventBus
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_arrow_left
import com.github.panpf.sketch.sample.resources.ic_arrow_right
import com.github.panpf.sketch.sample.util.isMobile
import com.github.panpf.sketch.sample.util.runtimePlatformInstance
import com.github.panpf.zoomimage.compose.util.AssistKey
import com.github.panpf.zoomimage.compose.util.KeyMatcher
import com.github.panpf.zoomimage.compose.util.matcherKeyHandler
import com.github.panpf.zoomimage.compose.util.platformAssistKey
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
@OptIn(ExperimentalFoundationApi::class)
fun TurnPageIndicator(
    pagerState: PagerState,
    photoPaletteState: MutableState<PhotoPalette>,
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val keyHandlers = listOf(
            matcherKeyHandler(
                listOf(
                    KeyMatcher(Key.LeftBracket, AssistKey.Alt, type = KeyEventType.KeyUp),
                    KeyMatcher(Key.LeftBracket, platformAssistKey(), type = KeyEventType.KeyUp),
                    KeyMatcher(Key.DirectionLeft, AssistKey.Alt, type = KeyEventType.KeyUp),
                    KeyMatcher(
                        Key.DirectionLeft,
                        platformAssistKey(),
                        type = KeyEventType.KeyUp
                    ),
                )
            ) {
                coroutineScope.launch {
                    pagerState.previousPage()
                }
            },
            matcherKeyHandler(
                listOf(
                    KeyMatcher(Key.RightBracket, AssistKey.Alt, type = KeyEventType.KeyUp),
                    KeyMatcher(
                        Key.RightBracket,
                        platformAssistKey(),
                        type = KeyEventType.KeyUp
                    ),
                    KeyMatcher(Key.DirectionRight, AssistKey.Alt, type = KeyEventType.KeyUp),
                    KeyMatcher(
                        Key.DirectionRight,
                        platformAssistKey(),
                        type = KeyEventType.KeyUp
                    ),
                )
            ) {
                coroutineScope.launch {
                    pagerState.nextPage()
                }
            }
        )
        EventBus.keyEvent.collect { keyEvent ->
            keyHandlers.any {
                it.handle(keyEvent)
            }
        }
    }
    if (!runtimePlatformInstance.isMobile()) {
        Box(Modifier.fillMaxSize()) {
            val turnPageIconModifier = Modifier
                .padding(20.dp)
                .size(50.dp)
                .clip(CircleShape)
            val photoPalette by photoPaletteState
            IconButton(
                onClick = { coroutineScope.launch { pagerState.previousPage() } },
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
                onClick = { coroutineScope.launch { pagerState.nextPage() } },
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
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun PagerState.nextPage() {
    val nextPageIndex = (currentPage + 1) % pageCount
    animateScrollToPage(nextPageIndex)
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun PagerState.previousPage() {
    val previousPageIndex = (currentPage - 1).let { if (it < 0) pageCount + it else it }
    animateScrollToPage(previousPageIndex)
}