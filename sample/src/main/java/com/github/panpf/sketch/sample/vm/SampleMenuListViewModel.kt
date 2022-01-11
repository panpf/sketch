package com.github.panpf.sketch.sample.vm

import android.app.Application
import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.LayoutMode
import com.github.panpf.sketch.sample.bean.MenuItemInfo

class SampleMenuListViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuList = MutableLiveData<List<MenuItemInfo<*>>>()

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
            initValue = application1.appSettingsService.photoListLayoutMode.value!!,
            titles = null,
            iconResIds = arrayOf(
                R.drawable.ic_layout_grid_staggered,
                R.drawable.ic_layout_grid,
                R.drawable.ic_layout_line
            ),
            MenuItem.SHOW_AS_ACTION_ALWAYS
        ) { _, newValue ->
            application1.appSettingsService.photoListLayoutMode.postValue(newValue)
            menuList.postValue(menuList.value)
        })

        if (showPlayMenu) {
            add(MenuItemInfo(
                0,
                values = arrayOf(true, false),
                initValue = application1.appSettingsService.disabledAnimatableDrawableInList.value!!,
                titles = null,
                iconResIds = arrayOf(
                    R.drawable.ic_pause,
                    R.drawable.ic_play,
                ),
                MenuItem.SHOW_AS_ACTION_ALWAYS
            ) { _, newValue ->
                application1.appSettingsService.disabledAnimatableDrawableInList.postValue(
                    newValue
                )
                menuList.postValue(menuList.value)
            })
        }

        add(MenuItemInfo(
            0,
            values = arrayOf(true, false),
            initValue = application1.appSettingsService.showDataFrom.value!!,
            titles = arrayOf("Show Data From", "Hidden Data From"),
            iconResIds = null,
            MenuItem.SHOW_AS_ACTION_NEVER
        ) { _, newValue ->
            application1.appSettingsService.showDataFrom.postValue(newValue)
            menuList.postValue(menuList.value)
        })

        add(MenuItemInfo(
            0,
            values = arrayOf(true, false),
            initValue = application1.appSettingsService.showMimeTypeLogoInLIst.value!!,
            titles = arrayOf("Show MimeType Logo", "Hidden MimeType Logo"),
            iconResIds = null,
            MenuItem.SHOW_AS_ACTION_NEVER
        ) { _, newValue ->
            application1.appSettingsService.showMimeTypeLogoInLIst.postValue(newValue)
            menuList.postValue(menuList.value)
        })

        add(MenuItemInfo(
            0,
            values = arrayOf(true, false),
            initValue = application1.appSettingsService.showProgressIndicatorInList.value!!,
            titles = arrayOf("Show Progress Indicator", "Hidden Progress Indicator"),
            iconResIds = null,
            MenuItem.SHOW_AS_ACTION_NEVER
        ) { _, newValue ->
            application1.appSettingsService.showProgressIndicatorInList.postValue(newValue)
            menuList.postValue(menuList.value)
        })

        add(MenuItemInfo(
            0,
            values = arrayOf(true, false),
            initValue = application1.appSettingsService.saveCellularTrafficInList.value!!,
            titles = arrayOf("Enabled Save Cellular Traffic", "Disabled Save Cellular Traffic"),
            iconResIds = null,
            MenuItem.SHOW_AS_ACTION_NEVER
        ) { _, newValue ->
            application1.appSettingsService.saveCellularTrafficInList.postValue(newValue)
            menuList.postValue(menuList.value)
        })
    }
}