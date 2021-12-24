package com.github.panpf.sketch.download


sealed interface DownloadResult

class DownloadSuccessResult(val data: DownloadData) : DownloadResult

class DownloadErrorResult(val throwable: Throwable) : DownloadResult