package com.github.panpf.sketch.viewability

import android.graphics.Canvas

interface DrawAbility : Ability {
    fun onDrawBefore(canvas: Canvas)
    fun onDraw(canvas: Canvas)
    fun onDrawForegroundBefore(canvas: Canvas)
    fun onDrawForeground(canvas: Canvas)
}