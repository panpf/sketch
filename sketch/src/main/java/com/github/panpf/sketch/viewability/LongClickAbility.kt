package com.github.panpf.sketch.viewability

import android.view.View

interface LongClickAbility : Ability {
    val canIntercept: Boolean
    fun onLongClick(v: View): Boolean
}