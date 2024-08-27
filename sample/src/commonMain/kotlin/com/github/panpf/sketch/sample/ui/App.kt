package com.github.panpf.sketch.sample.ui

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
import com.github.panpf.sketch.sample.EventBus
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.sample.util.Platform
import com.github.panpf.sketch.sample.util.current
import com.github.panpf.sketch.sample.util.isMobile

@Composable
fun App(onContentChanged: ((Navigator) -> Unit)? = null) {
    AppTheme {
        Box(Modifier.fillMaxSize()) {
            val homeScreen = if (Platform.current.isMobile()) VerHomeScreen else HorHomeScreen
            Navigator(homeScreen) { navigator ->
                ScaleTransition(navigator = navigator)
                onContentChanged?.invoke(navigator)
            }

            val snackbarHostState = remember { SnackbarHostState() }
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
            )
            LaunchedEffect(Unit) {
                EventBus.toastFlow.collect {
                    snackbarHostState.showSnackbar(it)
                }
            }
        }
    }
}