package com.github.panpf.sketch.state

import androidx.compose.ui.graphics.Color


/**
 * Set the error image when the save cellular traffic
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(
    color: Color
): ErrorStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, ColorPainterStateImage(color))
}