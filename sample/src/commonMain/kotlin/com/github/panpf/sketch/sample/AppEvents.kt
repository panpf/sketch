package com.github.panpf.sketch.sample

import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow

class AppEvents {
    val keyEvent = MutableSharedFlow<KeyEvent>()
    val toastFlow = MutableSharedFlow<String>()
}