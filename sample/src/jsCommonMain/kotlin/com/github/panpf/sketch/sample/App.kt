package com.github.panpf.sketch.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.gallery.HomeScreen
import com.github.panpf.sketch.sample.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Box(Modifier.fillMaxSize()) {
            Navigator(HomeScreen) { navigator ->
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
    }

    val context = LocalPlatformContext.current
    LaunchedEffect(Unit) {
        MyEvents.savePhotoFlow.collect {
            savePhoto(SingletonSketch.get(context), it)
        }
    }
    LaunchedEffect(Unit) {
        MyEvents.sharePhotoFlow.collect {
            sharePhoto(SingletonSketch.get(context), it)
        }
    }
}

@Suppress("UNUSED_PARAMETER")
private suspend fun savePhoto(sketch: Sketch, imageUri: String) {
    MyEvents.toastFlow.emit("JS platform does not support save photo")
}

@Suppress("UNUSED_PARAMETER")
private suspend fun sharePhoto(sketch: Sketch, imageUri: String) {
    MyEvents.toastFlow.emit("JS platform does not support sharing photo")
}