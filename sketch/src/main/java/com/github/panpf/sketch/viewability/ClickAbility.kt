package com.github.panpf.sketch.viewability

import android.view.View

interface ClickAbility : Ability {
    val canIntercept: Boolean
    fun onClick(v: View): Boolean
}