package me.panpf.sketch.sample.vm

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.panpf.sketch.Sketch
import me.panpf.sketch.datasource.DataSource
import me.panpf.sketch.decode.ImageDecodeUtils
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel
import me.panpf.sketch.uri.GetDataSourceException
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import java.io.IOException

class InBitmapTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val modeData = MutableLiveData(Mode.SAME_SIZE)
    val imageInfoData = MutableLiveData<String>()
    val imageBitmapData = MutableLiveData<Bitmap>()

    init {
        loadImage(application1)
    }

    fun changeMode(mode: Mode) {
        modeData.value = mode
        loadImage(application1)
    }

    private fun loadImage(context: Context) {
        val mode = modeData.value!!
        val imageUri = AssetImage.MEI_NV

        viewModelScope.launch(Dispatchers.IO) {
            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            decodeImage(context, imageUri, options)

            if (options.outWidth <= 1 || options.outHeight <= 1) {
                imageInfoData.postValue(null)
                imageBitmapData.postValue(null)
            } else {
                options.inSampleSize = 1   // 这很重要4.4以下必须得是1
                configOptionsByMode(options, mode)
                val builder = StringBuilder()
                builder.append("imageUri: ").append(imageUri)

                val sizeInBytes = SketchUtils.computeByteCount(
                    options.outWidth,
                    options.outHeight,
                    options.inPreferredConfig
                )
                builder.append("\n").append("image: ")
                    .append(options.outWidth).append("x").append(options.outHeight)
                    .append(", ").append(options.inPreferredConfig)
                    .append(", ").append(sizeInBytes)

                builder.append("\n").append("inSampleSize: ").append(options.inSampleSize)

                if (options.inBitmap != null) {
                    builder.append("\n")
                        .append("inBitmap: ")
                        .append(Integer.toHexString(options.inBitmap.hashCode()))
                        .append(", ").append(options.inBitmap.width).append("x")
                        .append(options.inBitmap.height)
                        .append(", ").append(options.inBitmap.isMutable)
                        .append(", ").append(SketchUtils.getByteCount(options.inBitmap))
                } else {
                    builder.append("\n").append("inBitmap: ").append("null")
                }

                var newBitmap: Bitmap? = null
                try {
                    options.inJustDecodeBounds = false
                    newBitmap = decodeImage(context, imageUri, options)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()

                    val callback = Sketch.with(context).configuration.callback
                    val bitmapPool = Sketch.with(context).configuration.bitmapPool
                    if (ImageDecodeUtils.isInBitmapDecodeError(throwable, options, false)) {
                        ImageDecodeUtils.recycleInBitmapOnDecodeError(
                            callback,
                            bitmapPool,
                            imageUri,
                            options.outWidth,
                            options.outHeight,
                            options.outMimeType,
                            throwable,
                            options,
                            false
                        )
                    }
                }

                if (newBitmap != null) {
                    builder.append("\n").append("newBitmap: ")
                        .append(Integer.toHexString(newBitmap.hashCode()))
                        .append(", ").append(newBitmap.width).append("x").append(newBitmap.height)
                        .append(", ").append(newBitmap.isMutable)
                        .append(", ").append(SketchUtils.getByteCount(newBitmap))
                } else {
                    builder.append("\n").append("newBitmap: ").append("null")
                }
                imageInfoData.postValue(builder.toString())
                imageBitmapData.postValue(newBitmap)
            }
        }
    }

    private fun configOptionsByMode(options: BitmapFactory.Options, mode: Mode) {
        when (mode) {
            Mode.SMALL_SIZE -> {
                options.inBitmap = Bitmap.createBitmap(
                    options.outWidth - 10,
                    options.outHeight - 10,
                    options.inPreferredConfig
                )
                options.inMutable = true
            }
            Mode.SAME_SIZE -> {
                options.inBitmap = Bitmap.createBitmap(
                    options.outWidth,
                    options.outHeight,
                    options.inPreferredConfig
                )
                options.inMutable = true
            }
            Mode.LARGE_SIZE -> {
                options.inBitmap = Bitmap.createBitmap(
                    options.outWidth + 10,
                    options.outHeight + 10,
                    options.inPreferredConfig
                )
                options.inMutable = true
            }
            Mode.WIDTH_HEIGHT_SWAP -> {
                options.inBitmap = Bitmap.createBitmap(
                    options.outHeight,
                    options.outWidth,
                    options.inPreferredConfig
                )
                options.inMutable = true
            }
            Mode.FIXED_TWO -> {
                options.inSampleSize = 2
                val finalWidth =
                    SketchUtils.calculateSamplingSize(options.outWidth, options.inSampleSize)
                val finalHeight =
                    SketchUtils.calculateSamplingSize(options.outHeight, options.inSampleSize)
                options.inBitmap =
                    Bitmap.createBitmap(finalWidth, finalHeight, options.inPreferredConfig)
                options.inMutable = true
            }
        }
    }

    private fun decodeImage(
        context: Context,
        @Suppress("SameParameterValue") imageUri: String,
        options: BitmapFactory.Options
    ): Bitmap? {
        val uriModel = UriModel.match(context, imageUri) ?: return null

        val dataSource: DataSource
        try {
            dataSource = uriModel.getDataSource(context, imageUri, null)
        } catch (e: GetDataSourceException) {
            e.printStackTrace()
            return null
        }

        return try {
            ImageDecodeUtils.decodeBitmap(dataSource, options)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    enum class Mode {
        SMALL_SIZE, SAME_SIZE, LARGE_SIZE, WIDTH_HEIGHT_SWAP, FIXED_TWO
    }
}