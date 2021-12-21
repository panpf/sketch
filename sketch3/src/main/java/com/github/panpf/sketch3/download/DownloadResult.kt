package com.github.panpf.sketch3.download


sealed interface DownloadResult

class DownloadSuccessResult(val data: DownloadData) : DownloadResult

class DownloadErrorResult(val throwable: Throwable) : DownloadResult