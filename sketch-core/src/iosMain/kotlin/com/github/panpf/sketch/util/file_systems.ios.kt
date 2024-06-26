package com.github.panpf.sketch.util

import okio.FileSystem

internal actual fun defaultFileSystem(): FileSystem = FileSystem.SYSTEM