package com.github.panpf.sketch.sample.bean

import androidx.navigation.NavDirections

class NavMenuItemInfo(
    override val title: String,
    override val iconResId: Int?,
    override val showAsAction: Int,
    val navDirections: NavDirections,
) : MenuItemInfo