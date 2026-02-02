package com.github.panpf.sketch.sample.ui.base

sealed interface ActionResult {

    companion object {
        fun success(message: String? = null): ActionResult = Success(message)
        fun error(message: String): ActionResult = Error(message)
    }

    class Success(val message: String? = null) : ActionResult
    class Error(val message: String) : ActionResult
}