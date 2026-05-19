package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.components.TurnPageIndicator
import com.github.panpf.sketch.sample.util.Platform
import com.github.panpf.sketch.sample.util.current
import com.github.panpf.sketch.sample.util.isMobile
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun PhotoPagerScreen(params: PhotoPagerParams) {
    BaseScreen {
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

            Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                PhotoPagerHeaders(params, pagerState, photoPaletteState)

                TurnPageIndicator(pagerState, photoPaletteState)

                if (!Platform.current.isMobile()) {
                    PhotoPagerGesturePromptDialog()
                }
            }
        }
        LaunchedEffect(Unit) {
            focusRequest.requestFocus()
        }
    }
}