package com.github.panpf.sketch.viewability

interface LayoutAbility : Ability {
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
}