package com.github.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.MenuItemInfo

class SampleMenuListViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuList = MutableLiveData<List<MenuItemInfo<*>>>()
    val layoutMode = MutableLiveData(LayoutMode.STAGGERED_GRID)

    init {
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
            )
        ) { _, newValue ->
            layoutMode.postValue(newValue)
            menuList.postValue(menuList.value)
        })
    }

    enum class LayoutMode {
        GRID, STAGGERED_GRID, LINE
    }
}