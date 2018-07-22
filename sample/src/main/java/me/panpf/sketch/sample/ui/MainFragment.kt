package me.panpf.sketch.sample.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.fm_main.*
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.BuildConfig
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.event.ChangePageEvent
import me.panpf.sketch.sample.event.CloseDrawerEvent
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.util.AnimationUtils
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.util.SketchUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

@RegisterEvent
@BindContentView(R.layout.fm_main)
class MainFragment : BaseFragment(), OnActivityPostCreateCallback, AppListFragment.GetPagerIndicatorCallback {

    private lateinit var toggleDrawable: ActionBarDrawerToggle
    private var page: Page? = null

    override fun onActivityPostCreate() {
        toggleDrawable.syncState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 空出顶部的状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = DeviceUtils.getStatusBarHeight(resources)
            if (statusBarHeight > 0) {
                view.updatePadding(top = statusBarHeight)
            }
        }

        val compatActivity = activity as AppCompatActivity
        compatActivity.setSupportActionBar(mainFm_toolbar)
        compatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        compatActivity.supportActionBar?.setHomeButtonEnabled(true)

        val drawerLayout = (activity as MainFragmentCallback).getDrawerLayout()
        toggleDrawable = ActionBarDrawerToggle(compatActivity, drawerLayout, mainFm_toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggleDrawable)

        mainFm_pagerIndicator.setTabViewFactory(TitleTabFactory(arrayOf("APP", "PACKAGE"), compatActivity))

        onEvent(ChangePageEvent(Page.UNSPLASH))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggleDrawable.onConfigurationChanged(newConfig)
    }

    @Subscribe
    fun onEvent(event: ChangePageEvent) {
        if (this.page == event.page) return
        this.page = event.page

        if (page == Page.APP_LIST) {
            AnimationUtils.visibleViewByAlpha(mainFm_pagerIndicator)
        } else {
            AnimationUtils.invisibleViewByAlpha(mainFm_pagerIndicator)
        }

        val compatActivity = activity as AppCompatActivity
        compatActivity.supportActionBar!!.title = page!!.showName
        childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                .replace(R.id.mainFm_contentFrame, page!!.fragment)
                .commitAllowingStateLoss()

        EventBus.getDefault().post(CloseDrawerEvent())
    }

    override fun onGetPagerIndicator(): PagerIndicator {
        return mainFm_pagerIndicator
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

class TitleTabFactory(private val titles: Array<String>, val context: Context) : PagerIndicator.TabViewFactory {

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

interface MainFragmentCallback {
    fun getDrawerLayout(): DrawerLayout
}
