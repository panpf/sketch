package me.panpf.sketch.sample.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.*
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.item.MenuTitleItem
import me.panpf.sketch.sample.item.PageMenuItem
import me.panpf.sketch.sample.util.DeviceUtils

@RegisterEvent
@BindContentView(R.layout.fragment_main)
class MainFragment : BaseFragment() {

    var page: Page? = null

    @SuppressLint("ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.updatePadding(top = DeviceUtils.getStatusBarHeight(resources))
        }

        main_recyclerMenu.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            val pages = arrayOf(
                    "Samples"
                    , Page.PHOTOS
                    , Page.UNSPLASH
                    , Page.GIF
                    , Page.APPS
                    , Page.APKS
                    , Page.SETTINGS
                    , Page.ABOUT
                    , "Test"
                    , Page.BLOCK_DISPLAY_TEST
                    , Page.IMAGE_PROCESSOR_TEST
                    , Page.IMAGE_SHAPER_TEST
                    , Page.REPEAT_LOAD_OR_DOWNLOAD_TEST
                    , Page.IN_BITMAP_TEST
                    , Page.IMAGE_ORIENTATION_TEST
                    , Page.BASE64_IMAGE_TEST
                    , Page.OTHER_TEST
            )
            adapter = AssemblyRecyclerAdapter(pages).apply {
                addItemFactory(MenuTitleItem.Factory())
                addItemFactory(PageMenuItem.Factory().setOnItemClickListener { _, _, _, _, data ->
                    val page = data ?: return@setOnItemClickListener
                    showPage(page)
                    main_drawerLayout.closeDrawer(GravityCompat.START)
                })
            }

            updateLayoutParams { width = (resources.displayMetrics.widthPixels * 0.7).toInt() }
        }

        main_drawerLayout.setDrawerShadow(R.drawable.shadow_drawer_left, GravityCompat.START)

        main_toolbar.apply {
            navigationIcon = resources.getDrawable(R.drawable.ic_menu)
            setNavigationOnClickListener {
                if (main_drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    main_drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    main_drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }

        showPage(Page.PHOTOS)
    }

    private fun showPage(newPage: Page) {
        if (this.page == newPage) return
        this.page = newPage

        main_toolbar.title = newPage.showName
        childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                .replace(R.id.main_contentFrame, newPage.fragmentClass.newInstance())
                .commitAllowingStateLoss()
    }
}

enum class Page(val showName: String, val fragmentClass: Class<out Fragment>) {
    PHOTOS("Photos", PhotosFragment::class.java),
    UNSPLASH("Unsplash", UnsplashFragment::class.java),
    GIF("GIF", GifFragment::class.java),
    APPS("Apps", AppsFragment::class.java),
    APKS("Apks", ApksFragment::class.java),
    SETTINGS("Settings", SettingsFragment::class.java),
    ABOUT("About", AboutFragment::class.java),

    BLOCK_DISPLAY_TEST("Huge Image Test", BlockDisplayTestFragment::class.java),
    IMAGE_PROCESSOR_TEST("Image Processor Test", ImageProcessorTestFragment::class.java),
    IMAGE_SHAPER_TEST("Image Shaper Test", ImageShaperTestFragment::class.java),
    REPEAT_LOAD_OR_DOWNLOAD_TEST("Repeat Load Or Download Test", RepeatLoadOrDownloadTestFragment::class.java),
    IN_BITMAP_TEST("inBitmap Test", InBitmapTestFragment::class.java),
    IMAGE_ORIENTATION_TEST("Image Orientation Test", ImageOrientationTestHomeFragment::class.java),
    BASE64_IMAGE_TEST("Base64 Image Test", Base64ImageTestFragment::class.java),
    OTHER_TEST("Other Test", OtherTestFragment::class.java);
}