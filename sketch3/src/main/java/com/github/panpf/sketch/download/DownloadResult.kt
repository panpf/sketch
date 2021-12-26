package com.github.panpf.sketch.download

import com.github.panpf.sketch.common.DataFrom


sealed interface DownloadResult

class DownloadSuccessResult constructor(val data: DownloadData, val from: DataFrom) : DownloadResult

class DownloadErrorResult constructor(val throwable: Throwable) : DownloadResult