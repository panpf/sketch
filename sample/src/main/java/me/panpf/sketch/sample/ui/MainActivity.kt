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

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.at_main.*
import me.panpf.sketch.sample.NotificationService
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseActivity
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.event.CloseDrawerEvent
import me.panpf.sketch.sample.event.DrawerOpenedEvent
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@RegisterEvent
@BindContentView(R.layout.at_main)
class MainActivity : BaseActivity(), MainFragmentCallback {
    override fun getDrawerLayout(): androidx.drawerlayout.widget.DrawerLayout = mainAt_drawer

    private var lastClickBackTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainAt_drawer.setDrawerShadow(R.drawable.shape_drawer_shadow_down_left, GravityCompat.START)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DeviceUtils.getStatusBarHeight(resources) <= 0) {
            mainAt_drawer.fitsSystemWindows = true
        }

        mainAt_drawer.setDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                EventBus.getDefault().post(DrawerOpenedEvent())
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        supportFragmentManager.beginTransaction()
                .replace(R.id.mainAt_menuFrame, MainMenuFragment())
                .replace(R.id.mainAt_contentFrame, MainFragment())
                .commit()

        ImageOrientationCorrectTestFileGenerator.getInstance(baseContext).onAppStart()

        startService(Intent(baseContext, NotificationService::class.java))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        supportFragmentManager.fragments.forEach { if (it is OnActivityPostCreateCallback) it.onActivityPostCreate() }
    }

    var registed = false
    override fun onStart() {
        super.onStart()
        if (!registed) {
            registed = true
            EventBus.getDefault().register(this)
        }
    }

    override fun isDisableSetFitsSystemWindows() = true

    @Suppress("unused")
    @Subscribe
    fun onEvent(@Suppress("UNUSED_PARAMETER") closeDrawerEvent: CloseDrawerEvent) {
        mainAt_drawer.closeDrawer(GravityCompat.START)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mainAt_drawer.isDrawerOpen(GravityCompat.START)) {
                mainAt_drawer.closeDrawer(GravityCompat.START)
            } else {
                mainAt_drawer.openDrawer(GravityCompat.START)
            }
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickBackTime > 2000) {
            lastClickBackTime = currentTime
            Toast.makeText(baseContext, "再按一下退出 ${getString(R.string.app_name)}", Toast.LENGTH_SHORT).show()
            return
        }

        super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            true
        }
    }
}

interface OnActivityPostCreateCallback {
    fun onActivityPostCreate()
}
