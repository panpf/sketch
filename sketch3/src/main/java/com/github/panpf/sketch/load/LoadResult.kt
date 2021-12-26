package com.github.panpf.sketch.load

import com.github.panpf.sketch.common.DataFrom

sealed interface LoadResult

class LoadSuccessResult constructor(val data: LoadData, val from: DataFrom) : LoadResult

class LoadErrorResult constructor(val throwable: Throwable) : LoadResult