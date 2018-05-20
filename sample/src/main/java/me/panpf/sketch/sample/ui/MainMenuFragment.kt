package me.panpf.sketch.sample.ui

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.text.format.Formatter
import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.fragment_main_menu.*
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.ktx.isPortraitOrientation
import me.panpf.sketch.SLog
import me.panpf.sketch.Sketch
import me.panpf.sketch.sample.*
import me.panpf.sketch.sample.bean.CheckMenu
import me.panpf.sketch.sample.bean.InfoMenu
import me.panpf.sketch.sample.event.*
import me.panpf.sketch.sample.item.CheckMenuItemFactory
import me.panpf.sketch.sample.item.InfoMenuItemFactory
import me.panpf.sketch.sample.item.MenuTitleItemFactory
import me.panpf.sketch.sample.item.PageMenuItemFactory
import me.panpf.sketch.sample.util.AppConfig
import me.panpf.sketch.sample.util.DeviceUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.*

@BindContentView(R.layout.fragment_main_menu)
class MainMenuFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  + DeviceUtils.getNavigationBarHeightByUiVisibility(this) 是为了兼容 MIX 2
        mainMenuBgImage.updateLayoutParams {
            width = resources.displayMetrics.widthPixels
            height = resources.displayMetrics.heightPixels
            if (isPortraitOrientation()) {
                height += DeviceUtils.getWindowHeightSupplement(activity)
            } else {
                width += DeviceUtils.getWindowHeightSupplement(activity)
            }
        }

        mainMenuBgImage.setOptions(ImageOptions.WINDOW_BACKGROUND)
        mainMenuBgImage.options.displayer = null

        // KITKAT 以后要内容要延伸到状态栏下面，所以菜单列表的顶部要留出状态栏的高度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DeviceUtils.getStatusBarHeight(resources).takeIf { it > 0 }?.also { mainMenuRecycler.updatePadding(top = it) }
        }

        mainMenuRecycler.layoutManager = LinearLayoutManager(context)
        mainMenuRecycler.adapter = AssemblyRecyclerAdapter(makeMenuList()).apply {
            addItemFactory(MenuTitleItemFactory())
            addItemFactory(PageMenuItemFactory(object : PageMenuItemFactory.OnClickItemListener {
                override fun onClickItem(page: Page) {
                    EventBus.getDefault().post(SwitchMainPageEvent(page))
                }
            }))
            addItemFactory(CheckMenuItemFactory())
            addItemFactory(InfoMenuItemFactory())
        }

        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)

        super.onDestroyView()
    }

    private fun makeMenuList(): List<Any> {
        val appContext = requireNotNull(context)

        val menuClickListener = View.OnClickListener { EventBus.getDefault().post(CloseDrawerEvent()) }

        val menuList = ArrayList<Any>()

        menuList.add("Sample Page")
        Page.normalPage.filterTo(menuList) { !it.isDisable }

        menuList.add("Test Page")
        Page.testPage.filterTo(menuList) { !it.isDisable }

        menuList.add("Cache Menu")
        menuList.add(CacheInfoMenu(appContext, "Memory", "Memory Cache (Click Clean)", menuClickListener))
        menuList.add(CacheInfoMenu(appContext, "BitmapPool", "Bitmap Pool (Click Clean)", menuClickListener))
        menuList.add(CacheInfoMenu(appContext, "Disk", "Disk Cache (Click Clean)", menuClickListener))
        menuList.add(CheckMenu(appContext, "Disable Memory Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Disable Bitmap Pool", AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Disable Disk Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK, null, menuClickListener))

        menuList.add("Gesture Zoom Menu")
        menuList.add(CheckMenu(appContext, "Enabled Gesture Zoom In Detail Page", AppConfig.Key.SUPPORT_ZOOM, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Enabled Read Mode In Detail Page", AppConfig.Key.READ_MODE, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Enabled Location Animation In Detail Page", AppConfig.Key.LOCATION_ANIMATE, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Pause Block Display When Page Not Visible In Detail Page", AppConfig.Key.PAUSE_BLOCK_DISPLAY_WHEN_PAGE_NOT_VISIBLE, null, menuClickListener))

        menuList.add("GIF Menu")
        menuList.add(CheckMenu(appContext, "Auto Play GIF In List", AppConfig.Key.PLAY_GIF_ON_LIST, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Click Play GIF In List", AppConfig.Key.CLICK_PLAY_GIF, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Show GIF Flag In List", AppConfig.Key.SHOW_GIF_FLAG, null, menuClickListener))

        menuList.add("Decode Menu")
        menuList.add(CheckMenu(appContext, "In Prefer Quality Over Speed", AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Low Quality Bitmap", AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Enabled Thumbnail Mode In List", AppConfig.Key.THUMBNAIL_MODE, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Cache Processed Image In Disk", AppConfig.Key.CACHE_PROCESSED_IMAGE, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Disabled Correct Image Orientation", AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION, null, menuClickListener))

        menuList.add("Other Menu")
        menuList.add(CheckMenu(appContext, "Show Round Rect In Photo List", AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Show Unsplash Raw Image In Detail Page", AppConfig.Key.SHOW_UNSPLASH_RAW_IMAGE, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Show Mapping Thumbnail In Detail Page", AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Show Press Status In List", AppConfig.Key.CLICK_SHOW_PRESSED_STATUS, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Show Image From Corner Mark", AppConfig.Key.SHOW_IMAGE_FROM_FLAG, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Show Download Progress In List", AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Click Show Image On Pause Download In List", AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Click Retry On Error In List", AppConfig.Key.CLICK_RETRY_ON_FAILED, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Scrolling Pause Load Image In List", AppConfig.Key.SCROLLING_PAUSE_LOAD, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Mobile Data Pause Download Image", AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Long Clock Show Image Info", AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO, null, menuClickListener))

        menuList.add("Log Menu")
        menuList.add(object : InfoMenu("Log Level") {
            override fun getInfo(): String {
                return when (SLog.getLevel()) {
                    SLog.LEVEL_VERBOSE -> "VERBOSE"
                    SLog.LEVEL_DEBUG -> "DEBUG"
                    SLog.LEVEL_INFO -> "INFO"
                    SLog.LEVEL_WARNING -> "WARNING"
                    SLog.LEVEL_ERROR -> "ERROR"
                    SLog.LEVEL_NONE -> "NONE"
                    else -> "Unknown"
                }
            }

            override fun onClick(adapter: AssemblyAdapter?) {
                AlertDialog.Builder(activity).apply {
                    setTitle("Switch Log Level")
                    val items = arrayOf("VERBOSE" + if (SLog.getLevel() == SLog.LEVEL_VERBOSE) " (*)" else "", "DEBUG" + if (SLog.getLevel() == SLog.LEVEL_DEBUG) " (*)" else "", "INFO" + if (SLog.getLevel() == SLog.LEVEL_INFO) " (*)" else "", "WARNING" + if (SLog.getLevel() == SLog.LEVEL_WARNING) " (*)" else "", "ERROR" + if (SLog.getLevel() == SLog.LEVEL_ERROR) " (*)" else "", "NONE" + if (SLog.getLevel() == SLog.LEVEL_NONE) " (*)" else "")
                    setItems(items) { dialog, which ->
                        when (which) {
                            0 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "VERBOSE")
                            1 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "DEBUG")
                            2 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "INFO")
                            3 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "WARNING")
                            4 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "ERROR")
                            5 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "NONE")
                        }
                        mainMenuRecycler.adapter.notifyDataSetChanged()
                    }
                    setPositiveButton("Cancel", null)
                }.show()
            }
        })
        menuList.add(CheckMenu(appContext, "Output Flow Log", AppConfig.Key.LOG_REQUEST, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Output Cache Log", AppConfig.Key.LOG_CACHE, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Output Zoom Log", AppConfig.Key.LOG_ZOOM, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Output Zoom Block Display Log", AppConfig.Key.LOG_ZOOM_BLOCK_DISPLAY, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Output Used Time Log", AppConfig.Key.LOG_TIME, null, menuClickListener))
        menuList.add(CheckMenu(appContext, "Sync Output Log To Disk (cache/sketch_log)", AppConfig.Key.OUT_LOG_2_SDCARD, null, menuClickListener))

        return menuList
    }

    @Suppress("unused")
    @Subscribe
    fun onDrawerOpendEvent(@Suppress("UNUSED_PARAMETER") drawerOpenedEvent: DrawerOpenedEvent) {
        mainMenuRecycler.adapter.notifyDataSetChanged()
    }

    @Suppress("unused")
    @Subscribe
    fun onChangeMainPageBgEvent(eventChange: ChangeMainPageBgEvent) {
        if (!TextUtils.isEmpty(eventChange.imageUrl)) {
            mainMenuBgImage.displayImage(eventChange.imageUrl)
        }
    }
}

class CacheInfoMenu(val context: Context, val type: String, title: String, val menuClickListener: View.OnClickListener) : InfoMenu(title) {
    override fun getInfo(): String {
        when (type) {
            "Memory" -> {
                val memoryCache = Sketch.with(context).configuration.memoryCache
                val usedSizeFormat = Formatter.formatFileSize(context, memoryCache.size)
                val maxSizeFormat = Formatter.formatFileSize(context, memoryCache.maxSize)
                return "$usedSizeFormat/$maxSizeFormat"
            }
            "Disk" -> {
                val diskCache = Sketch.with(context).configuration.diskCache
                val usedSizeFormat = Formatter.formatFileSize(context, diskCache.size)
                val maxSizeFormat = Formatter.formatFileSize(context, diskCache.maxSize)
                return "$usedSizeFormat/$maxSizeFormat"
            }
            "BitmapPool" -> {
                val bitmapPool = Sketch.with(context).configuration.bitmapPool
                val usedSizeFormat = Formatter.formatFileSize(context, bitmapPool.size.toLong())
                val maxSizeFormat = Formatter.formatFileSize(context, bitmapPool.maxSize.toLong())
                return "$usedSizeFormat/$maxSizeFormat"
            }
            else -> return "Unknown Type"
        }
    }

    override fun onClick(adapter: AssemblyAdapter?) {
        when (type) {
            "Memory" -> {
                Sketch.with(context).configuration.memoryCache.clear()
                menuClickListener.onClick(null)
                adapter?.notifyDataSetChanged()

                EventBus.getDefault().post(CacheCleanEvent())
            }
            "Disk" -> {
                CleanCacheTask(WeakReference(context), adapter, menuClickListener).execute(0)
            }
            "BitmapPool" -> {
                Sketch.with(context).configuration.bitmapPool.clear()
                menuClickListener.onClick(null)
                adapter?.notifyDataSetChanged()

                EventBus.getDefault().post(CacheCleanEvent())
            }
        }
    }

    class CleanCacheTask(val reference: WeakReference<Context>,
                         val adapter: AssemblyAdapter?,
                         private val menuClickListener: View.OnClickListener) : AsyncTask<Int, Int, Int>() {

        override fun doInBackground(vararg params: Int?): Int? {
            reference.get()?.let { Sketch.with(it).configuration.diskCache.clear() }

            return null
        }

        override fun onPostExecute(integer: Int?) {
            super.onPostExecute(integer)

            reference.get()?.let {
                menuClickListener.onClick(null)
                adapter?.notifyDataSetChanged()

                EventBus.getDefault().post(CacheCleanEvent())
            }
        }
    }
}

enum class Page constructor(val showName: String, val fragmentClass: Class<out Fragment>, val test: Boolean, val isDisable: Boolean) {
    UNSPLASH("Unsplash", UnsplashPhotosFragment::class.java, false, false),
    SEARCH("GIF Search", SearchFragment::class.java, false, false),
    MY_PHOTOS("My Photos", MyPhotosFragment::class.java, false, false),
    APP_LIST("My Apps", AppListFragment::class.java, false, false),
    ABOUT("About Sketch", AboutFragment::class.java, false, false),

    BLOCK_DISPLAY_TEST("Block Display Huge Image", BlockDisplayTestFragment::class.java, true, false),
    IMAGE_PROCESSOR_TEST("Image Processor Test", ImageProcessorTestFragment::class.java, true, false),
    IMAGE_SHAPER_TEST("Image Shaper Test", ImageShaperTestFragment::class.java, true, false),
    REPEAT_LOAD_OR_DOWNLOAD_TEST("Repeat Load Or Download Test", RepeatLoadOrDownloadTestFragment::class.java, true, false),
    IN_BITMAP_TEST("inBitmap Test", InBitmapTestFragment::class.java, true, false),
    IMAGE_ORIENTATION_TEST("Image Orientation Test", ImageOrientationTestHomeFragment::class.java, true, false),
    BASE64_IMAGE_TEST("Base64 Image Test", Base64ImageTestFragment::class.java, true, false),
    OTHER_TEST("Other Test", OtherTestFragment::class.java, true, !BuildConfig.DEBUG);

    val fragment: Fragment?
        get() {
            return try {
                fragmentClass.newInstance()
            } catch (e: InstantiationException) {
                e.printStackTrace()
                null
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                null
            }
        }

    companion object {
        val normalPage: Array<Page>
            get() {
                return values().filterTo(LinkedList()) { !it.test }.toTypedArray()
            }

        val testPage: Array<Page>
            get() {
                return values().filterTo(LinkedList()) { it.test }.toTypedArray()
            }
    }
}