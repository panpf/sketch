package com.github.panpf.sketch.request

sealed interface ExecuteResult<T : Any> {
    class Success<T : Any> constructor(val data: T) : ExecuteResult<T>

    class Error<T : Any> constructor(val throwable: Throwable) : ExecuteResult<T>
}