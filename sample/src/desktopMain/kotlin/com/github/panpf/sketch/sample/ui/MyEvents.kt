package com.github.panpf.sketch.sample.ui

import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow

object MyEvents {
    val keyEvent = MutableSharedFlow<KeyEvent>()
}