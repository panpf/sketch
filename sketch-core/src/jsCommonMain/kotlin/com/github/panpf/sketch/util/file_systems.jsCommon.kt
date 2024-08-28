/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.util

import okio.FileHandle
import okio.FileMetadata
import okio.FileSystem
import okio.Path
import okio.Sink
import okio.Source

internal actual fun defaultFileSystem(): FileSystem = ThrowingFileSystem

/** A file system that throws if any of its methods are called. */
private object ThrowingFileSystem : FileSystem() {

    override fun atomicMove(source: Path, target: Path) {
        throwReadWriteIsUnsupported()
    }

    override fun canonicalize(path: Path): Path {
        throwReadWriteIsUnsupported()
    }

    override fun createDirectory(dir: Path, mustCreate: Boolean) {
        throwReadWriteIsUnsupported()
    }

    override fun createSymlink(source: Path, target: Path) {
        throwReadWriteIsUnsupported()
    }

    override fun delete(path: Path, mustExist: Boolean) {
        throwReadWriteIsUnsupported()
    }

    override fun list(dir: Path): List<Path> {
        throwReadWriteIsUnsupported()
    }

    override fun listOrNull(dir: Path): List<Path>? {
        throwReadWriteIsUnsupported()
    }

    override fun metadataOrNull(path: Path): FileMetadata? {
        throwReadWriteIsUnsupported()
    }

    override fun openReadOnly(file: Path): FileHandle {
        throwReadWriteIsUnsupported()
    }

    override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
        throwReadWriteIsUnsupported()
    }

    override fun sink(file: Path, mustCreate: Boolean): Sink {
        throwReadWriteIsUnsupported()
    }

    override fun appendingSink(file: Path, mustExist: Boolean): Sink {
        throwReadWriteIsUnsupported()
    }

    override fun source(file: Path): Source {
        throwReadWriteIsUnsupported()
    }

    private fun throwReadWriteIsUnsupported(): Nothing {
        throw UnsupportedOperationException(
            "Javascript does not have access to the device's file system and cannot read from or " +
                    "write to it. If you are running on Node.js use 'NodeJsFileSystem' instead."
        )
    }
}
