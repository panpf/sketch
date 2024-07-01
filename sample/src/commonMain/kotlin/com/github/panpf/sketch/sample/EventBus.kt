package com.github.panpf.sketch.sample

import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow

object EventBus {
    val keyEvent = MutableSharedFlow<KeyEvent>()
    val savePhotoFlow = MutableSharedFlow<String>()
    val sharePhotoFlow = MutableSharedFlow<String>()
    val toastFlow = MutableSharedFlow<String>()
}