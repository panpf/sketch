package com.github.panpf.sketch.viewability

interface AttachAbility : Ability {

    fun onAttachedToWindow()

    fun onDetachedFromWindow()
}