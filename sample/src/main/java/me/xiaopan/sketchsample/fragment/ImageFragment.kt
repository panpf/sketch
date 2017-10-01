package me.xiaopan.sketchsample.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.format.Formatter
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import me.xiaopan.sketch.Sketch
import me.xiaopan.sketch.datasource.DataSource
import me.xiaopan.sketch.display.FadeInImageDisplayer
import me.xiaopan.sketch.drawable.*
import me.xiaopan.sketch.request.*
import me.xiaopan.sketch.state.MemoryCacheStateImage
import me.xiaopan.sketch.uri.FileUriModel
import me.xiaopan.sketch.uri.GetDataSourceException
import me.xiaopan.sketch.uri.UriModel
import me.xiaopan.sketch.util.SketchUtils
import me.xiaopan.sketch.viewfun.huge.HugeImageViewer
import me.xiaopan.sketch.viewfun.zoom.ImageZoomer
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.activity.PageBackgApplyCallback
import me.xiaopan.sketchsample.bean.Image
import me.xiaopan.sketchsample.event.AppConfigChangedEvent
import me.xiaopan.sketchsample.util.AppConfig
import me.xiaopan.sketchsample.util.ApplyWallpaperAsyncTask
import me.xiaopan.sketchsample.util.SaveImageAsyncTask
import me.xiaopan.sketchsample.widget.HintView
import me.xiaopan.sketchsample.widget.MappingView
import me.xiaopan.sketchsample.widget.SampleImageView
import me.xiaopan.ssvt.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.IOException
import java.util.*

@BindContentView(R.layout.fragment_image)
class ImageFragment : BaseFragment() {

    val imageView: SampleImageView by bindView(R.id.image_imageFragment_image)
    val mappingView: MappingView by bindView(R.id.mapping_imageFragment)
    val hintView: HintView by bindView(R.id.hint_imageFragment_hint)

    private var image: Image? = null
    private var loadingImageOptionsKey: String? = null
    private var showTools: Boolean = false

    private var finalShowImageUrl: String? = null

    private val setWindowBackground = SetWindowBackground()
    private val gifPlayFollowPageVisible = GifPlayFollowPageVisible()
    private val showImageHelper = ShowImageHelper()
    private val imageZoomHelper = ImageZoomHelper()
    private val mappingHelper = MappingHelper()
    private val hugeImageHelper = HugeImageHelper()
    private val clickHelper = ClickHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowBackground.onCreate(activity)

        arguments?.let {
            image = it.getParcelable<Image>(PARAM_REQUIRED_STRING_IMAGE_URI)
            loadingImageOptionsKey = it.getString(PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY)
            showTools = it.getBoolean(PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showHighDefinitionImage = AppConfig.getBoolean(context, AppConfig.Key.SHOW_UNSPLASH_RAW_IMAGE)
        finalShowImageUrl = if (showHighDefinitionImage && !TextUtils.isEmpty(image!!.highDefinitionUrl)) image!!.highDefinitionUrl else image!!.regularUrl

        imageZoomHelper.onViewCreated()
        hugeImageHelper.onViewCreated()
        mappingHelper.onViewCreated()
        clickHelper.onViewCreated()
        showImageHelper.onViewCreated()

        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    public override fun onUserVisibleChanged(isVisibleToUser: Boolean) {
        hugeImageHelper.onUserVisibleChanged()
        setWindowBackground.onUserVisibleChanged()
        gifPlayFollowPageVisible.onUserVisibleChanged()
    }

    @Suppress("unused")
    @Subscribe
    fun onEvent(event: AppConfigChangedEvent) {
        if (AppConfig.Key.SUPPORT_ZOOM == event.key) {
            imageZoomHelper.onConfigChanged()
            mappingHelper.onViewCreated()
        } else if (AppConfig.Key.READ_MODE == event.key) {
            imageZoomHelper.onReadModeConfigChanged()
        } else if (AppConfig.Key.SUPPORT_HUGE_IMAGE == event.key) {
            hugeImageHelper.onConfigChanged()
            mappingHelper.onViewCreated()
        }
    }

    class PlayImageEvent

    private inner class ShowImageHelper : DisplayListener, DownloadProgressListener {
        fun onViewCreated() {
            imageView.displayListener = this
            imageView.downloadProgressListener = this

            initOptions()
            imageView.displayImage(finalShowImageUrl!!)
        }

        private fun initOptions() {
            imageView.page = SampleImageView.Page.DETAIL

            val options = imageView.options

            // 允许播放GIF
            options.isDecodeGifImage = true

            // 有占位图选项信息的话就使用内存缓存占位图但不使用任何显示器，否则就是用渐入显示器
            if (!TextUtils.isEmpty(loadingImageOptionsKey)) {
                val uriModel = UriModel.match(activity, finalShowImageUrl!!)
                var cachedRefBitmap: SketchRefBitmap? = null
                var memoryCacheKey: String? = null
                if (uriModel != null) {
                    memoryCacheKey = SketchUtils.makeRequestKey(finalShowImageUrl!!, uriModel, loadingImageOptionsKey!!)
                    cachedRefBitmap = Sketch.with(activity).configuration.memoryCache.get(memoryCacheKey)
                }
                if (cachedRefBitmap != null) {
                    options.loadingImage = MemoryCacheStateImage(memoryCacheKey, null)
                } else {
                    options.displayer = FadeInImageDisplayer()
                }
            } else {
                options.displayer = FadeInImageDisplayer()
            }
        }

        override fun onStarted() {
            hintView.loading(null)
        }

        override fun onCompleted(drawable: Drawable, imageFrom: ImageFrom, imageAttrs: ImageAttrs) {
            hintView.hidden()

            setWindowBackground.onDisplayCompleted()
            gifPlayFollowPageVisible.onDisplayCompleted()
        }

        override fun onError(cause: ErrorCause) {
            hintView.hint(R.drawable.ic_error, "图片显示失败", "重新显示") { imageView.displayImage(finalShowImageUrl!!) }
        }

        override fun onCanceled(cause: CancelCause) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (cause) {
                CancelCause.PAUSE_DOWNLOAD -> hintView.hint(R.drawable.ic_error, "为节省流量已暂停下载新图片", "不管了，直接下载") {
                    val requestLevel = imageView.options.requestLevel
                    imageView.options.requestLevel = RequestLevel.NET
                    imageView.displayImage(finalShowImageUrl!!)
                    imageView.options.requestLevel = requestLevel
                }
                CancelCause.PAUSE_LOAD -> hintView.hint(R.drawable.ic_error, "已暂停加载新图片", "直接加载") {
                    val requestLevel = imageView.options.requestLevel
                    imageView.options.requestLevel = RequestLevel.NET
                    imageView.displayImage(finalShowImageUrl!!)
                    imageView.options.requestLevel = requestLevel
                }
            }
        }

        override fun onUpdateDownloadProgress(totalLength: Int, completedLength: Int) {
            hintView.setProgress(totalLength, completedLength)
        }
    }

    private inner class SetWindowBackground {
        private var pageBackgApplyCallback: PageBackgApplyCallback? = null

        fun onCreate(activity: Activity) {
            if (activity is PageBackgApplyCallback) {
                setWindowBackground.pageBackgApplyCallback = activity
            }
        }

        fun onUserVisibleChanged() {
            if (pageBackgApplyCallback != null && isVisibleToUser) {
                pageBackgApplyCallback!!.onApplyBackground(finalShowImageUrl)
            }
        }

        fun onDisplayCompleted() {
            onUserVisibleChanged()
        }
    }

    private inner class GifPlayFollowPageVisible {
        fun onUserVisibleChanged() {
            val drawable = imageView.drawable
            val lastDrawable = SketchUtils.getLastDrawable(drawable)
            if (lastDrawable != null && lastDrawable is SketchGifDrawable) {
                (lastDrawable as SketchGifDrawable).followPageVisible(isVisibleToUser, false)
            }
        }

        fun onDisplayCompleted() {
            val drawable = imageView.drawable
            val lastDrawable = SketchUtils.getLastDrawable(drawable)
            if (lastDrawable != null && lastDrawable is SketchGifDrawable) {
                (lastDrawable as SketchGifDrawable).followPageVisible(isVisibleToUser, true)
            }
        }
    }

    private inner class ImageZoomHelper {
        fun onViewCreated() {
            imageView.isZoomEnabled = AppConfig.getBoolean(imageView.context, AppConfig.Key.SUPPORT_ZOOM)
            onReadModeConfigChanged()
        }

        fun onConfigChanged() {
            onViewCreated()
        }

        fun onReadModeConfigChanged() {
            if (imageView.isZoomEnabled) {
                val readMode = AppConfig.getBoolean(activity, AppConfig.Key.READ_MODE)
                imageView.imageZoomer!!.isReadMode = readMode
            }
        }
    }

    private inner class HugeImageHelper {
        fun onViewCreated() {
            imageView.isHugeImageEnabled = AppConfig.getBoolean(imageView.context, AppConfig.Key.SUPPORT_HUGE_IMAGE)

            // 初始化超大图查看器的暂停状态，这一步很重要
            if (AppConfig.getBoolean(activity, AppConfig.Key.PAGE_VISIBLE_TO_USER_DECODE_HUGE_IMAGE) && imageView.isHugeImageEnabled) {
                imageView.hugeImageViewer!!.setPause(!isVisibleToUser)
            }
        }

        fun onConfigChanged() {
            onViewCreated()
        }

        fun onUserVisibleChanged() {
            // 不可见的时候暂停超大图查看器，节省内存
            if (AppConfig.getBoolean(activity, AppConfig.Key.PAGE_VISIBLE_TO_USER_DECODE_HUGE_IMAGE)) {
                if (imageView.isHugeImageEnabled) {
                    imageView.hugeImageViewer!!.setPause(!isVisibleToUser)
                }
            } else {
                if (imageView.isHugeImageEnabled
                        && isVisibleToUser && imageView.hugeImageViewer!!.isPaused) {
                    imageView.hugeImageViewer!!.setPause(false)
                }
            }
        }
    }

    private inner class MappingHelper {
        fun onViewCreated() {
            // MappingView跟随碎片变化刷新碎片区域
            if (imageView.isHugeImageEnabled) {
                imageView.hugeImageViewer!!.onTileChangedListener = HugeImageViewer.OnTileChangedListener { hugeImageViewer -> mappingView.tileChanged(hugeImageViewer) }
            }

            // MappingView跟随Matrix变化刷新显示区域
            if (imageView.isZoomEnabled) {
                imageView.imageZoomer!!.addOnMatrixChangeListener(object : ImageZoomer.OnMatrixChangeListener {
                    internal var visibleRect = Rect()

                    override fun onMatrixChanged(imageZoomer: ImageZoomer) {
                        imageZoomer.getVisibleRect(visibleRect)
                        mappingView.update(imageZoomer.drawableSize, visibleRect)
                    }
                })
            }

            // 点击MappingView定位到指定位置
            mappingView.setOnSingleClickListener(MappingView.OnSingleClickListener { x, y ->
                val drawable = imageView.drawable ?: return@OnSingleClickListener false

                if (drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
                    return@OnSingleClickListener false
                }

                if (mappingView.width == 0 || mappingView.height == 0) {
                    return@OnSingleClickListener false
                }

                val widthScale = drawable.intrinsicWidth.toFloat() / mappingView.width
                val heightScale = drawable.intrinsicHeight.toFloat() / mappingView.height
                val realX = x * widthScale
                val realY = y * heightScale

                val showLocationAnimation = AppConfig.getBoolean(imageView.context, AppConfig.Key.LOCATION_ANIMATE)
                location(realX, realY, showLocationAnimation)
            })

            mappingView.options.displayer = FadeInImageDisplayer()
            mappingView.options.setMaxSize(600, 600)
            mappingView.displayImage(finalShowImageUrl!!)

            mappingView.visibility = if (showTools) View.VISIBLE else View.GONE
        }

        fun location(x: Float, y: Float, animate: Boolean): Boolean {
            if (!imageView.isZoomEnabled) {
                return false
            }

            imageView.imageZoomer!!.location(x, y, animate)
            return true
        }
    }

    private inner class ClickHelper {

        fun onViewCreated() {
            // 将单击事件传递给上层Activity
            imageView.onClickListener = View.OnClickListener { v ->
                val parentFragment = parentFragment
                if (parentFragment != null && parentFragment is ImageZoomer.OnViewTapListener) {
                    (parentFragment as ImageZoomer.OnViewTapListener).onViewTap(v, 0f, 0f)
                }
            }

            // 长按显示图片信息与控制菜单
            imageView.setOnLongClickListener {
                show()
                true
            }
        }

        fun show() {
            val builder = AlertDialog.Builder(activity)

            val zoomEnabled = imageView.isZoomEnabled
            val imageZoomer = if (zoomEnabled) imageView.imageZoomer else null
            val hugeImageEnabled = imageView.isHugeImageEnabled
            val hugeImageViewer = imageView.hugeImageViewer
            val drawable = SketchUtils.getLastDrawable(imageView.drawable)

            val menuItemList = LinkedList<MenuItem>()

            val imageInfo: String
            if (drawable is SketchLoadingDrawable) {
                imageInfo = "图片正在加载，请稍后"
            } else if (drawable is SketchDrawable) {
                imageInfo = makeImageInfoWithZoom(drawable, drawable as SketchDrawable)
            } else {
                imageInfo = "未知来源图片"
            }
            menuItemList.add(MenuItem(imageInfo, null))

            val scaleTypeTitle = "切换ScaleType（" + (if (zoomEnabled) imageZoomer!!.scaleType else imageView.scaleType) + "）"
            menuItemList.add(MenuItem(scaleTypeTitle, DialogInterface.OnClickListener { _, _ -> showScaleTypeMenu() }))

            val hugeImageTileTitle = if (hugeImageEnabled) if (hugeImageViewer!!.isShowTileRect) "不显示分块区域" else "显示分块区域" else "分块区域（未开启大图功能）"
            menuItemList.add(MenuItem(hugeImageTileTitle, DialogInterface.OnClickListener { _, _ -> setShowTile() }))

            val readModeTitle = if (zoomEnabled) if (imageZoomer!!.isReadMode) "关闭阅读模式" else "开启阅读模式" else "阅读模式（未开启缩放功能）"
            menuItemList.add(MenuItem(readModeTitle, DialogInterface.OnClickListener { _, _ -> setReadMode() }))

            val moreTitle = "更多功能"
            menuItemList.add(MenuItem(moreTitle, DialogInterface.OnClickListener { _, _ -> showMoreMenu() }))

            val items = arrayOfNulls<String>(menuItemList.size)
            var w = 0
            val size = menuItemList.size
            while (w < size) {
                items[w] = menuItemList[w].title
                w++
            }

            builder.setItems(items) { dialog, which ->
                dialog.dismiss()

                menuItemList[which].clickListener?.onClick(dialog, which)
            }

            builder.setNegativeButton("取消", null)
            builder.show()
        }

        fun makeImageInfoWithZoom(drawable: Drawable, sketchDrawable: SketchDrawable): String {
            val messageBuilder = StringBuilder()

            messageBuilder.append(imageView.makeImageInfo(drawable, sketchDrawable))

            if (imageView.isZoomEnabled) {
                val imageZoomer = imageView.imageZoomer

                messageBuilder.append("\n")
                messageBuilder.append("\n")
                messageBuilder.append("缩放：").append(SketchUtils.formatFloat(imageZoomer!!.zoomScale, 2))

                val visibleRect = Rect()
                imageZoomer.getVisibleRect(visibleRect)
                messageBuilder.append("/").append(visibleRect.toShortString())
            }

            if (imageView.isHugeImageEnabled) {
                val hugeImageViewer = imageView.hugeImageViewer
                if (hugeImageViewer!!.isReady) {
                    val tilesNeedMemory = Formatter.formatFileSize(context, hugeImageViewer.tilesAllocationByteCount)
                    messageBuilder.append("\n")
                    messageBuilder.append("碎片：")
                            .append(hugeImageViewer.tiles)
                            .append("/")
                            .append(hugeImageViewer.tileList.size)
                            .append("/")
                            .append(tilesNeedMemory)
                    messageBuilder.append("\n")
                    messageBuilder.append("碎片解码区域：").append(hugeImageViewer.decodeRect.toShortString())
                    messageBuilder.append("\n")
                    messageBuilder.append("碎片SRC区域：").append(hugeImageViewer.decodeSrcRect.toShortString())
                } else if (hugeImageViewer.isInitializing) {
                    messageBuilder.append("\n")
                    messageBuilder.append("大图功能正在初始化...")
                }
            }

            messageBuilder.append("\n")

            return messageBuilder.toString()
        }

        fun showScaleTypeMenu() {
            val builder = AlertDialog.Builder(activity)

            builder.setTitle("修改ScaleType")

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
                    0 -> imageView.scaleType = ImageView.ScaleType.CENTER
                    1 -> imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    2 -> imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    3 -> imageView.scaleType = ImageView.ScaleType.FIT_START
                    4 -> imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    5 -> imageView.scaleType = ImageView.ScaleType.FIT_END
                    6 -> imageView.scaleType = ImageView.ScaleType.FIT_XY
                }
            }

            builder.setNegativeButton("取消", null)
            builder.show()
        }

        fun showMoreMenu() {
            val menuItemList = LinkedList<MenuItem>()

            val rotateTitle = if (imageView.isZoomEnabled) "顺时针旋转90度（" + imageView.imageZoomer!!.rotateDegrees + "）" else "旋转图片（未开启缩放功能）"
            menuItemList.add(MenuItem(rotateTitle, DialogInterface.OnClickListener { _, _ -> rotate() }))

            menuItemList.add(MenuItem("幻灯片播放", DialogInterface.OnClickListener { _, _ -> play() }))

            menuItemList.add(MenuItem("分享图片", DialogInterface.OnClickListener { _, _ -> share() }))

            menuItemList.add(MenuItem("保存图片", DialogInterface.OnClickListener { _, _ -> save() }))

            menuItemList.add(MenuItem("设为壁纸", DialogInterface.OnClickListener { _, _ -> setWallpaper() }))

            val builder = AlertDialog.Builder(activity)

            val items = arrayOfNulls<String>(menuItemList.size)
            var w = 0
            val size = menuItemList.size
            while (w < size) {
                items[w] = menuItemList[w].title
                w++
            }

            builder.setItems(items) { dialog, which ->
                dialog.dismiss()

                menuItemList[which].clickListener?.onClick(dialog, which)
            }

            builder.setNegativeButton("取消", null)
            builder.show()
        }

        fun setShowTile() {
            if (imageView.isHugeImageEnabled) {
                val hugeImageViewer = imageView.hugeImageViewer
                val newShowTileRect = !hugeImageViewer!!.isShowTileRect
                hugeImageViewer.isShowTileRect = newShowTileRect
            } else {
                Toast.makeText(context, "请先到首页左侧菜单开启大图功能", Toast.LENGTH_SHORT).show()
            }
        }

        fun setReadMode() {
            if (imageView.isZoomEnabled) {
                val imageZoomer = imageView.imageZoomer
                val newReadMode = !imageZoomer!!.isReadMode
                imageZoomer.isReadMode = newReadMode
            } else {
                Toast.makeText(context, "请先到首页左侧菜单开启缩放功能", Toast.LENGTH_SHORT).show()
            }
        }

        fun rotate() {
            if (imageView.isZoomEnabled) {
                if (!imageView.imageZoomer!!.rotateBy(90)) {
                    Toast.makeText(context, "旋转角度必须是90的倍数或开启大图功能后无法使用旋转功能", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "请先到首页左侧菜单开启缩放功能", Toast.LENGTH_SHORT).show()
            }
        }

        fun getImageFile(imageUri: String?): File? {
            if (TextUtils.isEmpty(imageUri)) {
                return null
            }

            val uriModel = UriModel.match(context, imageUri!!)
            if (uriModel == null) {
                Toast.makeText(activity, "我去，怎么会有这样的URL " + imageUri, Toast.LENGTH_LONG).show()
                return null
            }

            val dataSource: DataSource
            try {
                dataSource = uriModel.getDataSource(context, imageUri, null)
            } catch (e: GetDataSourceException) {
                e.printStackTrace()
                Toast.makeText(activity, "图片还没有准备好", Toast.LENGTH_LONG).show()
                return null
            }

            try {
                return dataSource.getFile(context.externalCacheDir, null)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        fun share() {
            val drawable = imageView.drawable
            val imageUri = if (drawable != null && drawable is SketchDrawable) (drawable as SketchDrawable).uri else null
            if (TextUtils.isEmpty(imageUri)) {
                Toast.makeText(activity, "稍等一会儿", Toast.LENGTH_LONG).show()
                return
            }

            val imageFile = getImageFile(imageUri)
            if (imageFile == null) {
                Toast.makeText(activity, "图片还没有准备好", Toast.LENGTH_LONG).show()
                return
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile))
            intent.type = "image/" + parseFileType(imageFile.name)!!

            val infoList = activity.packageManager.queryIntentActivities(intent, 0)
            if (infoList == null || infoList.isEmpty()) {
                Toast.makeText(activity, "您的设备上没有能够分享的APP", Toast.LENGTH_LONG).show()
                return
            }

            startActivity(intent)
        }

        fun setWallpaper() {
            val drawable = imageView.drawable
            val imageUri = if (drawable != null && drawable is SketchDrawable) (drawable as SketchDrawable).uri else null
            if (TextUtils.isEmpty(imageUri)) {
                Toast.makeText(activity, "稍等一会儿", Toast.LENGTH_LONG).show()
                return
            }

            val imageFile = getImageFile(imageUri)
            if (imageFile == null) {
                Toast.makeText(activity, "图片还没有准备好", Toast.LENGTH_LONG).show()
                return
            }

            ApplyWallpaperAsyncTask(activity, imageFile).execute(0)
        }

        fun play() {
            EventBus.getDefault().post(PlayImageEvent())
        }

        fun save() {
            val drawable = imageView.drawable
            val imageUri = if (drawable != null && drawable is SketchDrawable) (drawable as SketchDrawable).uri else null
            if (TextUtils.isEmpty(imageUri)) {
                Toast.makeText(activity, "稍等一会儿", Toast.LENGTH_LONG).show()
                return
            }

            val uriModel = UriModel.match(context, imageUri!!)
            if (uriModel == null) {
                Toast.makeText(activity, "我去，怎么会有这样的URL " + imageUri, Toast.LENGTH_LONG).show()
                return
            }

            if (uriModel is FileUriModel) {
                Toast.makeText(activity, "当前图片本就是本地的无需保存", Toast.LENGTH_LONG).show()
                return
            }

            val dataSource: DataSource
            try {
                dataSource = uriModel.getDataSource(context, imageUri, null)
            } catch (e: GetDataSourceException) {
                e.printStackTrace()
                Toast.makeText(activity, "图片还没有准备好哦，再等一会儿吧！", Toast.LENGTH_LONG).show()
                return
            }

            SaveImageAsyncTask(activity, dataSource, imageUri).execute("")
        }

        fun parseFileType(fileName: String): String? {
            val lastIndexOf = fileName.lastIndexOf(".")
            if (lastIndexOf < 0) {
                return null
            }
            val fileType = fileName.substring(lastIndexOf + 1)
            if ("" == fileType.trim { it <= ' ' }) {
                return null
            }
            return fileType
        }

        private inner class MenuItem(val title: String, var clickListener: DialogInterface.OnClickListener?)
    }

    companion object {
        val PARAM_REQUIRED_STRING_IMAGE_URI = "PARAM_REQUIRED_STRING_IMAGE_URI"
        val PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY = "PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY"
        val PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS = "PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS"

        fun build(image: Image, loadingImageOptionsId: String?, showTools: Boolean): ImageFragment {
            val bundle = Bundle()
            bundle.putParcelable(PARAM_REQUIRED_STRING_IMAGE_URI, image)
            bundle.putString(PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY, loadingImageOptionsId)
            bundle.putBoolean(PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS, showTools)
            val fragment = ImageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}