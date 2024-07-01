package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.EventBus
import kotlinx.coroutines.launch

actual fun getTopMargin(context: PlatformContext): Int {
    return 0
}


@Composable
@OptIn(ExperimentalFoundationApi::class)
actual fun BoxScope.PlatformPagerTools(
    buttonBgColorState: MutableState<Color>,
    pagerState: PagerState
) {
    LaunchedEffect(Unit) {
        EventBus.keyEvent.collect { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyUp && !keyEvent.isMetaPressed) {
                when (keyEvent.key) {
                    Key.PageUp, Key.DirectionLeft -> {
                        val previousPageIndex =
                            (pagerState.currentPage - 1).let { if (it < 0) pagerState.pageCount + it else it }
                        pagerState.animateScrollToPage(previousPageIndex)
                    }

                    Key.PageDown, Key.DirectionRight -> {
                        val nextPageIndex = (pagerState.currentPage + 1) % pagerState.pageCount
                        pagerState.animateScrollToPage(nextPageIndex)
                    }
                }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    IconButton(
        onClick = {
            coroutineScope.launch {
                val previousPageIndex =
                    (pagerState.currentPage - 1).let { if (it < 0) pagerState.pageCount + it else it }
                pagerState.animateScrollToPage(previousPageIndex)
            }
        },
        modifier = Modifier
            .padding(20.dp)
            .size(50.dp)
            .background(buttonBgColorState.value, shape = CircleShape)
            .align(Alignment.CenterStart)
    ) {
        Icon(Filled.KeyboardArrowLeft, contentDescription = "Previous", tint = Color.White)
    }

    IconButton(
        onClick = {
            coroutineScope.launch {
                val nextPageIndex = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPageIndex)
            }
        },
        modifier = Modifier
            .padding(20.dp)
            .size(50.dp)
            .background(buttonBgColorState.value, shape = CircleShape)
            .align(Alignment.CenterEnd)
    ) {
        Icon(Filled.KeyboardArrowRight, contentDescription = "Next", tint = Color.White)
    }
}