/*
 * Copyright 2013 Peng fei Pan
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

package me.panpf.sketch.sample.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.Formatter
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import me.panpf.sketch.sample.*
import me.panpf.sketch.sample.adapter.itemfactory.CheckMenuItemFactory
import me.panpf.sketch.sample.adapter.itemfactory.InfoMenuItemFactory
import me.panpf.sketch.sample.adapter.itemfactory.MenuTitleItemFactory
import me.panpf.sketch.sample.adapter.itemfactory.PageMenuItemFactory
import me.panpf.sketch.sample.bean.CheckMenu
import me.panpf.sketch.sample.bean.InfoMenu
import me.panpf.sketch.sample.event.CacheCleanEvent
import me.panpf.sketch.sample.fragment.*
import me.panpf.sketch.sample.util.AnimationUtils
import me.panpf.sketch.sample.util.AppConfig
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator
import me.panpf.sketch.sample.widget.SampleImageView
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter
import me.xiaopan.psts.PagerSlidingTabStrip
import me.xiaopan.sketch.SLog
import me.xiaopan.sketch.Sketch
import me.xiaopan.sketch.util.SketchUtils
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.*

/**
 * 首页
 */
@SuppressLint("RtlHardcoded")
@BindContentView(R.layout.activity_main)
class MainActivity : BaseActivity(), AppListFragment.GetAppListTagStripListener, PageBackgApplyCallback {

    val contentView: View by bindView(R.id.layout_main_content)
    val appListTabStrip: PagerSlidingTabStrip by bindView(R.id.tabStrip_main_appList)
    val drawerLayout: DrawerLayout by bindView(R.id.drawer_main_content)
    val menuRecyclerView: RecyclerView by bindView(R.id.recycler_main_menu)
    val leftMenuView: ViewGroup by bindView(R.id.layout_main_leftMenu)
    val backgroundImageView: SampleImageView by bindView(R.id.image_main_background)
    val menuBackgroundImageView: SampleImageView by bindView(R.id.image_main_menuBackground)

    private var lastClickBackTime: Long = 0
    private var page: Page? = null

    private var toggleDrawable: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initData()
        startService(Intent(baseContext, NotificationService::class.java))
        switchPage(Page.UNSPLASH)
    }

    private fun initViews() {
        //  + DeviceUtils.getNavigationBarHeightByUiVisibility(this) 是为了兼容 MIX 2
        backgroundImageView.layoutParams?.let {
            it.width = resources.displayMetrics.widthPixels
            it.height = resources.displayMetrics.heightPixels + DeviceUtils.getWindowHeightSupplement(this)
            backgroundImageView.layoutParams = it
        }

        backgroundImageView.setOptions(ImageOptions.WINDOW_BACKGROUND)

        //  + DeviceUtils.getNavigationBarHeightByUiVisibility(this) 是为了兼容 MIX 2
        menuBackgroundImageView.layoutParams?.let {
            it.width = resources.displayMetrics.widthPixels
            it.height = resources.displayMetrics.heightPixels + DeviceUtils.getWindowHeightSupplement(this)
            menuBackgroundImageView.layoutParams = it
        }

        menuBackgroundImageView.setOptions(ImageOptions.WINDOW_BACKGROUND)
        menuBackgroundImageView.options.displayer = null

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shadow_down_left, Gravity.LEFT)

        // 设置左侧菜单的宽度为屏幕的一半
        val params = leftMenuView.layoutParams
        params.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
        leftMenuView.layoutParams = params

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toggleDrawable = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)

        appListTabStrip.setTabViewFactory(TitleTabFactory(arrayOf("APP", "PACKAGE"), baseContext))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = DeviceUtils.getStatusBarHeight(resources)
            if (statusBarHeight > 0) {
                contentView.setPadding(contentView.paddingLeft, statusBarHeight, contentView.paddingRight, contentView.paddingBottom)
                menuRecyclerView.setPadding(contentView.paddingLeft, statusBarHeight, contentView.paddingRight, contentView.paddingBottom)
            } else {
                drawerLayout.fitsSystemWindows = true
            }
        }

        drawerLayout.setDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                toggleDrawable!!.onDrawerSlide(drawerView, slideOffset)
            }

            override fun onDrawerOpened(drawerView: View) {
                toggleDrawable!!.onDrawerOpened(drawerView)
                menuRecyclerView.adapter.notifyDataSetChanged()
            }

            override fun onDrawerClosed(drawerView: View) {
                toggleDrawable!!.onDrawerClosed(drawerView)
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })
    }

    private fun initData() {
        val adapter = AssemblyRecyclerAdapter(makeMenuList())
        adapter.addItemFactory(MenuTitleItemFactory())
        adapter.addItemFactory(PageMenuItemFactory(object : PageMenuItemFactory.OnClickItemListener{
            override fun onClickItem(page: Page) {
                switchPage(page)
            }
        }))
        adapter.addItemFactory(CheckMenuItemFactory())
        adapter.addItemFactory(InfoMenuItemFactory())
        menuRecyclerView.layoutManager = LinearLayoutManager(baseContext)
        menuRecyclerView.adapter = adapter

        ImageOrientationCorrectTestFileGenerator.getInstance(baseContext).onAppStart()
    }

    private fun makeMenuList(): List<Any> {
        val menuClickListener = View.OnClickListener { drawerLayout.closeDrawer(Gravity.LEFT) }

        val menuList = ArrayList<Any>()

        menuList.add("Sample Page")
        Page.normalPage.filterTo(menuList) { !it.isDisable }

        menuList.add("Test Page")
        Page.testPage.filterTo(menuList) { !it.isDisable }

        menuList.add("Cache Menu")
        menuList.add(CacheInfoMenu(this@MainActivity, "Memory", "Memory Cache (Click Clean)", menuClickListener))
        menuList.add(CacheInfoMenu(this@MainActivity, "BitmapPool", "Bitmap Pool (Click Clean)", menuClickListener))
        menuList.add(CacheInfoMenu(this@MainActivity, "Disk", "Disk Cache (Click Clean)", menuClickListener))
        menuList.add(CheckMenu(this, "Disable Memory Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY, null, menuClickListener))
        menuList.add(CheckMenu(this, "Disable Bitmap Pool", AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL, null, menuClickListener))
        menuList.add(CheckMenu(this, "Disable Disk Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK, null, menuClickListener))

        menuList.add("Gesture Zoom Menu")
        menuList.add(CheckMenu(this, "Enabled Gesture Zoom In Detail Page", AppConfig.Key.SUPPORT_ZOOM, null, menuClickListener))
        menuList.add(CheckMenu(this, "Enabled Read Mode In Detail Page", AppConfig.Key.READ_MODE, null, menuClickListener))
        menuList.add(CheckMenu(this, "Enabled Location Animation In Detail Page", AppConfig.Key.LOCATION_ANIMATE, null, menuClickListener))
        menuList.add(CheckMenu(this, "Pause Block Display When Page Not Visible In Detail Page", AppConfig.Key.PAUSE_BLOCK_DISPLAY_WHEN_PAGE_NOT_VISIBLE, null, menuClickListener))

        menuList.add("GIF Menu")
        menuList.add(CheckMenu(this, "Auto Play GIF In List", AppConfig.Key.PLAY_GIF_ON_LIST, null, menuClickListener))
        menuList.add(CheckMenu(this, "Click Play GIF In List", AppConfig.Key.CLICK_PLAY_GIF, null, menuClickListener))
        menuList.add(CheckMenu(this, "Show GIF Flag In List", AppConfig.Key.SHOW_GIF_FLAG, null, menuClickListener))

        menuList.add("Decode Menu")
        menuList.add(CheckMenu(this, "In Prefer Quality Over Speed", AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED, null, menuClickListener))
        menuList.add(CheckMenu(this, "Low Quality Bitmap", AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE, null, menuClickListener))
        menuList.add(CheckMenu(this, "Enabled Thumbnail Mode In List", AppConfig.Key.THUMBNAIL_MODE, null, menuClickListener))
        menuList.add(CheckMenu(this, "Cache Processed Image In Disk", AppConfig.Key.CACHE_PROCESSED_IMAGE, null, menuClickListener))
        menuList.add(CheckMenu(this, "Disabled Correct Image Orientation", AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION, null, menuClickListener))

        menuList.add("Other Menu")
        menuList.add(CheckMenu(this, "Show Round Rect In Photo List", AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST, null, menuClickListener))
        menuList.add(CheckMenu(this, "Show Unsplash Raw Image In Detail Page", AppConfig.Key.SHOW_UNSPLASH_RAW_IMAGE, null, menuClickListener))
        menuList.add(CheckMenu(this, "Show Mapping Thumbnail In Detail Page", AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL, null, menuClickListener))
        menuList.add(CheckMenu(this, "Show Press Status In List", AppConfig.Key.CLICK_SHOW_PRESSED_STATUS, null, menuClickListener))
        menuList.add(CheckMenu(this, "Show Image From Corner Mark", AppConfig.Key.SHOW_IMAGE_FROM_FLAG, null, menuClickListener))
        menuList.add(CheckMenu(this, "Show Download Progress In List", AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS, null, menuClickListener))
        menuList.add(CheckMenu(this, "Click Show Image On Pause Download In List", AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD, null, menuClickListener))
        menuList.add(CheckMenu(this, "Click Retry On Error In List", AppConfig.Key.CLICK_RETRY_ON_FAILED, null, menuClickListener))
        menuList.add(CheckMenu(this, "Scrolling Pause Load Image In List", AppConfig.Key.SCROLLING_PAUSE_LOAD, null, menuClickListener))
        menuList.add(CheckMenu(this, "Mobile Data Pause Download Image", AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD, null, menuClickListener))
        menuList.add(CheckMenu(this, "Long Clock Show Image Info", AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO, null, menuClickListener))

        menuList.add("Log Menu")
        menuList.add(object : InfoMenu("Log Level") {
            @SuppressLint("SwitchIntDef")
            override fun getInfo(): String {
                when (SLog.getLevel()) {
                    SLog.LEVEL_VERBOSE -> return "VERBOSE"
                    SLog.LEVEL_DEBUG -> return "DEBUG"
                    SLog.LEVEL_INFO -> return "INFO"
                    SLog.LEVEL_WARNING -> return "WARNING"
                    SLog.LEVEL_ERROR -> return "ERROR"
                    SLog.LEVEL_NONE -> return "NONE"
                    else -> return "Unknown"
                }
            }

            override fun onClick(adapter: AssemblyRecyclerAdapter) {
                switchLogLevel()
            }
        })
        menuList.add(CheckMenu(this, "Output Flow Log", AppConfig.Key.LOG_REQUEST, null, menuClickListener))
        menuList.add(CheckMenu(this, "Output Cache Log", AppConfig.Key.LOG_CACHE, null, menuClickListener))
        menuList.add(CheckMenu(this, "Output Zoom Log", AppConfig.Key.LOG_ZOOM, null, menuClickListener))
        menuList.add(CheckMenu(this, "Output Zoom Block Display Log", AppConfig.Key.LOG_ZOOM_BLOCK_DISPLAY, null, menuClickListener))
        menuList.add(CheckMenu(this, "Output Used Time Log", AppConfig.Key.LOG_TIME, null, menuClickListener))
        menuList.add(CheckMenu(this, "Sync Output Log To Disk (cache/sketch_log)", AppConfig.Key.OUT_LOG_2_SDCARD, null, menuClickListener))

        return menuList
    }

    override fun isDisableSetFitsSystemWindows(): Boolean {
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // 同步状态，这一步很重要，要不然初始
        toggleDrawable!!.syncState()
        toggleDrawable!!.onDrawerSlide(null, 1.0f)
        toggleDrawable!!.onDrawerSlide(null, 0.5f)
        toggleDrawable!!.onDrawerSlide(null, 0.0f)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggleDrawable!!.onConfigurationChanged(newConfig)
    }

    private fun switchPage(newPage: Page) {
        if (this.page == newPage) {
            return
        }
        this.page = newPage

        if (page == Page.APP_LIST) {
            AnimationUtils.visibleViewByAlpha(appListTabStrip)
        } else {
            AnimationUtils.invisibleViewByAlpha(appListTabStrip)
        }

        supportActionBar!!.title = page!!.showName
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                .replace(R.id.frame_main_content, page!!.fragment)
                .commitAllowingStateLoss()

        drawerLayout.post { drawerLayout.closeDrawer(Gravity.LEFT) }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT)
            } else {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    override fun onGetAppListTabStrip(): PagerSlidingTabStrip {
        return appListTabStrip
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickBackTime > 2000) {
            lastClickBackTime = currentTime
            Toast.makeText(baseContext, "再按一下返回键退出" + resources.getString(R.string.app_name), Toast.LENGTH_SHORT).show()
            return
        }

        super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        var result = true
        try {
            result = super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        return result
    }

    override fun onApplyBackground(imageUri: String?) {
        if (!TextUtils.isEmpty(imageUri)) {
            backgroundImageView.displayImage(imageUri ?: "")
            menuBackgroundImageView.displayImage(imageUri ?: "")
        }
    }

    private fun switchLogLevel() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Switch Log Level")
        val items = arrayOf("VERBOSE" + if (SLog.getLevel() == SLog.LEVEL_VERBOSE) " (*)" else "", "DEBUG" + if (SLog.getLevel() == SLog.LEVEL_DEBUG) " (*)" else "", "INFO" + if (SLog.getLevel() == SLog.LEVEL_INFO) " (*)" else "", "WARNING" + if (SLog.getLevel() == SLog.LEVEL_WARNING) " (*)" else "", "ERROR" + if (SLog.getLevel() == SLog.LEVEL_ERROR) " (*)" else "", "NONE" + if (SLog.getLevel() == SLog.LEVEL_NONE) " (*)" else "")
        builder.setItems(items) { dialog, which ->
            when (which) {
                0 -> AppConfig.putString(baseContext, AppConfig.Key.LOG_LEVEL, "VERBOSE")
                1 -> AppConfig.putString(baseContext, AppConfig.Key.LOG_LEVEL, "DEBUG")
                2 -> AppConfig.putString(baseContext, AppConfig.Key.LOG_LEVEL, "INFO")
                3 -> AppConfig.putString(baseContext, AppConfig.Key.LOG_LEVEL, "WARNING")
                4 -> AppConfig.putString(baseContext, AppConfig.Key.LOG_LEVEL, "ERROR")
                5 -> AppConfig.putString(baseContext, AppConfig.Key.LOG_LEVEL, "NONE")
            }
            menuRecyclerView.adapter.notifyDataSetChanged()
        }
        builder.setPositiveButton("Cancel", null)
        builder.show()
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
                    return values().filterTo(LinkedList<Page>()) { !it.test }.toTypedArray()
                }

            val testPage: Array<Page>
                get() {
                    return values().filterTo(LinkedList<Page>()) { it.test }.toTypedArray()
                }
        }
    }

    class TitleTabFactory(val titles: Array<String>, val context: Context) : PagerSlidingTabStrip.TabViewFactory {

        override fun addTabs(viewGroup: ViewGroup, i: Int) {
            titles.withIndex().forEach { (index, title) ->
                val textView = TextView(context)
                textView.text = title
                val padding = SketchUtils.dp2px(context, 12)
                when (index) {
                    0 -> textView.setPadding(padding, padding, padding / 2, padding)
                    (titles.size - 1) -> textView.setPadding(padding / 2, padding, padding, padding)
                    else -> textView.setPadding(padding / 2, padding, padding / 2, padding)
                }
                textView.gravity = Gravity.CENTER
                textView.setTextColor(context.resources.getColorStateList(R.color.tab))
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                viewGroup.addView(textView)
            }
        }
    }

    class CacheInfoMenu(val activity: MainActivity,
                        val type: String,
                        title: String,
                        val menuClickListener: View.OnClickListener) : InfoMenu(title) {
        override fun getInfo(): String {
            when (type) {
                "Memory" -> {
                    val memoryCache = Sketch.with(activity).configuration.memoryCache
                    val usedSizeFormat = Formatter.formatFileSize(activity, memoryCache.size)
                    val maxSizeFormat = Formatter.formatFileSize(activity, memoryCache.maxSize)
                    return usedSizeFormat + "/" + maxSizeFormat
                }
                "Disk" -> {
                    val diskCache = Sketch.with(activity).configuration.diskCache
                    val usedSizeFormat = Formatter.formatFileSize(activity, diskCache.size)
                    val maxSizeFormat = Formatter.formatFileSize(activity, diskCache.maxSize)
                    return usedSizeFormat + "/" + maxSizeFormat
                }
                "BitmapPool" -> {
                    val bitmapPool = Sketch.with(activity).configuration.bitmapPool
                    val usedSizeFormat = Formatter.formatFileSize(activity, bitmapPool.size.toLong())
                    val maxSizeFormat = Formatter.formatFileSize(activity, bitmapPool.maxSize.toLong())
                    return usedSizeFormat + "/" + maxSizeFormat
                }
                else -> return "Unknown Type"
            }
        }

        override fun onClick(adapter: AssemblyRecyclerAdapter) {
            when (type) {
                "Memory" -> {
                    Sketch.with(activity).configuration.memoryCache.clear()
                    menuClickListener.onClick(null)
                    adapter.notifyDataSetChanged()

                    EventBus.getDefault().post(CacheCleanEvent())
                }
                "Disk" -> {
                    CleanCacheTask(WeakReference(activity), adapter, menuClickListener).execute(0)
                }
                "BitmapPool" -> {
                    Sketch.with(activity).configuration.bitmapPool.clear()
                    menuClickListener.onClick(null)
                    adapter.notifyDataSetChanged()

                    EventBus.getDefault().post(CacheCleanEvent())
                }
            }
        }

        class CleanCacheTask(val activityReference: WeakReference<MainActivity>,
                             val adapter: AssemblyRecyclerAdapter,
                             val menuClickListener: View.OnClickListener) : AsyncTask<Int, Int, Int>() {

            override fun doInBackground(vararg params: Int?): Int? {
                activityReference.get()?.let {
                    Sketch.with(it as Context).configuration.diskCache.clear()
                }

                return null
            }

            override fun onPostExecute(integer: Int?) {
                super.onPostExecute(integer)

                activityReference.get()?.let {
                    menuClickListener.onClick(null)
                    adapter.notifyDataSetChanged()

                    EventBus.getDefault().post(CacheCleanEvent())
                }
            }
        }
    }
}
