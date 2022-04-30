package pl.droidsonroids.gif

import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource

class GifInfoHandleCompat(private val dataSource: DataSource) {

    private val gifInfoHandle: GifInfoHandle by lazy {
        when (dataSource) {
            is ByteArrayDataSource -> {
                GifInfoHandle(dataSource.data)
            }
            is DiskCacheDataSource -> {
                GifInfoHandle(dataSource.diskCacheSnapshot.file.path)
            }
            is ResourceDataSource -> {
                GifInfoHandle(dataSource.request.context.resources.openRawResourceFd(dataSource.drawableId))
            }
            is ContentDataSource -> {
                GifInfoHandle.openUri(dataSource.request.context.contentResolver, dataSource.contentUri)
            }
            is FileDataSource -> {
                GifInfoHandle(dataSource.file.path)
            }
            is AssetDataSource -> {
                GifInfoHandle(dataSource.request.context.assets.openFd(dataSource.assetFileName))
            }
            else -> {
                throw Exception("Unsupported DataSource: ${dataSource::class.qualifiedName}")
            }
        }
    }

    val width: Int
        get() = gifInfoHandle.width

    val height: Int
        get() = gifInfoHandle.width

    val duration: Int
        get() = gifInfoHandle.duration

    val numberOfFrames: Int
        get() = gifInfoHandle.numberOfFrames

    fun setOptions(options: GifOptions) {
        gifInfoHandle.setOptions(options.inSampleSize, options.inIsOpaque)
    }

    fun toGifDrawable(): GifDrawable = GifDrawable(gifInfoHandle, null, null, true)
}