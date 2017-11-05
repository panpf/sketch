package me.panpf.sketch.sample.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import me.xiaopan.sketch.Configuration
import me.xiaopan.sketch.Sketch
import me.xiaopan.sketch.cache.BitmapPoolUtils
import me.xiaopan.sketch.datasource.DataSource
import me.xiaopan.sketch.decode.ImageDecodeUtils
import me.xiaopan.sketch.uri.GetDataSourceException
import me.xiaopan.sketch.uri.UriModel
import me.xiaopan.sketch.util.SketchUtils
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bindView
import java.io.IOException

@BindContentView(R.layout.fragment_in_bitmap_test)
class InBitmapTestFragment : BaseFragment() {

    val imageView: ImageView by bindView(R.id.image_inBitmapTestFragment)
    val textView: TextView by bindView(R.id.text_inBitmapTestFragment)
    val sizeSameButton: Button by bindView(R.id.button_inBitmapTestFragment_sizeSame)
    val largeSizeButton: Button by bindView(R.id.button_inBitmapTestFragment_largeSize)
    val sizeNoSameButton: Button by bindView(R.id.button_inBitmapTestFragment_sizeNoSame)
    val inSampleSizeButton: Button by bindView(R.id.button_inBitmapTestFragment_inSampleSize)
    val pageNumberTextView: TextView by bindView(R.id.view_inBitmapTestFragment_pageNumber)
    val lastView: View by bindView(R.id.view_inBitmapTestFragment_last)
    val nextView: View by bindView(R.id.view_inBitmapTestFragment_next)

    internal var index = 0

    val configuration: Configuration by lazy {
        Sketch.with(activity).configuration
    }

    private var currentMode: View? = null

    private fun decodeImage(context: Context, imageUri: String, options: BitmapFactory.Options): Bitmap? {
        val uriModel = UriModel.match(context, imageUri) ?: return null

        val dataSource: DataSource
        try {
            dataSource = uriModel.getDataSource(context, imageUri, null)
        } catch (e: GetDataSourceException) {
            e.printStackTrace()
            return null
        }

        try {
            return ImageDecodeUtils.decodeBitmap(dataSource, options)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lastView.setOnClickListener {
            --index
            if (index < 0) {
                index = AssetImage.IN_BITMAP_SAMPLES.size - Math.abs(index)
            }
            currentMode!!.performClick()
        }

        nextView.setOnClickListener {
            index = ++index % AssetImage.IN_BITMAP_SAMPLES.size
            currentMode!!.performClick()
        }

        sizeSameButton.setOnClickListener { v ->
            testSizeSame()
            updateCheckedStatus(v)
        }

        largeSizeButton.setOnClickListener { v ->
            testLargeSize()
            updateCheckedStatus(v)
        }

        sizeNoSameButton.setOnClickListener { v ->
            testSizeNoSame()
            updateCheckedStatus(v)
        }

        inSampleSizeButton.setOnClickListener { v ->
            inSampleSize()
            updateCheckedStatus(v)
        }

        sizeSameButton.performClick()
    }

    private fun updateCheckedStatus(newView: View) {
        if (currentMode != null) {
            currentMode!!.isEnabled = true
        }

        newView.isEnabled = false
        currentMode = newView

        pageNumberTextView.text = String.format("%d/%d", index + 1, AssetImage.IN_BITMAP_SAMPLES.size)
    }

    private fun testSizeSame() {
        object : TestTask(activity) {
            override fun configOptions(options: BitmapFactory.Options) {
                if (BitmapPoolUtils.sdkSupportInBitmap()) {
                    options.inBitmap = Bitmap.createBitmap(options.outWidth, options.outHeight, options.inPreferredConfig)
                    options.inMutable = true
                }
                super.configOptions(options)
            }
        }.execute(AssetImage.IN_BITMAP_SAMPLES[index % AssetImage.IN_BITMAP_SAMPLES.size])
    }

    private fun testLargeSize() {
        object : TestTask(activity) {
            override fun configOptions(options: BitmapFactory.Options) {
                if (BitmapPoolUtils.sdkSupportInBitmap()) {
                    options.inBitmap = Bitmap.createBitmap(options.outWidth + 10, options.outHeight + 5, options.inPreferredConfig)
                    options.inMutable = true
                }
                super.configOptions(options)
            }
        }.execute(AssetImage.IN_BITMAP_SAMPLES[index % AssetImage.IN_BITMAP_SAMPLES.size])
    }

    private fun testSizeNoSame() {
        object : TestTask(activity) {
            override fun configOptions(options: BitmapFactory.Options) {
                if (BitmapPoolUtils.sdkSupportInBitmap()) {
                    options.inBitmap = Bitmap.createBitmap(options.outHeight, options.outWidth, options.inPreferredConfig)
                    options.inMutable = true
                }
                super.configOptions(options)
            }
        }.execute(AssetImage.IN_BITMAP_SAMPLES[index % AssetImage.IN_BITMAP_SAMPLES.size])
    }

    private fun inSampleSize() {
        object : TestTask(activity) {
            override fun configOptions(options: BitmapFactory.Options) {
                if (BitmapPoolUtils.sdkSupportInBitmap()) {
                    options.inSampleSize = 2
                    val finalWidth = SketchUtils.ceil(options.outWidth, options.inSampleSize.toFloat())
                    val finalHeight = SketchUtils.ceil(options.outHeight, options.inSampleSize.toFloat())
                    options.inBitmap = Bitmap.createBitmap(finalWidth, finalHeight, options.inPreferredConfig)
                    options.inMutable = true
                }
                super.configOptions(options)
            }
        }.execute(AssetImage.IN_BITMAP_SAMPLES[index % AssetImage.IN_BITMAP_SAMPLES.size])
    }

    private open inner class TestTask(context: Context) : AsyncTask<String, Int, Bitmap?>() {
        protected var builder = StringBuilder()
        private val context: Context

        init {
            this.context = context.applicationContext
        }

        override fun doInBackground(vararg params: String): Bitmap? {
            val imageUri = params[0]

            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            decodeImage(context, imageUri, options)

            if (options.outWidth <= 1 || options.outHeight <= 1) {
                return null
            }

            options.inSampleSize = 1   // 这很重要4.4以下必须得是1
            configOptions(options)

            builder.append("imageUri: ").append(imageUri)

            val sizeInBytes = SketchUtils.computeByteCount(options.outWidth, options.outHeight, options.inPreferredConfig)
            builder.append("\n").append("image: ")
                    .append(options.outWidth).append("x").append(options.outHeight)
                    .append(", ").append(options.inPreferredConfig)
                    .append(", ").append(sizeInBytes)

            builder.append("\n").append("inSampleSize: ").append(options.inSampleSize)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (options.inBitmap != null) {
                    builder.append("\n")
                            .append("inBitmap: ")
                            .append(Integer.toHexString(options.inBitmap.hashCode()))
                            .append(", ").append(options.inBitmap.width).append("x").append(options.inBitmap.height)
                            .append(", ").append(options.inBitmap.isMutable)
                            .append(", ").append(SketchUtils.getByteCount(options.inBitmap))
                } else {
                    builder.append("\n").append("inBitmap: ").append("null")
                }
            }

            var newBitmap: Bitmap? = null
            try {
                options.inJustDecodeBounds = false
                newBitmap = decodeImage(context, imageUri, options)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()

                val errorTracker = configuration.errorTracker
                val bitmapPool = configuration.bitmapPool
                if (ImageDecodeUtils.isInBitmapDecodeError(throwable, options, false)) {
                    ImageDecodeUtils.recycleInBitmapOnDecodeError(errorTracker, bitmapPool,
                            imageUri, options.outWidth, options.outHeight, options.outMimeType, throwable, options, false)
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

            return newBitmap
        }

        protected open fun configOptions(options: BitmapFactory.Options) {

        }

        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)

            var oldBitmap: Bitmap? = null
            imageView.drawable?.let {
                oldBitmap = (it as BitmapDrawable).bitmap
            }
            imageView.setImageBitmap(bitmap)
            textView.text = builder.toString()

            if (!BitmapPoolUtils.freeBitmapToPool(oldBitmap, configuration.bitmapPool)) {
                Log.w("BitmapPoolTest", "recycle")
            }
        }
    }
}
