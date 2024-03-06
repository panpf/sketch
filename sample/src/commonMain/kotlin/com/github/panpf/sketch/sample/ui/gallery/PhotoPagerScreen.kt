package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.util.ignoreFirst
import kotlinx.coroutines.launch

class PhotoPagerScreen(private val params: PhotoPagerParams) : BaseScreen() {

    @Composable
    override fun DrawContent() {
        Box(Modifier.fillMaxSize()) {
            val snackbarHostState = remember { SnackbarHostState() }
            val appSettings = LocalPlatformContext.current.appSettings
            LaunchedEffect(Unit) {
                appSettings.showOriginImage.ignoreFirst().collect { newValue ->
                    if (newValue) {
                        snackbarHostState.showSnackbar("Now show original image")
                    } else {
                        snackbarHostState.showSnackbar("Now show thumbnails image")
                    }
                }
            }

            val coroutineScope = rememberCoroutineScope()
            val navigator = LocalNavigator.current!!
            PhotoPager(
                imageList = params.imageList,
                totalCount = params.totalCount,
                startPosition = params.startPosition,
                initialPosition = params.initialPosition,
                onShareClick = {
                    // TODO Realize sharing
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Sharing feature is under development")
                    }
                },
                onSaveClick = {
                    // TODO Realize saving
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Save feature is under development")
                    }
                },
                onImageClick = {
                },
                onBackClick = {
                    navigator.pop()
                },
            )

            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
            )
        }
    }
}