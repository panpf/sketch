package com.github.panpf.sketch.sample.ui.base

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

// TODO Remove it and use the direct function as the page entry
abstract class BaseScreen {

    @Composable
    fun Content() {
        Surface {
            DrawContent()
        }
    }

    @Composable
    abstract fun DrawContent()
}