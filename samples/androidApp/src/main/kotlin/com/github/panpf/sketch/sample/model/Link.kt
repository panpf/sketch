package com.github.panpf.sketch.sample.model

import androidx.navigation.NavDirections
import com.github.panpf.assemblyadapter.recycler.DiffKey

data class Link(
    val title: String,
    val navDirections: NavDirections,
    val minSdk: Int? = null,
    val permissions: List<String>? = null
) : DiffKey {

    override val diffKey: String = "Link-$title"
}