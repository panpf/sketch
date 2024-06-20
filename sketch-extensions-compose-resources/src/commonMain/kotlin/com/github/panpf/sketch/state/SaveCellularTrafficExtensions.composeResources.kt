package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource

/**
 * Set the error image when the save cellular traffic
 */
@Composable
fun ErrorStateImage.Builder.saveCellularTrafficError(
    resource: DrawableResource
): ErrorStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, resource)
}