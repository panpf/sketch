package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.panpf.sketch.sample.ui.util.windowSize


@Composable
fun rememberMyDialogState(showing: Boolean = false): MyDialogState =
    remember { MyDialogState(showing) }

@Stable
class MyDialogState(showing: Boolean = false) {
    var showing by mutableStateOf(showing)

    var contentReady = true

    fun show() {
        showing = true
    }
}

@Composable
fun MyDialog(
    state: MyDialogState,
    content: @Composable () -> Unit
) {
    if (state.showing) {
        if (state.contentReady) {
            Dialog(onDismissRequest = { state.showing = false }) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = getDialogMaxHeight())
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    content()
                }
            }
        } else {
            state.showing = false
        }
    }
}

@Composable
fun getDialogMaxHeight(): Dp {
    val windowSize = windowSize()
    val density = LocalDensity.current
    return remember(windowSize, density) {
        with(density) { (windowSize.height * 0.8f).toDp() }
    }
}