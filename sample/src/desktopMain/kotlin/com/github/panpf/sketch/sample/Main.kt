package com.github.panpf.sketch.sample

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.gallery.PhotoListScreen
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.sample.ui.util.PexelsCompatibleRequestInterceptor
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.launch

fun main() = application {
    SingletonSketch.setSafe {
        Sketch.Builder(PlatformContext.INSTANCE).apply {
            components {
                addRequestInterceptor(PexelsCompatibleRequestInterceptor())
            }
            logger(Logger(Logger.Level.DEBUG))
        }.build()
    }
    val coroutineScope = rememberCoroutineScope()
    Window(
        title = "Sketch3",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(1000.dp, 800.dp)),
        onKeyEvent = {
            coroutineScope.launch {
                MyEvents.keyEvent.emit(it)
            }
            false
        }
    ) {
        AppTheme {
            Navigator(PhotoListScreen) { navigator ->
                ScaleTransition(navigator = navigator)
            }
        }
    }
}