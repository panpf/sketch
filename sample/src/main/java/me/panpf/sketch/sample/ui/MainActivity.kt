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

package me.panpf.sketch.sample.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.activity_main.*
import me.panpf.ktx.isPortraitOrientation
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.*
import me.panpf.sketch.sample.base.BaseActivity
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.event.ChangeMainPageBgEvent
import me.panpf.sketch.sample.event.CloseDrawerEvent
import me.panpf.sketch.sample.event.DrawerOpenedEvent
import me.panpf.sketch.sample.event.SwitchMainPageEvent
import me.panpf.sketch.sample.util.AnimationUtils
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator
import me.panpf.sketch.util.SketchUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@BindContentView(R.layout.activity_main)
class MainActivity : BaseActivity(), AppListFragment.GetAppListTagStripListener {

    private var lastClickBackTime: Long = 0
    private var page: Page? = null

    private val toggleDrawable by lazy {
        ActionBarDrawerToggle(this, main_drawer, main_toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  + DeviceUtils.getNavigationBarHeightByUiVisibility(this) 是为了兼容 MIX 2
        main_bgImage.updateLayoutParams {
            width = resources.displayMetrics.widthPixels
            height = resources.displayMetrics.heightPixels
            if (isPortraitOrientation()) {
                height += DeviceUtils.getWindowHeightSupplement(this@MainActivity)
            } else {
                width += DeviceUtils.getWindowHeightSupplement(this@MainActivity)
            }
        }
        main_bgImage.setOptions(ImageOptions.WINDOW_BACKGROUND)

        main_drawer.setDrawerShadow(R.drawable.shape_drawer_shadow_down_left, Gravity.START)

        // 设置左侧菜单的宽度为屏幕的 70%
        main_menuFrame.updateLayoutParams { width = (resources.displayMetrics.widthPixels * 0.7).toInt() }

        setSupportActionBar(main_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        main_pagerIndicator.setTabViewFactory(TitleTabFactory(arrayOf("APP", "PACKAGE"), baseContext))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = DeviceUtils.getStatusBarHeight(resources)
            if (statusBarHeight > 0) {
                main_contentLayout.updatePadding(top = statusBarHeight)
            } else {
                main_drawer.fitsSystemWindows = true
            }
        }

        main_drawer.setDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                toggleDrawable.onDrawerSlide(drawerView, slideOffset)
            }

            override fun onDrawerOpened(drawerView: View) {
                toggleDrawable.onDrawerOpened(drawerView)
                EventBus.getDefault().post(DrawerOpenedEvent())
            }

            override fun onDrawerClosed(drawerView: View) {
                toggleDrawable.onDrawerClosed(drawerView)
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_menuFrame, MainMenuFragment())
                .commit()
        onSwitchMainPageEvent(SwitchMainPageEvent(Page.UNSPLASH))

        ImageOrientationCorrectTestFileGenerator.getInstance(baseContext).onAppStart()

        startService(Intent(baseContext, NotificationService::class.java))

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)

        super.onDestroy()
    }

    override fun isDisableSetFitsSystemWindows(): Boolean {
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // 同步状态，这一步很重要，要不然初始
        toggleDrawable.syncState()
        toggleDrawable.onDrawerSlide(main_menuFrame, 1.0f)
        toggleDrawable.onDrawerSlide(main_menuFrame, 0.5f)
        toggleDrawable.onDrawerSlide(main_menuFrame, 0.0f)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggleDrawable.onConfigurationChanged(newConfig)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    @Subscribe
    fun onSwitchMainPageEvent(event: SwitchMainPageEvent) {
        if (this.page == event.page) {
            return
        }
        this.page = event.page

        if (page == Page.APP_LIST) {
            AnimationUtils.visibleViewByAlpha(main_pagerIndicator)
        } else {
            AnimationUtils.invisibleViewByAlpha(main_pagerIndicator)
        }

        supportActionBar!!.title = page!!.showName
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                .replace(R.id.main_contentFrame, page!!.fragment)
                .commitAllowingStateLoss()

        main_drawer.post { main_drawer.closeDrawer(Gravity.START) }
    }

    @Suppress("unused")
    @Subscribe
    fun onCloseDrawerEvent(@Suppress("UNUSED_PARAMETER") closeDrawerEvent: CloseDrawerEvent) {
        main_drawer.closeDrawer(Gravity.START)
    }

    @Suppress("unused")
    @Subscribe
    fun onChangeMainPageBgEvent(eventChange: ChangeMainPageBgEvent) {
        if (!TextUtils.isEmpty(eventChange.imageUrl)) {
            main_bgImage.displayImage(eventChange.imageUrl)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (main_drawer.isDrawerOpen(Gravity.START)) {
                main_drawer.closeDrawer(Gravity.START)
            } else {
                main_drawer.openDrawer(Gravity.START)
            }
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onGetAppListTabStrip(): PagerIndicator {
        return main_pagerIndicator
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickBackTime > 2000) {
            lastClickBackTime = currentTime
            Toast.makeText(baseContext, "再来一下就可以退出啦！", Toast.LENGTH_SHORT).show()
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
}
