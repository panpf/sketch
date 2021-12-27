package com.github.panpf.sketch.common

sealed interface ExecuteResult<T : Any> {
    class Success<T : Any> constructor(val data: T) : ExecuteResult<T>

    class Error<T : Any> constructor(val throwable: Throwable) : ExecuteResult<T>
}