package com.github.panpf.sketch.sample.ui.model

import android.os.Parcelable
import com.github.panpf.assemblyadapter.recycler.DiffKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ViewTestGroup(val title: String, var clickCount: Int = 0) : DiffKey, Parcelable {

    @IgnoredOnParcel
    override val diffKey: String = "ViewTestGroup-$title"
}