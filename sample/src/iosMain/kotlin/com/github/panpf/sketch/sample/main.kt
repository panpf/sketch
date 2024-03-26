package com.github.panpf.sketch.sample

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.sample.ui.gallery.PhotoListScreen
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.decode.GifAnimatedSkiaDecoder
import com.github.panpf.sketch.decode.WebpAnimatedSkiaDecoder
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initialSketch()
    return ComposeUIViewController {
        AppTheme {
            var screenState by remember { mutableStateOf<Screen?>(null) }
            LaunchedEffect(Unit) {
                snapshotFlow { screenState }.collect {
//                        lightStatusAndNavigationBar = it !is PhotoPagerScreen
                }
            }
            Navigator(PhotoListScreen) { navigator ->
                ScaleTransition(navigator = navigator)
                screenState = navigator.lastItem
            }
        }
    }
}

private fun initialSketch() {
    SingletonSketch.setSafe {
        Sketch.Builder(PlatformContext.INSTANCE).apply {
            logger(Logger(level = Logger.Debug))
            components {
                supportSvg()
                addDecoder(GifAnimatedSkiaDecoder.Factory())
                addDecoder(WebpAnimatedSkiaDecoder.Factory())
            }
        }.build()
    }
}
