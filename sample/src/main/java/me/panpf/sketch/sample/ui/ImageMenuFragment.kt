package me.panpf.sketch.sample.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.format.Formatter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.panpf.tools4a.toast.ktx.showLongToast
import me.panpf.sketch.datasource.DataSource
import me.panpf.sketch.decode.ImageOrientationCorrector
import me.panpf.sketch.drawable.SketchDrawable
import me.panpf.sketch.drawable.SketchLoadingDrawable
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable
import me.panpf.sketch.sample.base.parentViewModels
import me.panpf.sketch.sample.databinding.FragmentImageBinding
import me.panpf.sketch.sample.util.ApplyWallpaperAsyncTask
import me.panpf.sketch.sample.util.SaveImageAsyncTask
import me.panpf.sketch.sample.util.safeRun
import me.panpf.sketch.sample.vm.ShowImageMenuViewModel
import me.panpf.sketch.uri.FileUriModel
import me.panpf.sketch.uri.GetDataSourceException
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import java.io.File
import java.io.IOException
import java.util.*

class ImageMenuFragment : Fragment() {

    private val showImageMenuViewModel by parentViewModels<ShowImageMenuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showImageMenuViewModel.showImageMenuEvent.listen(this) {
            it ?: return@listen
            showMenu(it)
        }
    }

    private fun showMenu(binding: FragmentImageBinding) {
        AlertDialog.Builder(requireActivity()).apply {
            val menuItemList = LinkedList<MenuItem>().apply {
                add(MenuItem("Image Info") { _, _ ->
                    showImageInfo(binding)
                })
                add(MenuItem("Zoom/Rotate/Block Display") { _, _ ->
                    showZoomMenu(binding)
                })
                val imageView = binding.imageFragmentZoomImage
                val scaleType = imageView.zoomer.scaleType ?: imageView.scaleType
                add(MenuItem("Toggle ScaleType (%s)".format(scaleType)) { _, _ ->
                    showScaleTypeMenu(binding)
                })
                add(MenuItem("Set as wallpaper") { _, _ ->
                    setWallpaper(binding)
                })
                add(MenuItem("Share Image") { _, _ ->
                    share(binding)
                })
                add(MenuItem("Save Image") { _, _ ->
                    save(binding)
                })
            }
            val titles = menuItemList.map { it.title }.toTypedArray()
            setItems(titles) { dialog, which ->
                menuItemList[which].clickListener?.onClick(dialog, which)
            }
        }.show()
    }

    private fun showImageInfo(binding: FragmentImageBinding) {
        AlertDialog.Builder(requireActivity()).apply {
            @Suppress("MoveVariableDeclarationIntoWhen")
            val drawable = SketchUtils.getLastDrawable(binding.imageFragmentZoomImage.drawable)
            val imageInfo: String = when (drawable) {
                is SketchLoadingDrawable -> "Image is loading, please wait later"
                is SketchDrawable -> {
                    assembleImageInfo(drawable, drawable as SketchDrawable)
                }
                else -> "Unknown source image"
            }
            setMessage(imageInfo)
            setNegativeButton("Cancel", null)
        }.show()
    }

    private fun assembleImageInfo(drawable: Drawable, sketchDrawable: SketchDrawable): String =
        StringBuilder().apply {
            val uriModel = UriModel.match(requireContext(), sketchDrawable.uri!!)
            val dataSource: DataSource? = if (uriModel != null) {
                safeRun { uriModel.getDataSource(requireContext(), sketchDrawable.uri!!, null) }
            } else {
                null
            }
            val imageLength: Long = safeRun { dataSource?.length ?: 0 } ?: 0

            val needDiskSpace =
                if (imageLength > 0) Formatter.formatFileSize(context, imageLength) else "Unknown"

            val previewDrawableByteCount = sketchDrawable.byteCount
            val pixelByteCount: Int = if (drawable is SketchShapeBitmapDrawable) {
                val bitmap = drawable.bitmapDrawable.bitmap
                previewDrawableByteCount / bitmap.width / bitmap.height
            } else {
                previewDrawableByteCount / drawable.intrinsicWidth / drawable.intrinsicHeight
            }
            val originImageByteCount =
                sketchDrawable.originWidth * sketchDrawable.originHeight * pixelByteCount
            val needMemory = Formatter.formatFileSize(context, originImageByteCount.toLong())
            val mimeType = sketchDrawable.mimeType

            append(sketchDrawable.key)
            appendLine()
            appendLine()
            append(
                "Original: %dx%d/%s/%s/%s/%s".format(
                    sketchDrawable.originWidth,
                    sketchDrawable.originHeight,
                    if (mimeType?.startsWith("image/") == true) mimeType.substring(6) else "Unknown",
                    needDiskSpace,
                    ImageOrientationCorrector.toName(sketchDrawable.exifOrientation),
                    needMemory
                )
            )

            appendLine()
            appendLine()
            append(
                "Preview: %dx%d/%s/%s".format(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    sketchDrawable.bitmapConfig,
                    Formatter.formatFileSize(context, previewDrawableByteCount.toLong())
                )
            )
        }.toString()

    private fun showZoomMenu(binding: FragmentImageBinding) {
        AlertDialog.Builder(activity).apply {
            val menuItemList = LinkedList<MenuItem>().apply {
                val zoomer = binding.imageFragmentZoomImage.zoomer

                add(MenuItem(assembleZoomInfo(binding), null))
                add(MenuItem("canScrollHorizontally: ${zoomer.canScrollHorizontally()}", null))
                add(MenuItem("canScrollVertically: ${zoomer.canScrollVertically()}", null))
                add(MenuItem(assembleBlockInfo(binding), null))

                val blockDisplayer = zoomer.blockDisplayer
                if (blockDisplayer.isReady || blockDisplayer.isInitializing) {
                    val isShowBlockBounds = blockDisplayer.isShowBlockBounds
                    add(MenuItem(if (isShowBlockBounds) "Hide block bounds" else "Show block bounds") { _, _ ->
                        blockDisplayer.isShowBlockBounds = !blockDisplayer.isShowBlockBounds
                    })
                } else {
                    add(MenuItem("Block bounds (No need)", null))
                }

                add(MenuItem(if (zoomer.isReadMode) "Close read mode" else "Open read mode") { _, _ ->
                    zoomer.isReadMode = !zoomer.isReadMode
                })

                add(MenuItem("Clockwise rotation 90°（%d）".format(zoomer.rotateDegrees)) { _, _ ->
                    if (!zoomer.rotateBy(90)) {
                        showLongToast("The rotation angle must be a multiple of 90")
                    }
                })
            }
            val titles = menuItemList.map { it.title }.toTypedArray()
            setItems(titles) { dialog, which ->
                menuItemList[which].clickListener?.onClick(dialog, which)
            }
        }.show()
    }

    private fun assembleZoomInfo(binding: FragmentImageBinding): String {
        val zoomer = binding.imageFragmentZoomImage.zoomer
        val zoomScale = SketchUtils.formatFloat(zoomer.zoomScale, 2)
        val visibleRectString = Rect().apply { zoomer.getVisibleRect(this) }.toShortString()
        return "Zoom: %s/%s".format(zoomScale, visibleRectString)
    }

    private fun assembleBlockInfo(binding: FragmentImageBinding): String =
        StringBuilder().apply {
            val blockDisplayer = binding.imageFragmentZoomImage.zoomer.blockDisplayer
            when {
                blockDisplayer.isReady -> {
                    append(
                        "Blocks：%d/%d/%s".format(
                            blockDisplayer.blockBaseNumber,
                            blockDisplayer.blockSize,
                            Formatter.formatFileSize(context, blockDisplayer.allocationByteCount)
                        )
                    )

                    appendLine()
                    append("Blocks Area：%s".format(blockDisplayer.decodeRect.toShortString()))

                    appendLine()
                    append("Blocks Area (SRC)：%s".format(blockDisplayer.decodeSrcRect.toShortString()))
                }
                blockDisplayer.isInitializing -> {
                    appendLine()
                    append("Blocks initializing...")
                }
                else -> append("Blocks (No need)")
            }
        }.toString()

    private fun showScaleTypeMenu(binding: FragmentImageBinding) {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Toggle ScaleType")

        val items = arrayOfNulls<String>(7)
        items[0] = "CENTER"
        items[1] = "CENTER_CROP"
        items[2] = "CENTER_INSIDE"
        items[3] = "FIT_START"
        items[4] = "FIT_CENTER"
        items[5] = "FIT_END"
        items[6] = "FIT_XY"

        builder.setItems(items) { dialog, which ->
            dialog.dismiss()

            when (which) {
                0 -> binding.imageFragmentZoomImage.scaleType = ImageView.ScaleType.CENTER
                1 -> binding.imageFragmentZoomImage.scaleType =
                    ImageView.ScaleType.CENTER_CROP
                2 -> binding.imageFragmentZoomImage.scaleType =
                    ImageView.ScaleType.CENTER_INSIDE
                3 -> binding.imageFragmentZoomImage.scaleType = ImageView.ScaleType.FIT_START
                4 -> binding.imageFragmentZoomImage.scaleType =
                    ImageView.ScaleType.FIT_CENTER
                5 -> binding.imageFragmentZoomImage.scaleType = ImageView.ScaleType.FIT_END
                6 -> binding.imageFragmentZoomImage.scaleType = ImageView.ScaleType.FIT_XY
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun getImageFile(imageUri: String?): File? {
        val context = context ?: return null
        imageUri?.takeIf { it.isNotEmpty() } ?: return null

        val uriModel = UriModel.match(context, imageUri)
        if (uriModel == null) {
            Toast.makeText(activity, "Unknown format uri: $imageUri", Toast.LENGTH_LONG).show()
            return null
        }

        val dataSource: DataSource
        try {
            dataSource = uriModel.getDataSource(context, imageUri, null)
        } catch (e: GetDataSourceException) {
            e.printStackTrace()
            Toast.makeText(activity, "The Image is not ready yet", Toast.LENGTH_LONG).show()
            return null
        }

        return try {
            dataSource.getFile(context.externalCacheDir, null)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun share(binding: FragmentImageBinding) {
        val activity = activity ?: return
        val drawable = binding.imageFragmentZoomImage.drawable
        val imageUri =
            if (drawable != null && drawable is SketchDrawable) (drawable as SketchDrawable).uri else null
        if (TextUtils.isEmpty(imageUri)) {
            Toast.makeText(activity, "Please wait later", Toast.LENGTH_LONG).show()
            return
        }

        val imageFile = getImageFile(imageUri)
        if (imageFile == null) {
            Toast.makeText(activity, "The Image is not ready yet", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile))
        intent.type = "image/" + parseFileType(imageFile.name)

        val infoList = activity.packageManager.queryIntentActivities(intent, 0)
        if (infoList == null || infoList.isEmpty()) {
            Toast.makeText(
                activity,
                "There is no APP on your device to share the picture",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        startActivity(intent)
    }

    private fun setWallpaper(binding: FragmentImageBinding) {
        val activity = activity ?: return
        val drawable = binding.imageFragmentZoomImage.drawable
        val imageUri =
            if (drawable != null && drawable is SketchDrawable) (drawable as SketchDrawable).uri else null
        if (TextUtils.isEmpty(imageUri)) {
            Toast.makeText(activity, "Please wait later", Toast.LENGTH_LONG).show()
            return
        }

        val imageFile = getImageFile(imageUri)
        if (imageFile == null) {
            Toast.makeText(activity, "The Image is not ready yet", Toast.LENGTH_LONG).show()
            return
        }

        ApplyWallpaperAsyncTask(activity, imageFile).execute(0)
    }

    private fun save(binding: FragmentImageBinding) {
        val context = context ?: return
        val drawable = binding.imageFragmentZoomImage.drawable
        val imageUri =
            (if (drawable != null && drawable is SketchDrawable) (drawable as SketchDrawable).uri else null)?.takeIf { it.isNotEmpty() }
                ?: return

        val uriModel = UriModel.match(context, imageUri)
        if (uriModel == null) {
            Toast.makeText(activity, "Unknown format uri: $imageUri", Toast.LENGTH_LONG).show()
            return
        }

        if (uriModel is FileUriModel) {
            Toast.makeText(
                activity,
                "This image is the local no need to save",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val dataSource: DataSource
        try {
            dataSource = uriModel.getDataSource(context, imageUri, null)
        } catch (e: GetDataSourceException) {
            e.printStackTrace()
            Toast.makeText(activity, "The Image is not ready yet", Toast.LENGTH_LONG).show()
            return
        }

        SaveImageAsyncTask(activity, dataSource, imageUri).execute("")
    }

    private fun parseFileType(fileName: String): String? {
        val lastIndexOf = fileName.lastIndexOf("")
        if (lastIndexOf < 0) {
            return null
        }
        val fileType = fileName.substring(lastIndexOf + 1)
        if ("" == fileType.trim { it <= ' ' }) {
            return null
        }
        return fileType
    }

    private class MenuItem(
        val title: String,
        val clickListener: DialogInterface.OnClickListener?
    )
}