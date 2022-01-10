package com.github.panpf.sketch.sample.vm

import android.app.Application
import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.MenuItemInfo

class SampleMenuListViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuList = MutableLiveData<List<MenuItemInfo<*>>>()
    val layoutMode = MutableLiveData(LayoutMode.STAGGERED_GRID)
    val playAnimatableDrawable = MutableLiveData(true)
    val showDataFrom = MutableLiveData(false)
    val showMimeTypeLogo = MutableLiveData(true)
    val showProgressIndicator = MutableLiveData(true)

    init {
        menuList.postValue(assembleMenuList())
    }

    var showPlayMenu: Boolean = false
        set(value) {
            field = value
            menuList.postValue(assembleMenuList())
        }

    private fun assembleMenuList(): List<MenuItemInfo<*>> = buildList {
        add(MenuItemInfo(
            0,
            values = arrayOf(LayoutMode.STAGGERED_GRID, LayoutMode.GRID, LayoutMode.LINE),
            titles = null,
            iconResIds = arrayOf(
                R.drawable.ic_layout_grid_staggered,
                R.drawable.ic_layout_grid,
                R.drawable.ic_layout_line
            ),
            MenuItem.SHOW_AS_ACTION_ALWAYS
        ) { _, newValue ->
            layoutMode.postValue(newValue)
            menuList.postValue(menuList.value)
        })

        if (showPlayMenu) {
            add(MenuItemInfo(
                0,
                values = arrayOf(true, false),
                titles = null,
                iconResIds = arrayOf(
                    R.drawable.ic_play,
                    R.drawable.ic_pause,
                ),
                MenuItem.SHOW_AS_ACTION_ALWAYS
            ) { _, newValue ->
                playAnimatableDrawable.postValue(newValue)
                menuList.postValue(menuList.value)
            })
        }

        add(MenuItemInfo(
            0,
            values = arrayOf(false, true),
            titles = arrayOf("Hidden Data From", "Show Data From"),
            iconResIds = null,
            MenuItem.SHOW_AS_ACTION_NEVER
        ) { _, newValue ->
            showDataFrom.postValue(newValue)
            menuList.postValue(menuList.value)
        })

        add(MenuItemInfo(
            0,
            values = arrayOf(true, false),
            titles = arrayOf("Show MimeType Logo", "Hidden MimeType Logo"),
            iconResIds = null,
            MenuItem.SHOW_AS_ACTION_NEVER
        ) { _, newValue ->
            showMimeTypeLogo.postValue(newValue)
            menuList.postValue(menuList.value)
        })

        add(MenuItemInfo(
            0,
            values = arrayOf(true, false),
            titles = arrayOf("Show Progress Indicator", "Hidden Progress Indicator"),
            iconResIds = null,
            MenuItem.SHOW_AS_ACTION_NEVER
        ) { _, newValue ->
            showProgressIndicator.postValue(newValue)
            menuList.postValue(menuList.value)
        })
    }

    enum class LayoutMode {
        GRID, STAGGERED_GRID, LINE
    }
}