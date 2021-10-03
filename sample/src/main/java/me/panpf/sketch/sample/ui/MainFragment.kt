package me.panpf.sketch.sample.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentMainBinding
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.item.MenuTitleItem
import me.panpf.sketch.sample.item.PageMenuItem
import me.panpf.sketch.sample.util.DeviceUtils

@RegisterEvent
class MainFragment : BaseBindingFragment<FragmentMainBinding>() {

    var page: Page? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentMainBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentMainBinding,
        savedInstanceState: Bundle?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.mainContentGroup.updatePadding(top = DeviceUtils.getStatusBarHeight(resources))
            binding.mainRecyclerMenu.updatePadding(top = DeviceUtils.getStatusBarHeight(resources))
        }

        binding.mainRecyclerMenu.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            val pages = arrayOf(
                "Samples",
                Page.PHOTOS,
                Page.UNSPLASH,
                Page.BAIDU_GIF,
                Page.TENOR_GIF,
                Page.APPS,
                Page.APKS,
                Page.SETTINGS,
                Page.ABOUT,
                "Test",
                Page.BLOCK_DISPLAY_TEST,
                Page.IMAGE_PROCESSOR_TEST,
                Page.IMAGE_SHAPER_TEST,
                Page.REPEAT_LOAD_OR_DOWNLOAD_TEST,
                Page.IN_BITMAP_TEST,
                Page.IMAGE_ORIENTATION_TEST,
                Page.BASE64_IMAGE_TEST,
                Page.OTHER_TEST
            )
            adapter = AssemblyRecyclerAdapter(pages).apply {
                addItemFactory(MenuTitleItem.Factory())
                addItemFactory(PageMenuItem.Factory().setOnItemClickListener { _, _, _, _, data ->
                    val page = data ?: return@setOnItemClickListener
                    showPage(page)
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
                })
            }

            updateLayoutParams { width = (resources.displayMetrics.widthPixels * 0.7).toInt() }
        }

        binding.mainDrawerLayout.setDrawerShadow(R.drawable.shadow_drawer_left, GravityCompat.START)

        binding.mainToolbar.apply {
            navigationIcon = resources.getDrawable(R.drawable.ic_menu)
            setNavigationOnClickListener {
                if (binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.mainDrawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }

        showPage(Page.PHOTOS)
    }

    private fun showPage(newPage: Page) {
        if (this.page == newPage) return
        this.page = newPage

        binding?.mainToolbar?.title = newPage.showName
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
            .replace(R.id.main_contentFrame, newPage.fragmentClass.newInstance())
            .commitAllowingStateLoss()
    }
}

enum class Page(val showName: String, val fragmentClass: Class<out Fragment>) {
    PHOTOS("Photos", PhotosFragment::class.java),
    UNSPLASH("Unsplash", UnsplashFragment::class.java),
    BAIDU_GIF("Baidu GIF", BaiduGifFragment::class.java),
    TENOR_GIF("Tenor GIF", TenorGifFragment::class.java),
    APPS("Apps", AppsFragment::class.java),
    APKS("Apks", ApksFragment::class.java),
    SETTINGS("Settings", SettingsFragment::class.java),
    ABOUT("About", AboutFragment::class.java),

    BLOCK_DISPLAY_TEST("Huge Image Test", BlockDisplayTestFragment::class.java),
    IMAGE_PROCESSOR_TEST("Image Processor Test", ImageProcessorTestFragment::class.java),
    IMAGE_SHAPER_TEST("Image Shaper Test", ImageShaperTestFragment::class.java),
    REPEAT_LOAD_OR_DOWNLOAD_TEST(
        "Repeat Load Or Download Test",
        RepeatLoadOrDownloadTestFragment::class.java
    ),
    IN_BITMAP_TEST("inBitmap Test", InBitmapTestFragment::class.java),
    IMAGE_ORIENTATION_TEST("Image Orientation Test", ImageOrientationTestHomeFragment::class.java),
    BASE64_IMAGE_TEST("Base64 Image Test", Base64ImageTestFragment::class.java),
    OTHER_TEST("Other Test", OtherTestFragment::class.java);
}