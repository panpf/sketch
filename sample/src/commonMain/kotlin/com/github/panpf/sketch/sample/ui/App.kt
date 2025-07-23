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
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.sample.util.Platform
import com.github.panpf.sketch.sample.util.current
import com.github.panpf.sketch.sample.util.isMobile
import org.koin.compose.koinInject

@Composable
fun App(onContentChanged: ((Navigator) -> Unit)? = null) {
    AppTheme {
        Box(Modifier.fillMaxSize()) {
            val homeScreen = if (Platform.current.isMobile()) VerHomeScreen else HorHomeScreen
            Navigator(homeScreen) { navigator ->
                ScaleTransition(navigator = navigator)
                onContentChanged?.invoke(navigator)
            }
//            Column {
//                AsyncImage(
//                    uri = "https://upload.wikimedia.org/wikipedia/commons/4/4f/SVG_Logo.svg",
//                    contentDescription = "",
//                    modifier = Modifier.size(300.dp, 300.dp)
//                )
//                AsyncImage(
//                    uri = "blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
//                    contentDescription = "",
//                    modifier = Modifier.size(300.dp, 300.dp)
//                )
//            }

            val snackbarHostState = remember { SnackbarHostState() }
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
            )
            val appEvents: AppEvents = koinInject()
            LaunchedEffect(Unit) {
                appEvents.toastFlow.collect {
                    snackbarHostState.showSnackbar(it)
                }
            }
        }
    }
}