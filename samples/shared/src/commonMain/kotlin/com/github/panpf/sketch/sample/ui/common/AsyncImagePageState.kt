package com.github.panpf.sketch.sample.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.request.LoadState

@Composable
fun AsyncImagePageState(imageState: AsyncImageState, modifier: Modifier = Modifier.fillMaxSize()) {
    val pageState by remember {
        derivedStateOf {
            if (imageState.loadState is LoadState.Error) {
                PageState.Error("Image load failed") {
                    imageState.restart()
                }
            } else {
                null
            }
        }
    }

    PageState(pageState = pageState, modifier = modifier)
}