package com.github.panpf.sketch.sample.ui.screen.base

import androidx.compose.runtime.RememberObserver

/**
 * Why do you need to remember to count?
 *
 * Because when RememberObserver is passed as a parameter of the Composable function, the onRemembered method will be called when the Composable function is executed for the first time, causing it to be remembered multiple times.
 */
abstract class BaseRememberObserver : RememberObserver {

    private var rememberedCount = 0

    /**
     * Note: Do not actively call its onRemembered method because this will destroy the rememberedCount count.
     */
    final override fun onRemembered() {
        rememberedCount++
        if (rememberedCount != 1) return
        onFirstRemembered()
    }

    abstract fun onFirstRemembered()

    final override fun onForgotten() {
        if (rememberedCount <= 0) return
        rememberedCount--
        if (rememberedCount != 0) return

        onLastRemembered()
    }

    abstract fun onLastRemembered()

    final override fun onAbandoned() = onForgotten()
}