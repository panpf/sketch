package com.github.panpf.sketch.sample.model

import androidx.fragment.app.DialogFragment

class DialogFragmentItemInfo(
    override val title: String,
    override val iconResId: Int?,
    override val showAsAction: Int,
    val fragment: DialogFragment,
) : MenuItemInfo