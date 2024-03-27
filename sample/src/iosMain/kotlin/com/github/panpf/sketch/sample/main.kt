package com.github.panpf.sketch.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.GifAnimatedSkiaDecoder
import com.github.panpf.sketch.decode.WebpAnimatedSkiaDecoder
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.gallery.PhotoListScreen
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initialSketch()
    return ComposeUIViewController {
        AppTheme {
            Box(Modifier.fillMaxSize()) {
                Navigator(PhotoListScreen) { navigator ->
                    ScaleTransition(navigator = navigator)
                }

                val snackbarHostState = remember { SnackbarHostState() }
                SnackbarHost(
                    snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
                )
                LaunchedEffect(Unit) {
                    MyEvents.toastFlow.collect {
                        snackbarHostState.showSnackbar(it)
                    }
                }
            }

            LaunchedEffect(Unit) {
                MyEvents.savePhotoFlow.collect {
                    MyEvents.toastFlow.emit("Saving is not supported yet")
                }
            }
            LaunchedEffect(Unit) {
                MyEvents.sharePhotoFlow.collect {
                    MyEvents.toastFlow.emit("Sharing is not supported yet")
                }
            }
        }
    }
}

private fun initialSketch() {
    SingletonSketch.setSafe {
        val context = PlatformContext.INSTANCE
        val appSettings = context.appSettings
        Sketch.Builder(context).apply {
            logger(Logger(level = Logger.level(appSettings.logLevel.value)))
            components {
                supportSvg()
                addDecoder(GifAnimatedSkiaDecoder.Factory())
                addDecoder(WebpAnimatedSkiaDecoder.Factory())
            }
        }.build().apply {
            @Suppress("OPT_IN_USAGE")
            GlobalScope.launch {
                appSettings.logLevel.collect {
                    logger.level = Logger.level(it)
                }
            }
        }
    }
}
