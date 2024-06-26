package com.github.panpf.sketch.util

import okio.FileSystem
import okio.Path

sealed interface DownloadData {
    class Bytes(val bytes: ByteArray) : DownloadData
    class Cache(val fileSystem: FileSystem, val path: Path) : DownloadData
}