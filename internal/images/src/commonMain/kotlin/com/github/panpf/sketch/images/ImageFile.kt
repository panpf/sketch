package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ImageFile {
    val name: String
    val uri: String
    val size: Size
    val length: Long
    val mimeType: String
    val animated: Boolean
    val exifOrientation: Int
    val imageInfo: ImageInfo

    suspend fun toDataSource(context: PlatformContext): DataSource
}

@OptIn(ExperimentalUuidApi::class)
suspend fun ImageFile.getOnlyTempFile(context: PlatformContext, fileSystem: FileSystem): Path {
    val uuid = Uuid.generateV7()
    val tempPath = FileSystem.SYSTEM_TEMPORARY_DIRECTORY
        .resolve("sketch_test_${uuid.toHexString()}_${name}")
    val dataSource = toDataSource(context)
    dataSource.openSource().buffer().use { source ->
        fileSystem.sink(tempPath).buffer().use { sink ->
            sink.writeAll(source)
        }
    }
    return tempPath
}