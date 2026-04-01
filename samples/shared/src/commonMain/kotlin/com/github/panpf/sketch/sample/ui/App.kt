package com.github.panpf.sketch.sample.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.sample.util.Platform
import com.github.panpf.sketch.sample.util.current
import com.github.panpf.sketch.sample.util.isMobile
import org.koin.compose.koinInject

@Composable
fun App(onNavBackStackChanged: ((List<NavKey>) -> Unit)? = null) {
    AppTheme {
        Box(Modifier.fillMaxSize()) {
            NavigationContent(onNavBackStackChanged)
            SnackbarContent()
        }
    }
}

@Composable
private fun NavigationContent(onContentChanged: ((List<NavKey>) -> Unit)? = null) {
    val homeRoute = if (Platform.current.isMobile()) VerHomeRoute else HorHomeRoute
    val navBackStack: NavBackStack<NavKey> =
        rememberNavBackStack(navSavedStateConfig, homeRoute)
    LaunchedEffect(navBackStack.toList()) {
        onContentChanged?.invoke(navBackStack.toList())
    }
    CompositionLocalProvider(LocalNavBackStack provides navBackStack) {
        NavDisplay(
            backStack = navBackStack,
            entryProvider = navEntryProvider,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            popTransitionSpec = { fadeIn() togetherWith fadeOut() }
        )
    }
}

@Composable
private fun BoxScope.SnackbarContent() {
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