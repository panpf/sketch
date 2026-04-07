package com.github.panpf.sketch.sample.ui.base

import com.github.panpf.sketch.sample.AppEvents

sealed interface ActionResult {

    companion object {
        fun success(message: String? = null): ActionResult = Success(message)
        fun error(message: String): ActionResult = Error(message)
    }

    class Success(val message: String? = null) : ActionResult
    class Error(val message: String) : ActionResult
}

suspend fun handleActionResult(appEvents: AppEvents, result: ActionResult): Boolean =
    when (result) {
        is ActionResult.Success -> {
            result.message?.let {
                appEvents.toastFlow.emit(it)
            }
            true
        }

        is ActionResult.Error -> {
            appEvents.toastFlow.emit(result.message)
            false
        }
    }