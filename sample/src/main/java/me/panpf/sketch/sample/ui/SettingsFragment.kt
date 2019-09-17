package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.SLog
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.bean.CacheInfoMenu
import me.panpf.sketch.sample.bean.CheckMenu
import me.panpf.sketch.sample.bean.InfoMenu
import me.panpf.sketch.sample.item.CheckMenuItem
import me.panpf.sketch.sample.item.InfoMenuItem
import me.panpf.sketch.sample.item.MenuTitleItem

@BindContentView(R.layout.fragment_recycler)
class SettingsFragment : BaseFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh_recyclerFragment.isEnabled = false
        recycler_recyclerFragment_content.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = AssemblyRecyclerAdapter(makeMenuList()).apply {
                addItemFactory(MenuTitleItem.Factory())
                addItemFactory(CheckMenuItem.Factory())
                addItemFactory(InfoMenuItem.Factory())
            }

            updateLayoutParams { width = (resources.displayMetrics.widthPixels * 0.7).toInt() }
        }
    }

    private fun makeMenuList(): List<Any> {
        val appContext = requireNotNull(context)

        val menuList = ArrayList<Any>()

        menuList.add("Cache")
        menuList.add(CacheInfoMenu(appContext, "Memory", "Memory Cache (Click Clean)"))
        menuList.add(CacheInfoMenu(appContext, "BitmapPool", "Bitmap Pool (Click Clean)"))
        menuList.add(CacheInfoMenu(appContext, "Disk", "Disk Cache (Click Clean)"))
        menuList.add(CheckMenu(appContext, "Disable Memory Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY, null))
        menuList.add(CheckMenu(appContext, "Disable Bitmap Pool", AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL, null))
        menuList.add(CheckMenu(appContext, "Disable Disk Cache", AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK, null))

        menuList.add("Zoom")
        menuList.add(CheckMenu(appContext, "Enabled Read Mode In Detail Page", AppConfig.Key.READ_MODE, null))
        menuList.add(CheckMenu(appContext, "Enabled Location Animation In Detail Page", AppConfig.Key.LOCATION_ANIMATE, null))
        menuList.add(CheckMenu(appContext, "Pause Block Display When Page Not Visible In Detail Page", AppConfig.Key.PAUSE_BLOCK_DISPLAY_WHEN_PAGE_NOT_VISIBLE, null))
        menuList.add(CheckMenu(appContext, "Fixed Three Level Zoom Mode", AppConfig.Key.FIXED_THREE_LEVEL_ZOOM_MODE, null))

        menuList.add("GIF")
        menuList.add(CheckMenu(appContext, "Auto Play GIF In List", AppConfig.Key.PLAY_GIF_ON_LIST, null))
        menuList.add(CheckMenu(appContext, "Click Play GIF In List", AppConfig.Key.CLICK_PLAY_GIF, null))
        menuList.add(CheckMenu(appContext, "Show GIF Flag In List", AppConfig.Key.SHOW_GIF_FLAG, null))

        menuList.add("Decode")
        menuList.add(CheckMenu(appContext, "In Prefer Quality Over Speed", AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED, null))
        menuList.add(CheckMenu(appContext, "Low Quality Bitmap", AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE, null))
        menuList.add(CheckMenu(appContext, "Enabled Thumbnail Mode In List", AppConfig.Key.THUMBNAIL_MODE, null))
        menuList.add(CheckMenu(appContext, "Cache Processed Image In Disk", AppConfig.Key.CACHE_PROCESSED_IMAGE, null))
        menuList.add(CheckMenu(appContext, "Disabled Correct Image Orientation", AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION, null))

        menuList.add("Other")
        menuList.add(CheckMenu(appContext, "Show Round Rect In Photo List", AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST, null))
        menuList.add(CheckMenu(appContext, "Show Unsplash Raw Image In Detail Page", AppConfig.Key.SHOW_UNSPLASH_RAW_IMAGE, null))
        menuList.add(CheckMenu(appContext, "Show Mapping Thumbnail In Detail Page", AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL, null))
        menuList.add(CheckMenu(appContext, "Show Press Status In List", AppConfig.Key.CLICK_SHOW_PRESSED_STATUS, null))
        menuList.add(CheckMenu(appContext, "Show Image From Corner Mark", AppConfig.Key.SHOW_IMAGE_FROM_FLAG, null))
        menuList.add(CheckMenu(appContext, "Show Download Progress In List", AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS, null))
        menuList.add(CheckMenu(appContext, "Click Show Image On Pause Download In List", AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD, null))
        menuList.add(CheckMenu(appContext, "Click Retry On Error In List", AppConfig.Key.CLICK_RETRY_ON_FAILED, null))
        menuList.add(CheckMenu(appContext, "Scrolling Pause Load Image In List", AppConfig.Key.SCROLLING_PAUSE_LOAD, null))
        menuList.add(CheckMenu(appContext, "Mobile Data Pause Download Image", AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD, null))
        menuList.add(CheckMenu(appContext, "Long Clock Show Image Info", AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO, null))

        menuList.add("Log")
        menuList.add(object : InfoMenu("Log Level") {
            override fun getInfo(): String {
                return when (SLog.getLevel()) {
                    SLog.VERBOSE -> "VERBOSE"
                    SLog.DEBUG -> "DEBUG"
                    SLog.INFO -> "INFO"
                    SLog.WARNING -> "WARNING"
                    SLog.ERROR -> "ERROR"
                    SLog.NONE -> "NONE"
                    else -> "Unknown"
                }
            }

            override fun onClick(adapter: AssemblyAdapter?) {
                AlertDialog.Builder(requireActivity()).apply {
                    setTitle("Switch Log Level")
                    val items = arrayOf("VERBOSE" + if (SLog.getLevel() == SLog.VERBOSE) " (*)" else "", "DEBUG" + if (SLog.getLevel() == SLog.DEBUG) " (*)" else "", "INFO" + if (SLog.getLevel() == SLog.INFO) " (*)" else "", "WARNING" + if (SLog.getLevel() == SLog.WARNING) " (*)" else "", "ERROR" + if (SLog.getLevel() == SLog.ERROR) " (*)" else "", "NONE" + if (SLog.getLevel() == SLog.NONE) " (*)" else "")
                    setItems(items) { _, which ->
                        when (which) {
                            0 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "VERBOSE")
                            1 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "DEBUG")
                            2 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "INFO")
                            3 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "WARNING")
                            4 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "ERROR")
                            5 -> AppConfig.putString(appContext, AppConfig.Key.LOG_LEVEL, "NONE")
                        }
                        recycler_recyclerFragment_content.adapter?.notifyDataSetChanged()
                    }
                    setPositiveButton("Cancel", null)
                }.show()
            }
        })
        menuList.add(CheckMenu(appContext, "Sync Output Log To Disk (cache/sketch_log)", AppConfig.Key.OUT_LOG_2_SDCARD, null))

        return menuList
    }
}