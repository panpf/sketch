package com.github.panpf.sketch.sample.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import com.github.panpf.sketch.sample.ui.Page
import com.github.panpf.sketch.sample.ui.util.EventBus

@Composable
fun rememberNavigation(initialPage: Page): Navigation {
//    val navigation = rememberSaveable(
//        saver = listSaver<Navigation<Page>, Page>(
//            restore = { NavigationStack(*it.toTypedArray()) },
//            save = { it.stack },
//        )
//    ) {
//        Navigation(initialPage)
//    }
    val navigation = remember { Navigation(initialPage) }
    LaunchedEffect(Unit) {
        EventBus.keyEvent.collect {
            if (it.key == Key.Escape && it.type == KeyEventType.KeyUp) {
                navigation.back()
            }
        }
    }
    return navigation
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationContainer(navigation: Navigation) {
    AnimatedContent(targetState = navigation.lastWithIndex(), transitionSpec = {
        val previousIdx = initialState.index
        val currentIdx = targetState.index
        if (currentIdx == 0 && previousIdx == 0) {
            fadeIn() togetherWith fadeOut(tween(delayMillis = 150))
        } else {
            val multiplier = if (previousIdx < currentIdx) 1 else -1
            slideInHorizontally { w -> multiplier * w } togetherWith
                    slideOutHorizontally { w -> multiplier * -1 * w }
        }
    }) { (index, page) ->
        page.content(navigation, index)
    }
}

@Composable
fun NavigationContainer(initialPage: Page) = NavigationContainer(rememberNavigation(initialPage))

class Navigation(vararg initial: Page) {

    val stack = mutableStateListOf(*initial)

    fun push(t: Page) {
        stack.add(t)
    }

    fun back() {
        if (stack.size > 1) {
            // Always keep one element on the view stack
            stack.removeLast()
        }
    }

    fun reset() {
        stack.removeRange(1, stack.size)
    }

    fun lastWithIndex() = stack.withIndex().last()
}