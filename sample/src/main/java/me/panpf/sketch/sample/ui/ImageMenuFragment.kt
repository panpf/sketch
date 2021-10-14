package me.panpf.sketch.sample.ui

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.format.Formatter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import com.github.panpf.tools4a.toast.ktx.showLongToast
import com.github.panpf.tools4k.lang.asOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.panpf.sketch.datasource.DataSource
import me.panpf.sketch.decode.ImageOrientationCorrector
import me.panpf.sketch.drawable.SketchDrawable
import me.panpf.sketch.drawable.SketchLoadingDrawable
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable
import me.panpf.sketch.sample.base.parentViewModels
import me.panpf.sketch.sample.databinding.FragmentImageBinding
import me.panpf.sketch.sample.util.safeRun
import me.panpf.sketch.sample.vm.ShowImageMenuViewModel
import me.panpf.sketch.uri.FileUriModel
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import java.io.*
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
            add(MenuItem("Set As Wallpaper") { _, _ ->
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

        AlertDialog.Builder(requireActivity()).apply {
            setItems(titles) { dialog, which ->
                menuItemList[which].clickListener?.onClick(dialog, which)
            }
        }.show()
    }

    private fun showImageInfo(binding: FragmentImageBinding) {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val drawable = SketchUtils.getLastDrawable(binding.imageFragmentZoomImage.drawable)
        val imageInfo: String = when (drawable) {
            is SketchLoadingDrawable -> "Image is loading, please wait later"
            is SketchDrawable -> {
                assembleImageInfo(drawable, drawable as SketchDrawable)
            }
            else -> "Unknown source image"
        }

        AlertDialog.Builder(requireActivity()).apply {
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

        AlertDialog.Builder(activity).apply {
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
        val items = listOf(
            "CENTER" to ImageView.ScaleType.CENTER,
            "CENTER_CROP" to ImageView.ScaleType.CENTER_CROP,
            "CENTER_INSIDE" to ImageView.ScaleType.CENTER_INSIDE,
            "FIT_START" to ImageView.ScaleType.FIT_START,
            "FIT_CENTER" to ImageView.ScaleType.FIT_CENTER,
            "FIT_END" to ImageView.ScaleType.FIT_END,
            "FIT_XY" to ImageView.ScaleType.FIT_XY,
        )
        val titles = items.map { it.first }.toTypedArray()

        AlertDialog.Builder(activity).apply {
            setTitle("Toggle ScaleType")
            setItems(titles) { _, which ->
                binding.imageFragmentZoomImage.scaleType = items[which].second
            }
            setNegativeButton("Cancel", null)
        }.show()
    }

    private fun setWallpaper(binding: FragmentImageBinding) {
        val drawable = binding.imageFragmentZoomImage.drawable
        val imageUri = drawable.asOrNull<SketchDrawable>()?.uri
            ?.takeIf { it.isNotEmpty() }
        val imageFile = if (imageUri != null) getImageFile(imageUri) else null
        if (imageFile == null) {
            showLongToast("Please wait later")
            return
        }

        lifecycleScope.launch {
            val context = requireActivity().applicationContext
            val success = withContext(Dispatchers.IO) {
                runCatching {
                    FileInputStream(imageFile).use {
                        WallpaperManager.getInstance(context).setStream(it)
                    }
                }.isSuccess
            }
            showLongToast(if (success) "Set wallpaper successfully" else "Set wallpaper failed")
        }
    }

    private fun share(binding: FragmentImageBinding) {
        val drawable = binding.imageFragmentZoomImage.drawable
        val imageUri = drawable.asOrNull<SketchDrawable>()?.uri
            ?.takeIf { it.isNotEmpty() }
        val imageFile = if (imageUri != null) getImageFile(imageUri) else null
        if (imageFile == null) {
            showLongToast("Please wait later")
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, requireContext().getShareFileUri(imageFile))
            type = "image/" + parseFileType(imageFile.name)
        }
        val infoList = requireActivity().packageManager.queryIntentActivities(intent, 0)
        if (infoList == null || infoList.isEmpty()) {
            showLongToast("There is no App on your device to share the picture")
            return
        }
        startActivity(intent)
    }

    private fun save(binding: FragmentImageBinding) {
        val drawable = binding.imageFragmentZoomImage.drawable
        val imageUri = drawable.asOrNull<SketchDrawable>()?.uri
            ?.takeIf { it.isNotEmpty() }
        if (imageUri == null) {
            showLongToast("Please wait later")
            return
        }
        val uriModel = UriModel.match(requireActivity(), imageUri) ?: return
        if (uriModel is FileUriModel) {
            showLongToast("This image is the local no need to save")
            return
        }

        val dataSource = kotlin.runCatching {
            uriModel.getDataSource(requireContext(), imageUri, null)
        }.getOrNull()
        if (dataSource == null) {
            showLongToast("The Image is not ready yet")
            return
        }

        lifecycleScope.launch {
            val context = requireActivity().applicationContext
            val outFile = withContext(Dispatchers.IO) {
                val dir = File(Environment.getExternalStorageDirectory(), "download")
                dir.mkdirs()
                val imageFileName = SketchUtils.generatorTempFileName(dataSource, imageUri)
                val outImageFile = File(dir, imageFileName)

                kotlin.runCatching {
                    outImageFile.createNewFile()
                }.onFailure {
                    return@withContext null
                }

                kotlin.runCatching {
                    FileOutputStream(outImageFile).use { outputStream ->
                        dataSource.inputStream.use { inputStream ->
                            val data = ByteArray(1024)
                            var length: Int
                            while (inputStream.read(data).also { length = it } != -1) {
                                outputStream.write(data, 0, length)
                            }
                        }
                    }
                }.onFailure {
                    return@withContext null
                }

                val intent =
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outImageFile))
                context.sendBroadcast(intent)
                outImageFile
            }
            if (outFile != null) {
                val dir = outFile.parentFile
                showLongToast("Saved to the '${dir.path}' directory")
            } else {
                showLongToast("Failed to save picture")
            }
        }
    }

    private fun getImageFile(imageUri: String?): File? {
        imageUri?.takeIf { it.isNotEmpty() } ?: return null
        val uriModel = UriModel.match(requireContext(), imageUri) ?: return null

        val dataSource = kotlin.runCatching {
            uriModel.getDataSource(requireContext(), imageUri, null)
        }.getOrNull()
        if (dataSource == null) {
            showLongToast("The Image is not ready yet")
            return null
        }

        return kotlin.runCatching {
            dataSource.getFile(requireContext().externalCacheDir, null)
        }.getOrNull()
    }

    private fun parseFileType(fileName: String): String? {
        val lastIndexOf = fileName.lastIndexOf("").takeIf { it >= 0 } ?: return null
        return fileName.substring(lastIndexOf + 1).takeIf { "" != it.trim { it1 -> it1 <= ' ' } }
    }

    private class MenuItem(
        val title: String,
        val clickListener: DialogInterface.OnClickListener?
    )
}