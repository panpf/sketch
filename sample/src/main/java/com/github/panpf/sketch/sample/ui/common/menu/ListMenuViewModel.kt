/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.sample.ui.common.menu

import android.app.Application
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.model.LayoutMode
import com.github.panpf.sketch.sample.model.MenuItemInfoGroup
import com.github.panpf.sketch.sample.model.NavMenuItemInfo
import com.github.panpf.sketch.sample.model.SwitchMenuItemInfo
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.ui.setting.Page
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListMenuViewModel(
    application1: Application,
    private val showLayoutModeMenu: Boolean,
    private val showPlayMenu: Boolean
) : LifecycleAndroidViewModel(application1) {

    class Factory(
        val application1: Application,
        val showLayoutModeMenu: Boolean,
        val showPlayMenu: Boolean
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ListMenuViewModel(application1, showLayoutModeMenu, showPlayMenu) as T
        }
    }

    private val _menuFlow = MutableStateFlow(assembleMenuList())
    val menuFlow: StateFlow<List<MenuItemInfoGroup>> = _menuFlow

    private fun assembleMenuList(): List<MenuItemInfoGroup> = buildList {
        add(MenuItemInfoGroup(buildList {
            if (showPlayMenu) {
                add(SwitchMenuItemInfo(
                    values = arrayOf(true, false),
                    initValue = application1.prefsService.disallowAnimatedImageInList.value,
                    titles = null,
                    iconResIds = arrayOf(
                        R.drawable.ic_pause,
                        R.drawable.ic_play,
                    ),
                    showAsAction = MenuItem.SHOW_AS_ACTION_ALWAYS
                ) { _, newValue ->
                    application1.prefsService.disallowAnimatedImageInList.value = newValue
                    _menuFlow.value = assembleMenuList()
                })
            }

            if (showLayoutModeMenu) {
                add(SwitchMenuItemInfo(
                    values = arrayOf(
                        LayoutMode.GRID.toString(),
                        LayoutMode.STAGGERED_GRID.toString(),
                    ),
                    initValue = application1.prefsService.photoListLayoutMode.value,
                    titles = null,
                    iconResIds = arrayOf(
                        R.drawable.ic_layout_grid,
                        R.drawable.ic_layout_grid_staggered,
                    ),
                    showAsAction = MenuItem.SHOW_AS_ACTION_ALWAYS
                ) { _, newValue ->
                    application1.prefsService.photoListLayoutMode.value = newValue
                    _menuFlow.value = assembleMenuList()
                })
            }

            add(
                NavMenuItemInfo(
                    title = "Settings",
                    iconResId = R.drawable.ic_settings,
                    showAsAction = MenuItem.SHOW_AS_ACTION_ALWAYS,
                    navDirections = MainFragmentDirections.actionGlobalSettingsDialogFragment(Page.LIST.name)
                )
            )
        }))
    }
}